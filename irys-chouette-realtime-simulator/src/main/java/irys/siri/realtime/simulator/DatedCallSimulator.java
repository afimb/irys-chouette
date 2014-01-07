package irys.siri.realtime.simulator;

import irys.siri.realtime.dao.DatedCallDao;
import irys.siri.realtime.dao.DatedVehicleJourneyDao;
import irys.siri.realtime.model.DatedCallNeptune;
import irys.siri.realtime.model.DatedVehicleJourneyNeptune;
import irys.siri.realtime.model.type.VisitStatus;

import java.sql.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

public class DatedCallSimulator extends AbstractSimulator
{
	private static Logger logger = Logger.getLogger(DatedCallSimulator.class);

	// @Setter private ChouetteTool chouetteTool;
	@Getter @Setter private INeptuneManager<Timetable> timetableManager;
	@Getter @Setter private DatedVehicleJourneyDao dvjDAO;
	@Getter @Setter private DatedCallDao dcDAO;
	@Getter @Setter private int igap = 120;
	@Getter @Setter private int earlyGap = 30;
	@Getter @Setter private int delayedGap = 90;
	@Getter @Setter private String gapType = "random";

	// private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	@Override
	public void produceData() throws ChouetteException 
	{
		// load timetables
		List<Timetable> timetables = timetableManager.getAll(null);
		Calendar c = Calendar.getInstance();
		Date date = new Date(c.getTime().getTime());
		logger.info("timetables count = "+timetables.size()); 
		int dvjCount = 0;
		int dcCount = 0;
		// loop on each 
		for (Iterator<Timetable> iterator = timetables.iterator(); iterator.hasNext();) 
		{
			Timetable timetable = iterator.next();

			logger.info("proceed tm"+timetable.getComment()); 
			// if active today
			if (timetable.isActiveOn(date) )
			{
				logger.info("  tm active today"); 
				if ( timetable.getVehicleJourneys() != null)
				{
					// loop on each vehiclejourney
					logger.info("  proceed for "+timetable.getVehicleJourneys().size()+" vehicle journeys"); 
					for (VehicleJourney vj : timetable.getVehicleJourneys()) 
					{
						if (vj.getVehicleJourneyAtStops() == null || vj.getVehicleJourneyAtStops().isEmpty()) continue;
						int dcC = buildDvj(vj, c);
						if (dcC > 0)
						{
							dcCount += dcC;
							dvjCount++;
						}

					}
					// end loop
				}
				else
				{
					logger.info("  no vehicleJourneys for this timetable"); 
				}
			}
			// end if
			iterator.remove(); // clean for garbaging
		}
		// end loop
		logger.info("DatedVehicleJourneys produced : "+dvjCount);
		logger.info("DatedCalls produced : "+dcCount);
		logger.info("generation ended");

	}

	private int buildDvj(VehicleJourney vj, Calendar c) 
	{
		Session session = SessionFactoryUtils.getSession(getSiriSessionFactory(), true);
		TransactionSynchronizationManager.bindResource(getSiriSessionFactory(), new SessionHolder(session));
		int dcCount = 0;
		try
		{
			DatedVehicleJourneyNeptune dvj = dvjDAO.get(vj.getObjectId(), c);
			// if datedvj not present, create it
			if (dvj == null)
			{
				// logger.debug("create DatedVehicleJourney "+vj.getObjectId()+" on "+formater.format(c.getTime()));
				dvj = new DatedVehicleJourneyNeptune(vj);
				dvj.setOriginAimedDepartureTime(c);
				dvj.setLastModificationTime(Calendar.getInstance());
				dvjDAO.save(dvj);
				// generate random RT data
				int sgap = getRandomGap();
				VisitStatus status = VisitStatus.onTime;
				if (sgap >= earlyGap) status = VisitStatus.early;
				else if (sgap <= -delayedGap) status = VisitStatus.delayed;
				DatedCallNeptune previousDC = null;
				vj.sortVehicleJourneyAtStops();
				int position  = 0;
				for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) 
				{
					DatedCallNeptune dc = new DatedCallNeptune(dvj,vjas,previousDC);
					Calendar cal = dc.getExpectedDepartureTime();
					cal.add(Calendar.SECOND, sgap);
					dc.setExpectedDepartureTime(cal);
					cal = dc.getExpectedArrivalTime();
					cal.add(Calendar.SECOND, sgap);
					dc.setExpectedArrivalTime(cal);	
					dc.setDepartureStatus(status);
					dc.setArrivalStatus(status);
					dc.setPosition(position++);
					dcDAO.save(dc);
					previousDC = dc;
					// logger.debug("   add DatedCall "+dc.getStopPointNeptuneRef()+" on "+formater.format(dc.getExpectedDepartureTime().getTime()));
					dcCount++;
				}
				session.flush();
			}
		}
		finally
		{
			TransactionSynchronizationManager.unbindResource(getSiriSessionFactory());
			SessionFactoryUtils.closeSession(session);
		}
		return dcCount;
	}

	private int getRandomGap()
	{
		if (igap == 0 || gapType.equalsIgnoreCase("fixed")) return igap;
		int gap =  (int)(getRandom().nextGaussian()*(double)igap);
		return gap;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}


}
