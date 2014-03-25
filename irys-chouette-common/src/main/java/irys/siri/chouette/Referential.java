package irys.siri.chouette;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import irys.common.SiriException;
import irys.common.SiriTool;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

public class Referential
{
	private static final Logger logger = Logger.getLogger(Referential.class);

	@Setter private ChouetteTool chouetteTool;
	@Setter private INeptuneManager<Line> lineManager;
	@Setter private INeptuneManager<PTNetwork> networkManager;
	@Setter private SessionFactory sessionFactory ;
	@Setter private boolean scanNetworkVersionDate = false;
	@Setter private long scanPeriod = 30; // minutes

	private Map<String, PTNetwork> networkOidMap = new HashMap<String, PTNetwork>();
	private Map<String, Line> lineOidMap = new HashMap<String, Line>();
	private Map<String, Company> companyOidMap  = new HashMap<String, Company>();
	private Map<String, Route> routeOidMap  = new HashMap<String, Route>();
	private Map<String, JourneyPattern> journeyPatternOidMap  = new HashMap<String, JourneyPattern>();
	// private Map<String, VehicleJourney> vehicleJourneyMap;
	private Map<String, StopPoint> stopPointOidMap  = new HashMap<String, StopPoint>();
	private Map<String, StopArea> boardingPositionOidMap  = new HashMap<String, StopArea>();
	private Map<String, StopArea> quayOidMap  = new HashMap<String, StopArea>();
	private Map<String, StopArea> stopPlaceOidMap  = new HashMap<String, StopArea>();

	// private Map<String, List<String>> areaOidListByLineOidMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> lineOidListByAreaOidMap = new HashMap<String, List<String>>();

	private Map<Long, PTNetwork> networkMap = new HashMap<Long, PTNetwork>();
	private Map<Long, Line> lineMap = new HashMap<Long, Line>();
	private Map<Long, Company> companyMap  = new HashMap<Long, Company>();
	private Map<Long, Route> routeMap  = new HashMap<Long, Route>();
	private Map<Long, JourneyPattern> journeyPatternMap  = new HashMap<Long, JourneyPattern>();
	// private Map<String, VehicleJourney> vehicleJourneyMap;
	private Map<Long, StopPoint> stopPointMap  = new HashMap<Long, StopPoint>();
	private Map<Long, StopArea> boardingPositionMap  = new HashMap<Long, StopArea>();
	private Map<Long, StopArea> quayMap  = new HashMap<Long, StopArea>();
	private Map<Long, StopArea> stopPlaceMap  = new HashMap<Long, StopArea>();

	private Map<Long, List<Long>> areaIdListByLineIdMap = new HashMap<Long, List<Long>>();
	private Map<Long, List<Long>> lineIdListByAreaIdMap = new HashMap<Long, List<Long>>();

	private Thread scanThread = null;

	private boolean initialized = false;

	public synchronized void init() 
	{
		if (initialized) return;
		Session session = null;

		networkMap.clear();
		lineMap.clear();
		companyMap.clear();
		routeMap.clear();
		journeyPatternMap.clear();
		// vehicleJourneyMap.clear();
		stopPointMap.clear();
		boardingPositionMap.clear();
		quayMap.clear();
		stopPlaceMap.clear();
		areaIdListByLineIdMap.clear();
		lineIdListByAreaIdMap.clear();
		networkOidMap.clear();
		lineOidMap.clear();
		companyOidMap.clear();
		routeOidMap.clear();
		journeyPatternOidMap.clear();
		// vehicleJourneyMap.clear();
		stopPointOidMap.clear();
		boardingPositionOidMap.clear();
		quayOidMap.clear();
		stopPlaceOidMap.clear();
		// areaOidListByLineOidMap.clear();
		lineOidListByAreaOidMap.clear();

		try 
		{
			session = SessionFactoryUtils.getSession(sessionFactory, true);
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			// initialize lazy collections and fill maps
			List<Line> lines = lineManager.getAll(null);
			for (Line line : lines) 
			{
				lineOidMap.put(line.getObjectId(),line);
				lineMap.put(line.getId(),line);
				PTNetwork network = line.getPtNetwork();
				if (network != null)
				{
					networkOidMap.put(network.getObjectId(), network);
					networkMap.put(network.getId(), network);
				}
				Company company = line.getCompany();
				if (company != null)
				{
					companyOidMap.put(company.getObjectId(), company);
					companyMap.put(company.getId(), company);
				}
				for (Route route : line.getRoutes())
				{
					routeOidMap.put(route.getObjectId(), route);
					routeMap.put(route.getId(), route);
					populateStopPointMap(route);
					mapStopAreaWithLine(route);
					for (JourneyPattern jp : route.getJourneyPatterns())
					{
						journeyPatternOidMap.put(jp.getObjectId(), jp);
						journeyPatternMap.put(jp.getId(), jp);
						List<StopPoint> stopPoints = jp.getStopPoints();
						stopPoints.size();
						jp.getRoute();
						//						for (VehicleJourney vj : jp.getVehicleJourneys())
						//						{
						//							vehicleJourneyMap.put(vj.getObjectId(), vj);
						//						}
					}
				}
			}
			logger.info("line count = "+lineMap.size());
			logger.info("route count = "+routeMap.size());
			logger.info("journeyPattern count = "+journeyPatternMap.size());
			logger.info("stopPoint count = "+stopPointMap.size());
			logger.info("boardingPosition count = "+boardingPositionMap.size());
			logger.info("quay count = "+quayMap.size());
			logger.info("stopPlace count = "+stopPlaceMap.size());
			initialized = true;
		} 
		catch (Exception e) 
		{
			logger.error("fail to load chouette referential "+e.getMessage(),e);
		}
		finally
		{
			if (session != null)
			{
				TransactionSynchronizationManager.unbindResource(sessionFactory);
				SessionFactoryUtils.closeSession(session);
			}
		}

		if (scanNetworkVersionDate)
		{
			launchScanner();
		}

	}


