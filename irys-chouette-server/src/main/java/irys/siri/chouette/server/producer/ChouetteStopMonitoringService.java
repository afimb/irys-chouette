/**
 *   Siri Product - Produit SIRI
 *  
 *   a set of tools for easy application building with 
 *   respect of the France Siri Local Agreement
 *
 *   un ensemble d'outils facilitant la realisation d'applications
 *   respectant le profil France de la norme SIRI
 * 
 *   Copyright DRYADE 2009-2010
 */
package irys.siri.chouette.server.producer;

import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import irys.common.SiriException;
import irys.common.SiriTool;
import irys.siri.chouette.ChouetteTool;
import irys.siri.chouette.Referential;
import irys.siri.chouette.server.model.DatedCall;
import irys.siri.chouette.server.model.DetailLevelEnum;
import irys.siri.chouette.server.model.RealTimeDao;
import irys.siri.chouette.server.model.Vehicle;
import irys.siri.server.producer.service.AbstractStopMonitoringService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.GDuration;
import org.w3.xml.x1998.namespace.LangAttribute.Lang;

import irys.uk.org.siri.siri.ContextualisedRequestStructure;
import irys.uk.org.siri.siri.CoordinatesStructure;
import irys.uk.org.siri.siri.DataFrameRefStructure;
import irys.uk.org.siri.siri.DestinationRefStructure;
import irys.uk.org.siri.siri.FramedVehicleJourneyRefStructure;
import irys.uk.org.siri.siri.JourneyPatternRefStructure;
import irys.uk.org.siri.siri.JourneyPlaceRefStructure;
import irys.uk.org.siri.siri.LineRefStructure;
import irys.uk.org.siri.siri.LocationStructure;
import irys.uk.org.siri.siri.MonitoredCallStructure;
import irys.uk.org.siri.siri.MonitoredStopVisitStructure;
import irys.uk.org.siri.siri.MonitoredVehicleJourneyStructure;
import irys.uk.org.siri.siri.MonitoringRefStructure;
import irys.uk.org.siri.siri.NaturalLanguagePlaceNameStructure;
import irys.uk.org.siri.siri.NaturalLanguageStringStructure;
import irys.uk.org.siri.siri.OnwardCallStructure;
import irys.uk.org.siri.siri.OnwardCallsStructure;
import irys.uk.org.siri.siri.OperatorRefStructure;
import irys.uk.org.siri.siri.ProgressStatusEnumeration;
import irys.uk.org.siri.siri.RouteRefStructure;
import irys.uk.org.siri.siri.StopMonitoringDeliveriesStructure;
import irys.uk.org.siri.siri.StopMonitoringDeliveryStructure;
import irys.uk.org.siri.siri.StopMonitoringRequestStructure;
import irys.uk.org.siri.siri.StopPointRefStructure;
import irys.uk.org.siri.siri.StopVisitTypeEnumeration;
import irys.uk.org.siri.siri.VehicleModesEnumeration;



public class ChouetteStopMonitoringService extends AbstractStopMonitoringService
{
	/**
	 * Logger for this class 
	 */
	private static final Logger logger = Logger.getLogger(ChouetteStopMonitoringService.class);

	@Setter private String stopIdSubType = "SPOR";

	@Setter private String terminusIdSubType = "BP";

	@Setter private String monitoringIdSubType = "RequestedRef";

	@Setter private ChouetteTool chouetteTool;

	@Setter private Referential referential;

	@Setter private RealTimeDao realTimeDao;

	@Setter private int delayedGap;

	@Setter private int earlyGap;

	private DetailLevelEnum defaultDetailLevel = DetailLevelEnum.full;

	private static final DateFormat timeFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


	/**
	 * 
	 */
	public ChouetteStopMonitoringService() throws SiriException
	{
		// chargement des parametres 
		super();

	}

	public void init()
	{
		super.init();
	}

	public void setDefaultDetailLevel(String level)
	{
		defaultDetailLevel = DetailLevelEnum.forString(level);
	}

	public Logger getLogger()
	{
		return logger;
	}

