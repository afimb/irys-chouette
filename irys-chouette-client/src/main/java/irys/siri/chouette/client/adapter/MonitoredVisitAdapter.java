/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.chouette.client.adapter;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import lombok.Setter;
import irys.common.SiriException;
import irys.siri.chouette.ChouetteTool;
import irys.siri.chouette.Referential;
import irys.siri.realtime.model.DatedCallNeptune;
import irys.siri.realtime.model.DatedVehicleJourneyNeptune;
import irys.siri.realtime.model.MonitoredVisit;

/**
 *
 * @author marc
 */
public class MonitoredVisitAdapter {
	private static Logger logger = Logger.getLogger(MonitoredVisitAdapter.class);

	@Setter private ChouetteTool siriTool; 	
	@Setter private Referential referential;

	public MonitoredVisitAdapter() {};

	public String vehicleJourneyNeptuneRef( String vehicleJourneyRef)
	{
		if (siriTool == null) throw new RuntimeException("siriTool not set");
		try 
		{
			return siriTool.toNeptuneId(vehicleJourneyRef, ChouetteTool.ID_VEHICLEJOURNEY, NeptuneIdentifiedObject.VEHICLEJOURNEY_KEY);
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+vehicleJourneyRef);
			return vehicleJourneyRef;
		}
	}

	public DatedVehicleJourneyNeptune read( MonitoredVisit visit)
	{
		DatedVehicleJourneyNeptune dvj = new DatedVehicleJourneyNeptune();
		dvj.setDatedVehicleJourneyRef( vehicleJourneyNeptuneRef( visit.getDatedVehicleJourneyRef()));
		// TODO: use OriginAimedDepartureTime when method will be defined on MonitoredVisit
		dvj.setLineId(getLineNeptuneRef(visit.getLineRef()));
		dvj.setOriginAimedDepartureTime( visit.getAimedDepartureTime());
		return dvj;
	}

	public Long getLineNeptuneRef( String lineRef) {
		try 
		{
			String oid = siriTool.toNeptuneId(lineRef, ChouetteTool.ID_LINE, NeptuneIdentifiedObject.LINE_KEY);
			if (oid != null)
			{
				Line area = referential.getLine(oid);
				if (area != null)
				{
					return area.getId();
				}
			}
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+lineRef);
			return null;
		}
		logger.warn("invalid id syntax "+lineRef);
		return null;
	}

	public DatedCallNeptune read( Long datedVehicleJourneyId, MonitoredVisit visit)
	{
		DatedCallNeptune datedCall = new DatedCallNeptune();
		datedCall.setStopPointNeptuneId( getStopPointNeptuneRef(visit.getStopPointRef()));
		datedCall.setDatedVehicleJourneyId( datedVehicleJourneyId);

		updateDatedCall( datedCall, visit);
		return datedCall;
	}

	public Long getStopPointNeptuneRef( String stopPointRef) 
	{
		try 
		{
			if (stopPointRef.contains(":SPOR:"))
			{
				String oid = siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPPOINT_KEY);
				if (oid != null)
				{
					StopPoint point = referential.getStopPoint(oid);
					if (point != null)
					{
						return point.getContainedInStopArea().getId();
					}
				}
			}
			else
			{
				String oid = siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPAREA_KEY);
				if (oid != null)
				{
					StopArea area = referential.getStopArea(oid);
					if (area != null)
					{
						return area.getId();
					}
				}
			}
		} 
		catch (SiriException e) 
		{
			logger.warn("invalid id syntax "+stopPointRef);
			return null;
		}
		logger.warn("invalid id syntax "+stopPointRef);
		return null;
	}

	public void updateDatedCall( DatedCallNeptune datedCall, MonitoredVisit visit)
	{
		datedCall.setArrivalStatus( visit.getArrivalStatus());
		datedCall.setDepartureStatus( visit.getDepartureStatus());
		datedCall.setExpectedArrivalTime( visit.getExpectedArrivalTime());
		datedCall.setExpectedDepartureTime( visit.getExpectedDepartureTime());
	}
}