	private void launchScanner() 
	{
		scanThread = new Thread()
		{

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() 
			{
				long waiting = scanPeriod* 60000;

				try 
				{
					while (true)
					{
						Thread.sleep(waiting);
						try 
						{
							List<PTNetwork> networks = networkManager.getAll(null);
							boolean reload = false;
							for (PTNetwork ptNetwork : networks) 
							{
								PTNetwork network = networkMap.get(ptNetwork.getObjectId());
								if (network == null) 
								{
									logger.info("new network detected "+ptNetwork.getObjectId());
									reload = true;
									break;
								}
								if (network.getVersionDate().before(ptNetwork.getVersionDate()))
								{
									SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
									logger.info("network date changed from "+format.format(network.getVersionDate())+" to "+ format.format(ptNetwork.getVersionDate()));									
									reload = true;
									break;
								}
							}

							if (reload)
							{
								initialized = false;
								init();
							}
						} 
						catch (ChouetteException e) 
						{
							logger.error("cannot load networks",e);
						}


					}
				} 
				catch (InterruptedException e) 
				{
					logger.info("scan Network Version date stopped");
				}

			}

		};
		scanThread.start();

	}

	public void close()
	{
		if (scanThread != null)
		{
			scanThread.interrupt();
		}
	}

	/**
	 * attach stoparea on lines using route definition
	 * 
	 * @param route
	 */
	private void mapStopAreaWithLine(Route route) 
	{
		Line line = route.getLine();
		// String lineOid = line.getObjectId();
		Long lineId = line.getId();
		List<Long> areaOfLineList = areaIdListByLineIdMap.get(lineId);
		// List<String> areaOidOfLineList = areaOidListByLineOidMap.get(lineOid);
		if (areaOfLineList == null)
		{
			areaOfLineList = new ArrayList<Long>();
			areaIdListByLineIdMap.put(lineId, areaOfLineList);
		}
		//		if (areaOidOfLineList == null)
		//		{
		//			areaOidOfLineList = new ArrayList<String>();
		//			areaOidListByLineOidMap.put(lineOid, areaOidOfLineList);
		//		}
		for (StopPoint point : route.getStopPoints())
		{
			StopArea area = point.getContainedInStopArea();
			mapAreaToLine(area,line,areaOfLineList);
		}
	}


	/**
	 * affect an area to a line if not allready affected
	 * 
	 * @param area
	 * @param lineId
	 * @param areaOfLineList
	 */
	private void mapAreaToLine(StopArea area, Line line,List<Long> areaOfLineList) 
	{
		Long areaId = area.getId();
		String areaOid = area.getObjectId();
		if (! areaOfLineList.contains(areaId)) 
		{
			areaOfLineList.add(areaId);
			List<String> lineOidOfAreaList = lineOidListByAreaOidMap.get(areaOid);
			if (lineOidOfAreaList == null) 
			{
				lineOidOfAreaList = new ArrayList<String>();
				lineOidListByAreaOidMap.put(areaOid, lineOidOfAreaList);
			}
			if (!lineOidOfAreaList.contains(line.getObjectId())) lineOidOfAreaList.add(line.getObjectId());
			List<Long> lineOfAreaList = lineIdListByAreaIdMap.get(areaId);
			if (lineOfAreaList == null) 
			{
				lineOfAreaList = new ArrayList<Long>();
				lineIdListByAreaIdMap.put(areaId, lineOfAreaList);
			}
			if (!lineOfAreaList.contains(line.getId())) lineOfAreaList.add(line.getId());
			StopArea parent = area.getParent(); 
			if (parent != null)
				mapAreaToLine(parent,line,areaOfLineList);
		}

	}


