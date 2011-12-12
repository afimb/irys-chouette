package net.dryade.siri.chouette.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import net.dryade.siri.server.common.SiriException;
import net.dryade.siri.server.common.SiriTool;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

public class Referential implements ApplicationContextAware
{
	private static final Logger logger = Logger.getLogger(Referential.class);

	@Setter private ApplicationContext applicationContext ;
	@Setter private SiriTool siriTool;
	@Setter private INeptuneManager<Line> lineManager;

	private Map<String, PTNetwork> networkMap = new HashMap<String, PTNetwork>();
	private Map<String, Line> lineMap = new HashMap<String, Line>();
	private Map<String, Company> companyMap  = new HashMap<String, Company>();
	private Map<String, Route> routeMap  = new HashMap<String, Route>();
	private Map<String, JourneyPattern> journeyPatternMap  = new HashMap<String, JourneyPattern>();
	// private Map<String, VehicleJourney> vehicleJourneyMap;
	private Map<String, StopPoint> stopPointMap  = new HashMap<String, StopPoint>();
	private Map<String, StopArea> boardingPositionMap  = new HashMap<String, StopArea>();
	private Map<String, StopArea> quayMap  = new HashMap<String, StopArea>();
	private Map<String, StopArea> stopPlaceMap  = new HashMap<String, StopArea>();

	private Map<String, List<String>> areaIdListByLineIdMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> lineIdListByAreaIdMap = new HashMap<String, List<String>>();

	public void init() 
	{
		SessionFactory sessionFactory = (SessionFactory)applicationContext.getBean("sessionFactory");
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		try 
		{
			// initialize lazy collections and fill maps
			List<Line> lines = lineManager.getAll(null);
			for (Line line : lines) 
			{
				lineMap.put(line.getObjectId(),line);
				PTNetwork network = line.getPtNetwork();
				if (network != null)
				{
					networkMap.put(network.getObjectId(), network);
				}
				Company company = line.getCompany();
				if (company != null)
				{
					companyMap.put(company.getObjectId(), company);
				}
				for (Route route : line.getRoutes())
				{
					routeMap.put(route.getObjectId(), route);
					populateStopPointMap(route);
					mapStopAreaWithLine(route);
					for (JourneyPattern jp : route.getJourneyPatterns())
					{
						journeyPatternMap.put(jp.getObjectId(), jp);
						List<StopPoint> stopPoints = jp.getStopPoints();
						stopPoints.size();
						//						for (VehicleJourney vj : jp.getVehicleJourneys())
						//						{
						//							vehicleJourneyMap.put(vj.getObjectId(), vj);
						//						}
					}
				}
			}
			logger.debug("line count = "+lineMap.size());
			logger.debug("route count = "+routeMap.size());
			logger.debug("journeyPattern count = "+journeyPatternMap.size());
			logger.debug("stopPoint count = "+stopPointMap.size());
			logger.debug("boardingPosition count = "+boardingPositionMap.size());
			logger.debug("quay count = "+quayMap.size());
			logger.debug("stopPlace count = "+stopPlaceMap.size());
		} 
		catch (ChouetteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			SessionFactoryUtils.closeSession(session);
		}

	}


	private void mapStopAreaWithLine(Route route) 
	{
		String lineId = route.getLine().getObjectId();
		List<String> areaOfLineList = areaIdListByLineIdMap.get(lineId);
		if (areaOfLineList == null)
		{
			areaOfLineList = new ArrayList<String>();
			areaIdListByLineIdMap.put(lineId, areaOfLineList);
		}
		for (StopPoint point : route.getStopPoints())
		{
			StopArea area = point.getContainedInStopArea();
			mapAreaToLine(area,lineId,areaOfLineList);
		}
	}


	private void mapAreaToLine(StopArea area, String lineId,List<String> areaOfLineList) 
	{
		String areaId = area.getObjectId();
		if (! areaOfLineList.contains(areaId)) 
		{
			areaOfLineList.add(areaId);
			List<String> lineOfAreaList = lineIdListByAreaIdMap.get(areaId);
			if (lineOfAreaList == null) 
			{
				lineOfAreaList = new ArrayList<String>();
				lineIdListByAreaIdMap.put(areaId, lineOfAreaList);
			}
			if (!lineOfAreaList.contains(lineId)) lineOfAreaList.add(lineId);
			for (StopArea parent : area.getParents()) 
			{
				mapAreaToLine(parent,lineId,areaOfLineList);
			}
		}

	}