	/* (non-Javadoc)
	 * @see uk.org.siri.soapimpl.StopMonitoringServiceInterface#getStopMonitoringDeliveries(uk.org.siri.www.StopMonitoringRequestStructure)
	 */
	public StopMonitoringDeliveriesStructure getStopMonitoringDeliveries(ContextualisedRequestStructure serviceRequestInfo,StopMonitoringRequestStructure request,Calendar responseTimestamp) throws SiriException
	{
		// indicateurs pour les filtres sans valeur par defaut
		boolean hasLineFilter = false;
		boolean hasDestinationFilter = false;
		boolean hasMaximumStopVisitsFilter = false;
		boolean hasMinimumStopVisitsPerLineFilter = false;
		boolean hasOnWardCallsFilter = false;

		// long methodStart = System.currentTimeMillis();

		String stopSiriId = request.getMonitoringRef().getStringValue();
		String stopKey = null;
		List<StopPoint> requestedStops = new ArrayList<StopPoint>();

		if (stopSiriId.contains(":SPOR:"))
		{
			StopPoint point = referential.getStopPointFromSiri(stopSiriId);
			if (point == null)
				throw new SiriException(SiriException.Code.BAD_ID,"unknown MonitoringRef "+stopSiriId);
			stopKey = point.getObjectId();
			requestedStops.add(point);
		}
		else
		{
			StopArea area = referential.getStopAreaFromSiri(stopSiriId);
			if (area == null)
				throw new SiriException(SiriException.Code.BAD_ID,"unknown MonitoringRef "+stopSiriId);
			stopKey = area.getObjectId();
			requestedStops.addAll(collectStopPoints(area));
		}

		logger.info("Asked StopPoint = "+stopKey +" ("+stopSiriId+")");

		// analyse des filtres eventuels
		Long lineKey = null;
		String lineId = "";
		if (request.isSetLineRef())
		{
			lineId = request.getLineRef().getStringValue();

			if (lineId != null )
			{
				Line line = referential.getLineFromSiri(lineId);
				if (line == null)
					throw new SiriException(SiriException.Code.BAD_ID,"unknown LineRef "+lineId);
				lineKey = line.getId();
				logger.info("Line filter = "+lineKey +" ("+lineId+")");
				hasLineFilter = true;
			}
		}

		List<StopPoint> destinations = new ArrayList<StopPoint>() ;

		String destinationId = "";
		if (request.isSetDestinationRef())
		{
			String destinationKey = "";
			destinationId = request.getDestinationRef().getStringValue();

			if (destinationId != null )
			{
				if (destinationId.contains(":SPOR:"))
				{
					StopPoint point = referential.getStopPointFromSiri(destinationId);
					if (point == null)
						throw new SiriException(SiriException.Code.BAD_ID,"unknown DestinationRef "+destinationId);
					destinations.add(point);
					destinationKey = point.getObjectId();
				}
				else
				{
					StopArea area = referential.getStopAreaFromSiri(destinationId);
					if (area == null)
						throw new SiriException(SiriException.Code.BAD_ID,"unknown DestinationRef "+destinationId);
					destinations.addAll(collectStopPoints(area));
					destinationKey = area.getObjectId();
				}

				logger.info("Destination filter = "+destinationKey +" ("+destinationId+")");
				hasDestinationFilter = true;
			}

		}

		Calendar startTime = Calendar.getInstance(); // par defaut : heure courante 
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MILLISECOND, 0);
		if (request.isSetStartTime())
		{
			Calendar t = request.getStartTime();
			startTime.set(Calendar.HOUR_OF_DAY, t.get(Calendar.HOUR_OF_DAY));
			startTime.set(Calendar.MINUTE, t.get(Calendar.MINUTE));
		}
		String formatedStartTime = timeFormater.format(startTime.getTime());
		logger.info("effective startTime = "+formatedStartTime );

		Calendar endTime = null;
		if (request.isSetPreviewInterval())
		{
			GDuration previewInterval = request.getPreviewInterval();     
			endTime = (Calendar)startTime.clone();
			endTime.add(Calendar.HOUR_OF_DAY, previewInterval.getHour());
			endTime.add(Calendar.MINUTE, previewInterval.getMinute());
			// securite sur fin de journee !!
			if (endTime.get(Calendar.DAY_OF_YEAR) == startTime.get(Calendar.DAY_OF_YEAR))
			{
				if (endTime.before(startTime) || endTime.equals(startTime))
				{
					throw new SiriException(SiriException.Code.BAD_PARAMETER,"PreviewInterval cannot be negative or zero");               
				}
				// String formatedEndTime = timeFormater.format(endTime.getTime());
				logger.info("previewInterval = "+previewInterval.getHour()+" h " +previewInterval.getMinute()+" mn");
				// hasPreviewIntervalFilter = true;
			}
		}

		String operatorId = null;
		Long companyKey = null;
		if (request.isSetOperatorRef())
		{
			operatorId = request.getOperatorRef().getStringValue();

			if (operatorId != null )
			{
				Company company = referential.getCompanyFromSiri(operatorId);
				if (company == null)
					throw new SiriException(SiriException.Code.BAD_ID,"unknown OperatorRef "+lineId);
				companyKey = company.getId();
				logger.info("Operator filter = "+companyKey +" ("+operatorId+")");
				// hasOperatorFilter = true;
			}

		}

		StopVisitTypeEnumeration.Enum stopVisitTypes = StopVisitTypeEnumeration.DEPARTURES; //valeur par defaut : departs
		// filtre les info renseignees horaires depart et/ou arrivees 
		if (request.isSetStopVisitTypes())
		{
			stopVisitTypes = request.getStopVisitTypes();
		}

		boolean filterOnDeparture = !(stopVisitTypes.equals(StopVisitTypeEnumeration.ARRIVALS));
		logger.info("effective stopVisitTypes filter = "+stopVisitTypes.toString());

		int minimumStopVisitsPerLine = 0; // valeur par defaut
		if (request.isSetMinimumStopVisitsPerLine())
		{
			if (hasLineFilter || hasDestinationFilter)
			{
				//  positionner une erreur parametre ignore (optionnel)
				logger.info("MinimumStopVisitsPerLine filter rejected");
			}
			else
			{
				BigInteger bi = request.getMinimumStopVisitsPerLine();
				minimumStopVisitsPerLine = bi.intValue();
				if (minimumStopVisitsPerLine <= 0)
				{
					throw new SiriException(SiriException.Code.BAD_PARAMETER,"MinimumStopVisitsPerLine cannot be negative or zero");
				}
				logger.info("MinimumStopVisitsPerLine filter = "+minimumStopVisitsPerLine);
				hasMinimumStopVisitsPerLineFilter = true;
			}
		}

		int maximumStopVisits = 0; // valeur par defaut : 0 pour tout
		if (request.isSetMaximumStopVisits())
		{
			if (hasMinimumStopVisitsPerLineFilter)
			{
				//  positionner une erreur parametre ignore (optionnel)
				logger.info("MaximumStopVisits filter rejected");
			}
			else
			{
				BigInteger bi = request.getMaximumStopVisits();

				maximumStopVisits = bi.intValue();
				if (maximumStopVisits <= 0)
				{
					throw new SiriException(SiriException.Code.BAD_PARAMETER,"MaximumStopVisits cannot be negative or zero");
				}
				logger.info("MaximumStopVisits filter = "+maximumStopVisits);
				hasMaximumStopVisitsFilter = true;
			}
		}