	/**
	 * @param route
	 */
	private void populateStopPointMap(Route route) 
	{
		for (StopPoint point : route.getStopPoints())
		{
			stopPointMap.put(point.getId(), point);
			stopPointOidMap.put(point.getObjectId(), point);
			StopArea area = point.getContainedInStopArea();
			Map<String, StopArea> areaOidMap = null;
			Map<Long, StopArea> areaMap = null;
			if (area.getAreaType().equals(ChouetteAreaEnum.BOARDINGPOSITION))
			{
				areaMap = boardingPositionMap;
				areaOidMap = boardingPositionOidMap;
			}
			else if (area.getAreaType().equals(ChouetteAreaEnum.QUAY))
			{
				areaMap = quayMap;
				areaOidMap = quayOidMap;
			}
			if (areaMap != null)
			{
				if (!areaMap.containsKey(area.getObjectId()))
				{
					areaMap.put(area.getId(), area);
					areaOidMap.put(area.getObjectId(), area);
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
		StopArea parent = area.getParent();
		if (parent != null) 
		{
			if (!stopPlaceMap.containsKey(parent.getId()))
			{
				stopPlaceMap.put(parent.getId(), parent);
				stopPlaceOidMap.put(parent.getObjectId(), parent);
				addParentsArea(parent);
			}

		}

	}

	//	public List<String> getAreaOidsForLineOid(String lineId)
	//	{
	//		if (!initialized) init();
	//		return areaOidListByLineOidMap.get(lineId);
	//	}

	public List<Long> getAreaIdsForLine(Long id) {
		if (!initialized) init();
		return areaIdListByLineIdMap.get(id);
	}


	public List<String> getLineOidsForAreaOid(String areaId)
	{
		if (!initialized) init();
		return lineOidListByAreaOidMap.get(areaId);
	}

	public List<Long> getLineIdsForArea(Long id) {
		if (!initialized) init();
		return lineIdListByAreaIdMap.get(id);
	}

	public Line getLine(String objectId)
	{
		if (!initialized) init();
		return lineOidMap.get(objectId);
	}

	public Line getLine(Long id)
	{
		if (!initialized) init();
		return lineMap.get(id);
	}

	public Route getRoute(String objectId)
	{
		if (!initialized) init();
		return routeOidMap.get(objectId);
	}

	public Route getRoute(Long id)
	{
		if (!initialized) init();
		return routeMap.get(id);
	}

	public JourneyPattern getJourneyPattern(String objectId)
	{
		if (!initialized) init();
		return journeyPatternOidMap.get(objectId);
	}

	public JourneyPattern getJourneyPattern(Long id)
	{
		if (!initialized) init();
		return journeyPatternMap.get(id);
	}

	public StopPoint getStopPoint(String objectId)
	{
		if (!initialized) init();
		return stopPointOidMap.get(objectId);
	}

	public StopPoint getStopPoint(Long id)
	{
		if (!initialized) init();
		return stopPointMap.get(id);
	}

	public StopArea getStopArea(String objectId)
	{
		if (!initialized) init();
		if (boardingPositionOidMap.containsKey(objectId)) return boardingPositionOidMap.get(objectId);
		if (quayOidMap.containsKey(objectId)) return quayOidMap.get(objectId);
		if (stopPlaceOidMap.containsKey(objectId)) return stopPlaceOidMap.get(objectId);
		return null;
	}

	public StopArea getStopArea(Long id)
	{
		if (!initialized) init();
		if (boardingPositionMap.containsKey(id)) return boardingPositionMap.get(id);
		if (quayMap.containsKey(id)) return quayMap.get(id);
		if (stopPlaceMap.containsKey(id)) return stopPlaceMap.get(id);
		return null;
	}

	public Company getCompany(String objectId)
	{
		if (!initialized) init();
		return companyOidMap.get(objectId);
	}

	public Company getCompany(Long id)
	{
		if (!initialized) init();
		return companyMap.get(id);
	}


	public Line getLineFromSiri(String siriId) throws SiriException
	{
		if (!initialized) init();
		if (siriId == null) return null;
		return getLine(chouetteTool.toNeptuneId(siriId, SiriTool.ID_LINE, Line.LINE_KEY));
	}

	public Route getRouteFromSiri(String siriId) throws SiriException
	{
		if (!initialized) init();
		if (siriId == null) return null;
		return getRoute(chouetteTool.toNeptuneId(siriId, SiriTool.ID_ROUTE, Route.ROUTE_KEY));
	}

	public JourneyPattern getJourneyPatternFromSiri(String siriId) throws SiriException
	{
		if (!initialized) init();
		if (siriId == null) return null;
		return getJourneyPattern(chouetteTool.toNeptuneId(siriId, SiriTool.ID_JOURNEYPATTERN, JourneyPattern.JOURNEYPATTERN_KEY));
	}

	public StopPoint getStopPointFromSiri(String siriId) throws SiriException
	{
		if (!initialized) init();
		if (siriId == null) return null;
		return getStopPoint(chouetteTool.toNeptuneId(siriId, SiriTool.ID_STOPPOINT, StopPoint.STOPPOINT_KEY));
	}

	public StopArea getStopAreaFromSiri(String siriId) throws SiriException
	{
		if (!initialized) init();
		if (siriId == null) return null;
		return getStopArea(chouetteTool.toNeptuneId(siriId, SiriTool.ID_STOPPOINT, StopArea.STOPAREA_KEY));
	}

	public Company getCompanyFromSiri(String siriId) throws SiriException
	{
		if (!initialized) init();
		if (siriId == null) return null;
		return getCompany(chouetteTool.toNeptuneId(siriId, SiriTool.ID_COMPANY, Company.COMPANY_KEY));
	}

	public Collection<Line> getAllLines()
	{
		if (!initialized) init();
		return lineMap.values();
	}
	public Collection<Route> getAllRoutes()
	{
		if (!initialized) init();
		return routeMap.values();
	}

	public Collection<StopArea> getAllBoardingPositions()
	{
		if (!initialized) init();
		return boardingPositionMap.values();
	}

	public Collection<StopArea> getAllQuays()
	{
		if (!initialized) init();
		return quayMap.values();
	}

	public Collection<JourneyPattern> getAllJourneyPatterns()
	{
		if (!initialized) init();
		return journeyPatternMap.values();
	}

	public Collection<PTNetwork> getAllNetworks()
	{
		if (!initialized) init();
		return networkMap.values();
	}

	public Collection<Company> getAllCompanies()
	{
		if (!initialized) init();
		return companyMap.values();
	}

	public Collection<StopPoint> getAllStopPoints()
	{
		if (!initialized) init();
		return stopPointMap.values();
	}
	public Collection<StopArea> getAllStopPlaces()
	{
		if (!initialized) init();
		return stopPlaceMap.values();
	}

	public boolean loadNeptuneFiles(File dir,String version)
	{
		Collection<File> xmlFiles = FileUtils.listFiles(dir, new String[]{"xml"}, false);
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue inputFileParameterValue = new SimpleParameterValue("inputFile");
		parameters.add(inputFileParameterValue);

		int lineCount = 0;
		for (File file : xmlFiles) 
		{
			ReportHolder ireport = new ReportHolder();
			ReportHolder vreport = new ReportHolder();
			inputFileParameterValue.setFilepathValue(file.getAbsolutePath());

			try 
			{
				List<Line> lines = lineManager.doImport(null, "NEPTUNE", parameters, ireport, vreport);
				if (lines.isEmpty())
				{
					logReport(ireport.getReport(), Level.ERROR);
					logReport(vreport.getReport(), Level.ERROR);
				}
				else
				{
					logReport(ireport.getReport(), Level.INFO);
					logReport(vreport.getReport(), Level.INFO);
					for (Line line : lines) 
					{
						line.getPtNetwork().setComment(version);
					}
					lineManager.saveAll(null, lines, true, true);
					lineCount++;
				}
			} 
			catch (ChouetteException e) 
			{
				logger.error("fail to import "+file.getName(),e);
			}
		}
		if (lineCount > 0 ) 
		{
			logger.debug("rebuild referential");
			initialized = false;
			init();
			return true;
		}
		logger.error("no line imported");
		return false;
	}
	/**
	 * @param report
	 * @param level
	 */
	private void logReport(Report report, Level level)
	{
		logger.log(level,report.getLocalizedMessage());
		logItems("",report.getItems(),level);

	}

	/**
	 * log report details from import plugins
	 * 
	 * @param indent text indentation for sub levels
	 * @param items report items to log
	 * @param level log level 
	 */
	private void logItems(String indent, List<ReportItem> items, Level level) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			logger.log(level,indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			logItems(indent+"   ",item.getItems(),level);
		}

	}




}