	/**
	 * @param route
	 */
	private void populateStopPointMap(Route route) 
	{
		for (StopPoint point : route.getStopPoints())
		{
			stopPointMap.put(point.getObjectId(), point);
			StopArea area = point.getContainedInStopArea();
			Map<String, StopArea> areaMap = null;
			if (area.getAreaType().equals(ChouetteAreaEnum.BOARDINGPOSITION))
			{
				areaMap = boardingPositionMap;
			}
			else if (area.getAreaType().equals(ChouetteAreaEnum.QUAY))
			{
				areaMap = quayMap;
			}
			if (areaMap != null)
			{
				if (!areaMap.containsKey(area.getObjectId()))
				{
					areaMap.put(area.getObjectId(), area);
					addParentsArea(area);
				}
			}
			else
			{
				// log problem
			}
		}
	}


	/**
	 * populate stopPlaceMap with non already present parents of a stop area
	 * 
	 * @param area
	 */
	private void addParentsArea(StopArea area) 
	{
		// initialize lazy collections
		area.getContainedStopAreas().size();
		area.getContainedStopPoints().size();
		List<StopArea> parents = area.getParents();
		if (parents != null) 
		{
			for (StopArea parent : parents) 
			{
				// ignore ITL
				if (parent.getAreaType().equals(ChouetteAreaEnum.ITL)) continue;

				if (!stopPlaceMap.containsKey(parent.getObjectId()))
				{
					stopPlaceMap.put(parent.getObjectId(), parent);
					addParentsArea(parent);
				}
			}
		}

	}
	
	public List<String> getAreaIdsForLine(String lineId)
	{
		return areaIdListByLineIdMap.get(lineId);
	}
	
	public List<String> getLineIdsForArea(String areaId)
	{
		return lineIdListByAreaIdMap.get(areaId);
	}


	public Line getLine(String objectId)
	{
		return lineMap.get(objectId);
	}

	public Route getRoute(String objectId)
	{
		return routeMap.get(objectId);
	}

	public JourneyPattern getJourneyPattern(String objectId)
	{
		return journeyPatternMap.get(objectId);
	}

	public StopPoint getStopPoint(String objectId)
	{
		return stopPointMap.get(objectId);
	}

	public StopArea getStopArea(String objectId)
	{
		if (boardingPositionMap.containsKey(objectId)) return boardingPositionMap.get(objectId);
		if (quayMap.containsKey(objectId)) return quayMap.get(objectId);
		if (stopPlaceMap.containsKey(objectId)) return stopPlaceMap.get(objectId);
		return null;
	}

	public Company getCompany(String objectId)
	{
		return companyMap.get(objectId);
	}


	private String convertId(String siriId,String siriType,String neptuneType) throws SiriException
	{
		String technicalId = siriTool.extractId(siriId, siriType);
		String prefix = siriId.split(":")[0];
		logger.debug(siriId+" = "+prefix+":"+neptuneType+":"+technicalId);
		return prefix+":"+neptuneType+":"+technicalId;
	}

	public Line getLineFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return lineMap.get(convertId(siriId, SiriTool.ID_LINE, Line.LINE_KEY));
	}

	public Route getRouteFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return routeMap.get(convertId(siriId, SiriTool.ID_ROUTE, Route.ROUTE_KEY));
	}

	public JourneyPattern getJourneyPatternFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return journeyPatternMap.get(convertId(siriId, SiriTool.ID_JOURNEYPATTERN, JourneyPattern.JOURNEYPATTERN_KEY));
	}

	public StopPoint getStopPointFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return stopPointMap.get(convertId(siriId, SiriTool.ID_STOPPOINT, StopPoint.STOPPOINT_KEY));
	}

	public StopArea getStopAreaFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return getStopArea(convertId(siriId, SiriTool.ID_STOPPOINT, StopArea.STOPAREA_KEY));
	}

	public Company getCompanyFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return getCompany(convertId(siriId, SiriTool.ID_COMPANY, Company.COMPANY_KEY));
	}


}