		int onWardCalls = 0 ; // valeur par defaut non traitee
		if (request.isSetMaximumNumberOfCalls())
		{
			if (request.getMaximumNumberOfCalls().isSetOnwards())
			{
				BigInteger bi =  request.getMaximumNumberOfCalls().getOnwards();
				onWardCalls = bi.intValue();
				if (onWardCalls < 0)
				{
					throw new SiriException(SiriException.Code.BAD_PARAMETER,"MaximumNumberOfCalls cannot be negative");
				}
				logger.info("MaximumNumberOfCalls filter = "+onWardCalls);
				hasOnWardCallsFilter = true;
			}
		}

		// check presence of Detail Level
		DetailLevelEnum detailLevel = defaultDetailLevel;
		if (request.isSetStopMonitoringDetailLevel())
		{
			detailLevel = DetailLevelEnum.forStopMonitoringDetailLevel(request.getStopMonitoringDetailLevel());
		}

		// prepare response
		StopMonitoringDeliveriesStructure response = StopMonitoringDeliveriesStructure.Factory.newInstance();
		StopMonitoringDeliveryStructure delivery = response.addNewStopMonitoringDelivery();
		delivery.setVersion(request.getVersion());

		delivery.setStatus(true); 
		delivery.setResponseTimestamp(responseTimestamp);

		// prefiltrage sur les lignes et les destinations
		{
			String errorMessage = "No line reaches this StopPoint";
			for (Iterator<StopPoint> iterator = requestedStops.iterator(); iterator.hasNext();) 
			{
				StopPoint point = iterator.next();

				if (hasLineFilter)
				{
					if (!point.getRoute().getLine().getId().equals(lineKey))
					{
						iterator.remove();
						errorMessage = "MonitoringRef and LineRef mismatch PTNetwork structure";
					}           
				}
			}

			if (!requestedStops.isEmpty() && hasDestinationFilter)
			{
				reduce(requestedStops,destinations);
				if (destinations.isEmpty() )
				{
					errorMessage = "MonitoringRef and DestinationRef mismatch PTNetwork structure";
				}
			}

			if (requestedStops.isEmpty())
			{
				throw new SiriException(SiriException.Code.BAD_ID,errorMessage);
			}
		}

