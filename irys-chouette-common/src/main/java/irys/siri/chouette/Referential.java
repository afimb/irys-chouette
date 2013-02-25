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

	private Thread scanThread = null;

	public synchronized void init() 
	{
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
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
		} 
		catch (ChouetteException e) 
		{
			logger.error("fail to load chouette referential "+e.getMessage(),e);
		}
		finally
		{
			TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(session);
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


	/**
	 * affect an area to a line if not allready affected
	 * 
	 * @param area
	 * @param lineId
	 * @param areaOfLineList
	 */
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
			StopArea parent = area.getParent(); 
			if (parent != null)
				mapAreaToLine(parent,lineId,areaOfLineList);
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
		StopArea parent = area.getParent();
		if (parent != null) 
		{
			if (!stopPlaceMap.containsKey(parent.getObjectId()))
			{
				stopPlaceMap.put(parent.getObjectId(), parent);
				addParentsArea(parent);
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


	public Line getLineFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return lineMap.get(chouetteTool.toNeptuneId(siriId, SiriTool.ID_LINE, Line.LINE_KEY));
	}

	public Route getRouteFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return routeMap.get(chouetteTool.toNeptuneId(siriId, SiriTool.ID_ROUTE, Route.ROUTE_KEY));
	}

	public JourneyPattern getJourneyPatternFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return journeyPatternMap.get(chouetteTool.toNeptuneId(siriId, SiriTool.ID_JOURNEYPATTERN, JourneyPattern.JOURNEYPATTERN_KEY));
	}

	public StopPoint getStopPointFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return stopPointMap.get(chouetteTool.toNeptuneId(siriId, SiriTool.ID_STOPPOINT, StopPoint.STOPPOINT_KEY));
	}

	public StopArea getStopAreaFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return getStopArea(chouetteTool.toNeptuneId(siriId, SiriTool.ID_STOPPOINT, StopArea.STOPAREA_KEY));
	}

	public Company getCompanyFromSiri(String siriId) throws SiriException
	{
		if (siriId == null) return null;
		return getCompany(chouetteTool.toNeptuneId(siriId, SiriTool.ID_COMPANY, Company.COMPANY_KEY));
	}

	public Collection<Line> getAllLines()
	{
		return lineMap.values();
	}
	public Collection<Route> getAllRoutes()
	{
		return routeMap.values();
	}

	public Collection<StopArea> getAllBoardingPositions()
	{
		return boardingPositionMap.values();
	}

	public Collection<StopArea> getAllQuays()
	{
		return quayMap.values();
	}

	public Collection<JourneyPattern> getAllJourneyPatterns()
	{
		return journeyPatternMap.values();
	}

	public Collection<PTNetwork> getAllNetworks()
	{
		return networkMap.values();
	}

	public Collection<Company> getAllCompanies()
	{
		return companyMap.values();
	}

	public Collection<StopPoint> getAllStopPoints()
	{
		return stopPointMap.values();
	}
	public Collection<StopArea> getAllStopPlaces()
	{
		return stopPlaceMap.values();
	}

	@SuppressWarnings("unchecked")
	public boolean loadNeptuneFiles(File dir,String version)
	{
		Collection<File> xmlFiles = FileUtils.listFiles(dir, new String[]{"xml"}, false);
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue inputFileParameterValue = new SimpleParameterValue("inputFile");
		parameters.add(inputFileParameterValue);

		int lineCount = 0;
		for (File file : xmlFiles) 
		{
			ReportHolder report = new ReportHolder();
			inputFileParameterValue.setFilepathValue(file.getAbsolutePath());

			try 
			{
				List<Line> lines = lineManager.doImport(null, "NEPTUNE", parameters, report);
				if (lines.isEmpty())
				{
					logReport(report.getReport(), Level.ERROR);
				}
				else
				{
					logReport(report.getReport(), Level.INFO);
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