		try 
		{

			Date activeDate = new Date(Calendar.getInstance().getTimeInMillis());

			List<Long> requestedStopIds = NeptuneIdentifiedObject.extractIds(requestedStops);
			List<Long> destinationIds = NeptuneIdentifiedObject.extractIds(destinations);
			List<DatedCall> journeys = realTimeDao.getCalls(activeDate, requestedStopIds, startTime, endTime, filterOnDeparture, lineKey, companyKey, destinationIds, maximumStopVisits);

			if (hasMaximumStopVisitsFilter && journeys.size() > maximumStopVisits)
			{
				journeys = journeys.subList(0, maximumStopVisits);
			}
			else if (hasMinimumStopVisitsPerLineFilter)
			{
				Map<String,List<DatedCall>> map = new HashMap<String, List<DatedCall>>();
				for (DatedCall datedCall : journeys)
				{
					StopPoint point = referential.getStopPoint(datedCall.getStopPointId());
					String line = point.getRoute().getLine().getObjectId();
					List<DatedCall> lineJourney = map.get(line);
					if (lineJourney == null)
					{
						lineJourney = new ArrayList<DatedCall>();
						map.put(line, lineJourney);
					}
					if (lineJourney.size() >= minimumStopVisitsPerLine) continue;
					lineJourney.add(datedCall);
				}
				journeys.clear();
				for (List<DatedCall> list : map.values())
				{
					journeys.addAll(list);
				}
			}

			Map<String, MonitoredVehicleJourneyStructure> monitoredVehicleJourneyMap = new HashMap<String, MonitoredVehicleJourneyStructure>();

			for (DatedCall datedCall : journeys)
			{

				// logger.debug("prepare response on "+datedCall);
				if (datedCall ==  null)
					throw new SiriException(SiriException.Code.INTERNAL_ERROR,"journey nulle");
				if (datedCall.getVehicleJourney() == null)
				{
					logger.info("horaire sans course :"+datedCall.getStopPointId());
					continue;
				}

				//				if (datedCall.getVehicleJourney().getJourneyPatternId() == null)
				//				{
				//					logger.info("course sans mission:"+datedCall.getVehicleJourney().getId());
				//					continue;
				//				}

				MonitoredStopVisitStructure monitoredStopVisit = delivery.addNewMonitoredStopVisit();

				StopPoint point = referential.getStopPoint(datedCall.getStopPointId());
				String cleArret = point.getObjectId();
				String cleCourse = datedCall.getVehicleJourney().getObjectId();

				// habillage du resultat
				Calendar recordedAtTime = Calendar.getInstance();
				if (datedCall.getLastModificationDate() != null)
				{
					recordedAtTime.setTimeInMillis(datedCall.getLastModificationDate().getTime());
				}
				else if (datedCall.getVehicleJourney().getLastModificationDate() != null)
				{
					recordedAtTime.setTimeInMillis(datedCall.getVehicleJourney().getLastModificationDate().getTime());

				}
				monitoredStopVisit.setRecordedAtTime(recordedAtTime);
				monitoredStopVisit.setItemIdentifier(cleArret+"-"+cleCourse);
				MonitoringRefStructure monitoringRef = monitoredStopVisit.addNewMonitoringRef();
				if (monitoringIdSubType.equals("RequestedRef"))
				{
					monitoringRef.setStringValue(stopSiriId);
				}
				else if (monitoringIdSubType.equals("SPOR"))
				{
					monitoringRef.setStringValue(chouetteTool.toSiriId(point.getObjectId(),SiriTool.ID_STOPPOINT,null));
				}
				else if (monitoringIdSubType.equals("BP"))
				{
					monitoringRef.setStringValue(chouetteTool.toSiriId(point.getContainedInStopArea().getObjectId(),SiriTool.ID_STOPPOINT,point.getContainedInStopArea().getAreaType()));
				}
				else // SP
				{
					if (point.getContainedInStopArea().getParent() != null)
						monitoringRef.setStringValue(chouetteTool.toSiriId(point.getContainedInStopArea().getParent().getObjectId(),SiriTool.ID_STOPPOINT,point.getContainedInStopArea().getParent().getAreaType()));
					else
						monitoringRef.setStringValue(chouetteTool.toSiriId(point.getContainedInStopArea().getObjectId(),SiriTool.ID_STOPPOINT,point.getContainedInStopArea().getAreaType()));
				}

				MonitoredVehicleJourneyStructure monitoredVehicleJourney = monitoredStopVisit.addNewMonitoredVehicleJourney();

				// sauvegarde pour les onwards eventuels
				monitoredVehicleJourneyMap.put(cleCourse, monitoredVehicleJourney);

				Line line = point.getRoute().getLine();
				LineRefStructure lineRef = monitoredVehicleJourney.addNewLineRef();

				lineRef.setStringValue(chouetteTool.toSiriId(line.getObjectId(),SiriTool.ID_LINE));

				FramedVehicleJourneyRefStructure vehiculeJourneyRef = monitoredVehicleJourney.addNewFramedVehicleJourneyRef();
				DataFrameRefStructure dataFrameRef = vehiculeJourneyRef.addNewDataFrameRef();


				String dataFrame = "undefined";
				if (line.getPtNetwork() != null)
				{
					if (line.getPtNetwork().getVersionDate() != null)
					{
						dataFrame = line.getPtNetwork().getName()+":"+line.getPtNetwork().getVersionDate();
					}
				}

				// obligatoire (meme si la spec ne le dit pas)
				dataFrameRef.setStringValue(dataFrame);
				// obligatoire (meme si la spec ne le dit pas)
				vehiculeJourneyRef.setDatedVehicleJourneyRef(chouetteTool.toSiriId(datedCall.getVehicleJourney().getObjectId(),SiriTool.ID_VEHICLEJOURNEY));

				if (detailLevel.isValidFor(DetailLevelEnum.basic))
				{
					// debut VehicleJourneyInfoGroup 
					populateVehicleJourneyInfoGroup(datedCall,point, monitoredVehicleJourney,detailLevel);
					// fin
				}
				if (detailLevel.isValidFor(DetailLevelEnum.normal))
				{
					// debut JourneyPatternInfoGroup
					populateJourneyPatternInfoGroup(datedCall,point, monitoredVehicleJourney,detailLevel);
					// fin JourneyPatternInfoGroup
					// debut DisruptionGroup
					populateDisruptionGroup(monitoredVehicleJourney,detailLevel);
					// fin DisruptionGroup
					// debut JourneyProgressInfoGroup
					populateJourneyProgressInfoGroup(datedCall,monitoredVehicleJourney,detailLevel);
					// fin JourneyProgressInfoGroup
				}
				// debut MonitoredCall
				populateMonitoredCall(datedCall,point, stopVisitTypes, monitoredVehicleJourney,detailLevel);
				// fin MonitoredCall

			}
			if (detailLevel.isValidFor(DetailLevelEnum.basic))
			{
				// origin/destination passing time 
				if (!journeys.isEmpty())
				{
					Set<Long> ids = new HashSet<Long>();

					Map<Long,String> vjKeyById = new HashMap<Long, String>();
					for (DatedCall datedCall : journeys)
					{
						ids.add(datedCall.getVehicleJourney().getId());
						vjKeyById.put(datedCall.getVehicleJourney().getId(), datedCall.getVehicleJourney().getObjectId());
					}
					List<DatedCall> oriDestCalls = realTimeDao.getOriginDestinationCalls(ids);
					for (DatedCall datedCall : oriDestCalls) 
					{
						String cleCourse = vjKeyById.get(datedCall.getDatedVehicleJourneyId());
						if (cleCourse == null)
						{
							continue;
						}
						MonitoredVehicleJourneyStructure monitoredVehicleJourney = monitoredVehicleJourneyMap.get(cleCourse);
						if (monitoredVehicleJourney != null)
						{
							if (datedCall.isDeparture())
							{
								monitoredVehicleJourney.setOriginAimedDepartureTime(convertToCalendar(datedCall.getAimedArrivalTime()));
							}
							else if (datedCall.isArrival())
							{
								monitoredVehicleJourney.setDestinationAimedArrivalTime(convertToCalendar(datedCall.getAimedArrivalTime()));
							}
						}
					}
				}
			}

			// Onward en post traitement
			if (!journeys.isEmpty() && hasOnWardCallsFilter)
			{
				for (DatedCall datedCall : journeys)
				{
					// Onward 
					String cleCourse = datedCall.getVehicleJourney().getObjectId();
					MonitoredVehicleJourneyStructure monitoredVehicleJourney = monitoredVehicleJourneyMap.get(cleCourse);
					if (monitoredVehicleJourney != null)
					{
						addOnwards(monitoredVehicleJourney,datedCall,stopVisitTypes,onWardCalls,detailLevel);
					}
				}
			}
			//			{
			//				long checkPoint = System.currentTimeMillis();
			//				long methodDuration = checkPoint - methodStart;
			//				logger.info("method returns : duration = "+chouetteTool.getTimeAsString(methodDuration));
			//				methodStart = checkPoint;
			//			}

		}
		catch (SiriException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			logger.error(e.getMessage(),e);
			throw new SiriException(SiriException.Code.INTERNAL_ERROR,e.getMessage());
		}


		return response;


	}

	private void reduce(List<StopPoint> requestedStops,
			List<StopPoint> destinations) 
	{
		// check if stoppoints run to at least one destination
		List<StopPoint> validDest = new ArrayList<StopPoint>();
		for (Iterator<StopPoint> iterator = requestedStops.iterator(); iterator.hasNext();) 
		{
			StopPoint stopPoint = iterator.next();
			boolean valid = false;
			Route route = stopPoint.getRoute();
			for (JourneyPattern jp : route.getJourneyPatterns()) 
			{
				StopPoint dest = getDestination(jp);
				if (dest != null && destinations.contains(dest)) 
				{
					validDest.add(dest);
					valid = true;
				}
			}
			if (!valid) iterator.remove();
		}
		// mainatains only valid destinations
		destinations.clear();
		destinations.addAll(validDest);
	}


	private StopPoint getDestination(JourneyPattern jp) 
	{
		StopPoint dest = null;
		for (StopPoint point : jp.getStopPoints())
		{
			if (dest == null || dest.getPosition() < point.getPosition())
			{
				dest = point;
			}
		}
		return dest;
	}

	private StopPoint getOrigin(JourneyPattern jp) 
	{
		StopPoint ori = null;
		for (StopPoint point : jp.getStopPoints())
		{
			if (ori == null || ori.getPosition() > point.getPosition())
			{
				ori = point;
			}
		}
		return ori;
	}


	private List<StopPoint> collectStopPoints(StopArea area) 
	{
		List<StopPoint> points = new ArrayList<StopPoint>();
		if (area.getContainedStopPoints() != null && !area.getContainedStopPoints().isEmpty())
		{
			points.addAll(area.getContainedStopPoints());
		}
		if (area.getContainedStopAreas() != null && !area.getContainedStopAreas().isEmpty())
		{
			for (StopArea child : area.getContainedStopAreas()) 
			{
				points.addAll(collectStopPoints(child));
			}
		}
		return points;
	}


	/**
	 * @param stopSiriId
	 * @param stopVisitTypes
	 * @param monitoredVehicleJourney
	 * @param detailLevel 
	 * @return
	 * @throws SiriException 
	 */
	private void populateMonitoredCall(DatedCall datedCall,StopPoint point, StopVisitTypeEnumeration.Enum stopVisitTypes,
			MonitoredVehicleJourneyStructure monitoredVehicleJourney, DetailLevelEnum detailLevel) 
					throws SiriException 
					{


		MonitoredCallStructure monitoredCall = monitoredVehicleJourney.addNewMonitoredCall();

		StopPointRefStructure stopPointRef = monitoredCall.addNewStopPointRef();
		StopArea area = point.getContainedInStopArea();
		if (stopIdSubType.equalsIgnoreCase("SPOR"))
		{
			stopPointRef.setStringValue(chouetteTool.toSiriId(point.getObjectId(), SiriTool.ID_STOPPOINT, null));
		}
		else if (stopIdSubType.equalsIgnoreCase("BP") || stopIdSubType.equalsIgnoreCase("Q"))
		{
			stopPointRef.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
		}
		else
		{
			area = (area.getParent() != null ? area.getParent() : area);
			stopPointRef.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
		}

		monitoredCall.setOrder(BigInteger.valueOf(point.getPosition()+1));
		if (detailLevel.isValidFor(DetailLevelEnum.full))
		{
			NaturalLanguageStringStructure stopPointName = monitoredCall.addNewStopPointName();
			stopPointName.setStringValue(area.getName());
			stopPointName.setLang(Lang.FR); 
		}

		if (detailLevel.isValidFor(DetailLevelEnum.normal))
		{
			Calendar c = Calendar.getInstance();
			Timestamp t = new Timestamp(c.getTimeInMillis());
			Timestamp arr = datedCall.getExpectedArrivalTime();
			Timestamp dep = datedCall.getExpectedDepartureTime();

			monitoredCall.setVehicleAtStop(t.after(arr) &&  t.before(dep)); 
			monitoredCall.setPlatformTraversal(false);
			JourneyPattern jp = referential.getJourneyPattern(datedCall.getVehicleJourney().getJourneyPatternId());
			if (jp != null)
			{
				NaturalLanguageStringStructure destinationDisplay = monitoredCall.addNewDestinationDisplay();
				destinationDisplay.setLang(Lang.FR); 
				if (jp.getDestination() != null)
				{
					destinationDisplay.setStringValue(jp.getDestination()); 
				}
				else
				{
					StopPoint dest = getDestination(jp);
					destinationDisplay.setStringValue(dest.getContainedInStopArea().getName()); 
				}
			}		
		}

		Calendar now = Calendar.getInstance();
		String departureStatus = datedCall.getDepartureStatus();
		String arrivalStatus = datedCall.getArrivalStatus();
		// filtre info depart/arrivee/tout
		if (stopVisitTypes.intValue() != StopVisitTypeEnumeration.INT_DEPARTURES)
		{
			Calendar hta = convertToCalendar(datedCall.getAimedArrivalTime());
			monitoredCall.setAimedArrivalTime(hta);
			Calendar hra = convertToCalendar(datedCall.getExpectedArrivalTime());
			if (hra.compareTo(now) > 0)
			{
				monitoredCall.setExpectedArrivalTime(hra); 
				monitoredCall.setArrivalStatus(getStatus(arrivalStatus,hta,hra)); // should be on dl = normal
			}
			else
			{
				if (ProgressStatusEnumeration.CANCELLED.toString().equals(arrivalStatus))
				{
					monitoredCall.setArrivalStatus(ProgressStatusEnumeration.CANCELLED); // should be on dl = normal
				}
				else
				{
					monitoredCall.setArrivalStatus(ProgressStatusEnumeration.ARRIVED); // should be on dl = normal
				}
				monitoredCall.setActualArrivalTime(hra);
			}
			//			if (quai != null)
			//			{
			//				NaturalLanguageStringStructure arrivalPlatformName = monitoredCall.addNewArrivalPlatformName();
			//				arrivalPlatformName.setStringValue(quai); 
			//				arrivalPlatformName.setLang("FR"); 
			//			}
		}
		if (stopVisitTypes.intValue() != StopVisitTypeEnumeration.INT_ARRIVALS)
		{
			Calendar htd = convertToCalendar(datedCall.getAimedDepartureTime());
			Calendar hrd = convertToCalendar(datedCall.getExpectedDepartureTime());
			monitoredCall.setAimedDepartureTime(htd);

			if (hrd.compareTo(now) > 0)
			{
				monitoredCall.setExpectedDepartureTime(hrd); 
				monitoredCall.setDepartureStatus(getStatus(departureStatus,htd, hrd));// should be on dl = normal
			}
			else
			{
				monitoredCall.setActualDepartureTime(hrd);
				if (ProgressStatusEnumeration.CANCELLED.toString().equals(departureStatus))
				{
					monitoredCall.setDepartureStatus(ProgressStatusEnumeration.CANCELLED);// should be on dl = normal
				}
				else
				{
					monitoredCall.setDepartureStatus(ProgressStatusEnumeration.ARRIVED);// should be on dl = normal
				}
			}

			//			if (quai != null)
			//			{
			//				NaturalLanguageStringStructure departurePlatformName = monitoredCall.addNewDeparturePlatformName();
			//				departurePlatformName.setStringValue(quai); 
			//				departurePlatformName.setLang("FR"); 
			//			}
		}

		return ;

					}

	/**
	 * @param t
	 * @return
	 */
	private Calendar convertToCalendar(Timestamp t)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(t);
		return c;
	}

	/**
	 * @param rs
	 * @param monitoredVehicleJourney
	 * @param detailLevel 
	 */
	private void populateJourneyProgressInfoGroup(DatedCall datedCall,MonitoredVehicleJourneyStructure monitoredVehicleJourney, DetailLevelEnum detailLevel)
	{
		boolean monitored = false;
		if (datedCall.getVehicleJourney().getService() != null)
		{
			Vehicle vehicle = datedCall.getVehicleJourney().getService().getVehicle();
			if (vehicle != null)
			{
				monitored = vehicle.isMonitored();
				if (!monitored)
				{
					if (vehicle.getMonitoringError() != null)
					{
						List<String> msg = new Vector<String>();
						msg.add(vehicle.getMonitoringError());
						monitoredVehicleJourney.setMonitoringError(msg);
					}
				}
				monitoredVehicleJourney.setInCongestion(vehicle.isInCongestion());
				monitoredVehicleJourney.setInPanic(vehicle.isInPanic());
				if (vehicle.getDelay() != null)
				{
					monitoredVehicleJourney.setDelay(toGDuration(vehicle.getDelay()));
				}
				if (vehicle.getBearing() != null)
				{
					monitoredVehicleJourney.setBearing(vehicle.getBearing().floatValue());
				}
				if (vehicle.getLongLatType() != null && vehicle.getLatitude() != null && vehicle.getLongitude() != null)
				{
					LocationStructure location = monitoredVehicleJourney.addNewVehicleLocation();
					location.setLatitude(vehicle.getLatitude());
					location.setLongitude(vehicle.getLongitude());
				}
				else if (vehicle.getProjectionType() != null && vehicle.getX() != null && vehicle.getY() != null)
				{
					LocationStructure location = monitoredVehicleJourney.addNewVehicleLocation();
					location.setSrsName(vehicle.getProjectionType());
					CoordinatesStructure coords = location.addNewCoordinates();
					List<String> values = new ArrayList<String>();
					values.add(vehicle.getX().toString());
					values.add(vehicle.getY().toString());
					coords.setListValue(values);
				}
			}
		}
		monitoredVehicleJourney.setMonitored(monitored);


	}

	/**
	 * @param durationInSeconds
	 * @return
	 */
	private GDuration toGDuration(Long durationInSeconds) 
	{
		long absDuration = Math.abs(durationInSeconds.longValue());
		int sign = 1;
		if (durationInSeconds < 0) sign = -1;
		int hour = (int) (absDuration / 3600);
		int minute = (int) ((absDuration / 60) % 60);
		int second = (int) (absDuration % 60);
		return new GDuration(sign,0,0,0,hour,minute,second,BigDecimal.ZERO);
	}

	/**
	 * @param rs
	 * @param monitoredVehicleJourney
	 * @param detailLevel 
	 */
	private void populateDisruptionGroup(MonitoredVehicleJourneyStructure monitoredVehicleJourney, DetailLevelEnum detailLevel)
	{


	}

	/**
	 * @param rs
	 * @param cleLigne
	 * @param monitoredVehicleJourney
	 * @param detailLevel 
	 * @throws SiriException
	 */
	private void populateVehicleJourneyInfoGroup(DatedCall datedCall,StopPoint point,
			MonitoredVehicleJourneyStructure monitoredVehicleJourney, DetailLevelEnum detailLevel) throws SiriException
			{
		JourneyPattern jp = referential.getJourneyPattern(datedCall.getVehicleJourney().getJourneyPatternId());
		StopPoint dest = null;
		StopPoint ori = null;
		if (jp == null)
		{
			Route route = referential.getRoute(datedCall.getVehicleJourney().getRouteId());
			List<StopPoint> points = route.getStopPoints();
			if (points != null && !points.isEmpty())
			{
				ori = points.get(0);
				dest = points.get(points.size()-1);
			}
		}
		else
		{
			ori = getOrigin(jp);
			dest = getDestination(jp);
		}
		if (dest != null)
		{
			DestinationRefStructure destination = monitoredVehicleJourney.addNewDestinationRef();
			StopArea area = dest.getContainedInStopArea();

			if (terminusIdSubType.equalsIgnoreCase("SPOR"))
			{
				destination.setStringValue(chouetteTool.toSiriId(dest.getObjectId(),SiriTool.ID_STOPPOINT,null));
			}
			else if (terminusIdSubType.equalsIgnoreCase("BP") || terminusIdSubType.equalsIgnoreCase("Q"))

			{
				destination.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
			}
			else // SP
			{
				area = getParentIfExists(area);
				destination.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
			}
			if (detailLevel.isValidFor(DetailLevelEnum.full))
			{
				NaturalLanguageStringStructure destinationName = monitoredVehicleJourney.addNewDestinationName();
				destinationName.setStringValue(area.getName());
				destinationName.setLang(Lang.FR);

				// TODO : direction = ??
				NaturalLanguageStringStructure directionName = monitoredVehicleJourney.addNewDirectionName();
				directionName.setStringValue(area.getName()); 
				directionName.setLang(Lang.FR); 
			}

		}
		else
		{
			logger.warn("no destination for call");
		}
		if (ori != null)
		{
			JourneyPlaceRefStructure origin = monitoredVehicleJourney.addNewOriginRef();
			StopArea area = ori.getContainedInStopArea();

			if (terminusIdSubType.equalsIgnoreCase("SPOR"))
			{
				origin.setStringValue(chouetteTool.toSiriId(ori.getObjectId(),SiriTool.ID_STOPPOINT,null));
			}
			else if (terminusIdSubType.equalsIgnoreCase("BP") || terminusIdSubType.equalsIgnoreCase("Q"))
			{
				origin.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
			}
			else // SP
			{
				area = getParentIfExists(area);
				origin.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
			}

			if (detailLevel.isValidFor(DetailLevelEnum.full))
			{
				NaturalLanguagePlaceNameStructure originName = monitoredVehicleJourney.addNewOriginName();
				originName.setStringValue(area.getName());
				originName.setLang(Lang.FR);

				if (datedCall.getVehicleJourney().getName() != null)
				{
					NaturalLanguageStringStructure vehicleJourneyName = monitoredVehicleJourney.addNewVehicleJourneyName();
					vehicleJourneyName.setStringValue(datedCall.getVehicleJourney().getName());
				}
			}
		}
		else
		{
			logger.warn("no origin for call");
		}


		//   ServiceInfoGroup
		populateServiceInfoGroup( datedCall,point, monitoredVehicleJourney);

			}

	/**
	 * @param rs
	 * @param cleLigne
	 * @param monitoredVehicleJourney
	 */
	private void populateServiceInfoGroup(DatedCall datedCall,StopPoint point,
			MonitoredVehicleJourneyStructure monitoredVehicleJourney)
	{

		Line l = point.getRoute().getLine();
		TransportModeNameEnum modeEnum = l.getTransportModeName();
		VehicleModesEnumeration.Enum vehicleMode = null;
		switch (modeEnum)
		{
		case AIR : vehicleMode = VehicleModesEnumeration.AIR; break;
		case BUS :
		case TROLLEYBUS : vehicleMode = VehicleModesEnumeration.BUS; break;
		case COACH : vehicleMode = VehicleModesEnumeration.COACH; break;
		case FERRY : vehicleMode = VehicleModesEnumeration.FERRY; break;
		case LOCALTRAIN : 
		case LONGDISTANCETRAIN : 
		case LONGDISTANCETRAIN_2 : 
		case TRAIN : vehicleMode = VehicleModesEnumeration.RAIL; break;
		case METRO : vehicleMode = VehicleModesEnumeration.METRO; break;
		case TRAMWAY : vehicleMode = VehicleModesEnumeration.TRAM; break;
		default : vehicleMode = VehicleModesEnumeration.BUS; break;
		}
		monitoredVehicleJourney.addVehicleMode(vehicleMode);
		try
		{
			if (l.getCompany() != null)
			{
				OperatorRefStructure operatorRef = monitoredVehicleJourney.addNewOperatorRef();
				operatorRef.setStringValue(chouetteTool.toSiriId(l.getCompany().getObjectId(), SiriTool.ID_COMPANY)); 
			}
		}
		catch (SiriException e)
		{
			// problem in setting operator ref; reset it
			if (monitoredVehicleJourney.isSetOperatorRef())
			{
				monitoredVehicleJourney.unsetOperatorRef();
			}
		}

	}

	/**
	 * @param datedCall
	 * @param point
	 * @param monitoredVehicleJourney
	 * @param detailLevel 
	 * @throws SiriException
	 */
	private void populateJourneyPatternInfoGroup(DatedCall datedCall,StopPoint point,
			MonitoredVehicleJourneyStructure monitoredVehicleJourney, DetailLevelEnum detailLevel) throws SiriException
			{

		Long journeyPatternId = datedCall.getVehicleJourney().getJourneyPatternId();
		if (journeyPatternId != null)
		{
			JourneyPattern jp = referential.getJourneyPattern(journeyPatternId);
			if (jp != null)
			{
				JourneyPatternRefStructure journeyPatternRef = monitoredVehicleJourney.addNewJourneyPatternRef();
				journeyPatternRef.setStringValue(chouetteTool.toSiriId(jp.getObjectId(), SiriTool.ID_JOURNEYPATTERN));
			}
		}
		if (point.getRoute() != null)
		{
			RouteRefStructure route = monitoredVehicleJourney.addNewRouteRef();
			route.setStringValue(chouetteTool.toSiriId(point.getRoute().getObjectId(),SiriTool.ID_ROUTE));
			if (detailLevel.isValidFor(DetailLevelEnum.full))
			{
				Line line = point.getRoute().getLine();
				if (line != null)
				{
					String publishedName = line.getPublishedName();
					if (publishedName != null)
					{
						NaturalLanguageStringStructure publishedLineName = monitoredVehicleJourney.addNewPublishedLineName();
						publishedLineName.setStringValue(publishedName);
						publishedLineName.setLang(Lang.FR);
					}
				}
			}
		}

			}

	private void addOnwards(MonitoredVehicleJourneyStructure monitoredVehicleJourney,
			DatedCall datedCall,
			StopVisitTypeEnumeration.Enum stopVisitTypes,
			int maxOnwards, DetailLevelEnum detailLevel) 
					throws SiriException
					{
		// journey et pos > stopPoint stop 


		List<DatedCall> onwardCalls = realTimeDao.getOnwardCalls(datedCall.getVehicleJourney().getId(), datedCall.getPosition(),maxOnwards);
		if (onwardCalls.size() > 0)
		{
			OnwardCallsStructure onwards = monitoredVehicleJourney.addNewOnwardCalls();
			for (DatedCall onwardCall : onwardCalls)
			{
				populateOnward(onwards,onwardCall,stopVisitTypes,detailLevel);
			}
		}

					}

	private void populateOnward(OnwardCallsStructure onwards,
			DatedCall datedCall,
			StopVisitTypeEnumeration.Enum stopVisitTypes, 
			DetailLevelEnum detailLevel) 
					throws SiriException
					{
		String departureStatus = datedCall.getDepartureStatus();
		String arrivalStatus = datedCall.getArrivalStatus();

		OnwardCallStructure onwardCall = onwards.addNewOnwardCall();
		StopPoint point = referential.getStopPoint(datedCall.getStopPointId());
		StopArea area = point.getContainedInStopArea();
		StopPointRefStructure stopPointRef = onwardCall.addNewStopPointRef();
		if (stopIdSubType.equalsIgnoreCase("SPOR"))
		{
			stopPointRef.setStringValue(chouetteTool.toSiriId(point.getObjectId(), SiriTool.ID_STOPPOINT, null));
		}
		else if (stopIdSubType.equalsIgnoreCase("BP") || stopIdSubType.equalsIgnoreCase("Q"))
		{
			stopPointRef.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
		}
		else
		{
			area = getParentIfExists(area);
			stopPointRef.setStringValue(chouetteTool.toSiriId(area.getObjectId(),SiriTool.ID_STOPPOINT,area.getAreaType()));
		}

		if (detailLevel.isValidFor(DetailLevelEnum.full))
		{
			NaturalLanguageStringStructure name = onwardCall.addNewStopPointName();
			name.setLang(Lang.FR);
			name.setStringValue(area.getName());
		}
		StopPointRefStructure ref = onwardCall.addNewStopPointRef();
		ref.setStringValue(point.getObjectId());
		onwardCall.setOrder(BigInteger.valueOf((long) point.getPosition()+1));

		if (detailLevel.isValidFor(DetailLevelEnum.normal))
		{
			Calendar c = Calendar.getInstance();
			Timestamp t = new Timestamp(c.getTimeInMillis());
			Timestamp arr = datedCall.getExpectedArrivalTime();
			Timestamp dep = datedCall.getExpectedDepartureTime();
			onwardCall.setVehicleAtStop(t.before(dep) && t.after(arr));
		}

		// horaires
		Calendar now = Calendar.getInstance();
		// filtre info depart/arrivee/tout
		if (stopVisitTypes.intValue() != StopVisitTypeEnumeration.INT_DEPARTURES)
		{
			Calendar hta = convertToCalendar(datedCall.getAimedArrivalTime());
			onwardCall.setAimedArrivalTime(hta);
			Calendar hra = convertToCalendar(datedCall.getExpectedArrivalTime());
			if (hra.compareTo(now) > 0)
			{
				onwardCall.setExpectedArrivalTime(hra); 
				onwardCall.setArrivalStatus(getStatus(arrivalStatus,hta,hra));
			}
			else
			{
				if (ProgressStatusEnumeration.CANCELLED.toString().equals(arrivalStatus))
				{
					onwardCall.setArrivalStatus(ProgressStatusEnumeration.CANCELLED);
				}
				else
				{
					onwardCall.setArrivalStatus(ProgressStatusEnumeration.ARRIVED);
				}
				onwardCall.setExpectedArrivalTime(hra);
			}
		}
		if (stopVisitTypes.intValue() != StopVisitTypeEnumeration.INT_ARRIVALS)
		{
			Calendar htd = convertToCalendar(datedCall.getAimedDepartureTime());
			Calendar hrd = convertToCalendar(datedCall.getExpectedDepartureTime());
			onwardCall.setAimedDepartureTime(htd);

			if (hrd.compareTo(now) > 0)
			{
				onwardCall.setExpectedDepartureTime(hrd); 
				onwardCall.setDepartureStatus(getStatus(departureStatus,htd, hrd));
			}
			else
			{
				onwardCall.setExpectedDepartureTime(hrd);
				if (ProgressStatusEnumeration.CANCELLED.toString().equals(departureStatus))
				{
					onwardCall.setDepartureStatus(ProgressStatusEnumeration.CANCELLED);
				}
				else
				{
					onwardCall.setDepartureStatus(ProgressStatusEnumeration.ARRIVED);
				}
			}
		}
					}


	/**
	 * @param status 
	 * @param ht heure theorique
	 * @param hr heure reelle
	 * @return status
	 */
	private ProgressStatusEnumeration.Enum getStatus(String status, Calendar ht, Calendar hr)
	{
		long delta = hr.getTimeInMillis() - ht.getTimeInMillis();

		if (ProgressStatusEnumeration.CANCELLED.toString().equals(status))
			return ProgressStatusEnumeration.CANCELLED;

		if (status != null && !ProgressStatusEnumeration.NO_REPORT.toString().equals(status))
			return ProgressStatusEnumeration.Enum.forString(status);

		int ecart= (int) (delta / 1000);
		if (ecart >= this.delayedGap)
		{
			return ProgressStatusEnumeration.DELAYED;
		}
		if (ecart <= -this.earlyGap)
		{
			return ProgressStatusEnumeration.EARLY;
		}
		return ProgressStatusEnumeration.ON_TIME;

	}

	/**
	 * return self or parent if exists
	 * 
	 * @param area
	 * @return
	 */
	public StopArea getParentIfExists(StopArea area)
	{
		if (area.getParent() != null)
		{
			return area.getParent();
		}
		return area;
	}


}