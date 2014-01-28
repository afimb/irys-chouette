package irys.siri.chouette.server.producer;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import irys.common.SiriException;
import irys.common.SiriTool;
import irys.siri.chouette.ChouetteTool;
import irys.siri.chouette.Referential;
import irys.siri.server.producer.DiscoveryInterface;
import irys.siri.server.producer.service.AbstractSiriService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.w3.xml.x1998.namespace.LangAttribute.Lang;

import uk.org.siri.siri.AnnotatedDestinationStructure;
import uk.org.siri.siri.AnnotatedLineStructure;
import uk.org.siri.siri.AnnotatedLineStructure.Destinations;
import uk.org.siri.siri.AnnotatedStopPointStructure;
import uk.org.siri.siri.AnnotatedStopPointStructure.Lines;
import uk.org.siri.siri.DestinationRefStructure;
import uk.org.siri.siri.LineRefStructure;
import uk.org.siri.siri.LinesDeliveryStructure;
import uk.org.siri.siri.LinesDiscoveryRequestStructure;
import uk.org.siri.siri.NaturalLanguageStringStructure;
import uk.org.siri.siri.StopPointRefStructure;
import uk.org.siri.siri.StopPointsDeliveryStructure;
import uk.org.siri.siri.StopPointsDiscoveryRequestStructure;

public class ChouetteDiscoveryService extends AbstractSiriService implements DiscoveryInterface
{
	/**
	 * Logger for this class
	 */
	 // private static final Logger logger = Logger.getLogger(ChouetteDiscoveryService.class);


	@Setter @Getter private String version ="1.3";

	@Setter private ChouetteTool chouetteTool;

	@Setter private Referential referential;

	@Setter private boolean withStopPoints = false;


	/**
	 * 
	 */
	public ChouetteDiscoveryService()
	{
		super();
	}


	/* (non-Javadoc)
	 * @see irys.siri.server.DiscoveryServiceInterface#getLinesDiscovery(uk.org.siri.www.siri.LinesDiscoveryRequestStructure)
	 */
	@Override
	public LinesDeliveryStructure getLinesDiscovery(LinesDiscoveryRequestStructure request,Calendar responseTimestamp) throws SiriException
	{
		LinesDeliveryStructure response = LinesDeliveryStructure.Factory.newInstance();
		response.setResponseTimestamp(responseTimestamp);
		response.setVersion(getVersion());
		Collection<Line> chouetteLines = referential.getAllLines();
		for (Line chouetteLine : chouetteLines)
		{
			AnnotatedLineStructure line = response.addNewAnnotatedLineRef();
			NaturalLanguageStringStructure lineName = line.addNewLineName();
			lineName.setStringValue(chouetteLine.getPublishedName());
			lineName.setLang(Lang.FR);
			LineRefStructure lineRef = line.addNewLineRef();
			lineRef.setStringValue(chouetteTool.toSiriId(chouetteLine.getObjectId(),SiriTool.ID_LINE));
			line.setMonitored(true);
			// destinations: 
				Set<StopArea> dests = new HashSet<StopArea>();
				if (chouetteLine.getRoutes().size() > 0)
				{
					Destinations destinations = line.addNewDestinations();
					for (Route route : chouetteLine.getRoutes())
					{
						for (JourneyPattern jp : route.getJourneyPatterns())
						{
							if (jp.getStopPoints() == null || jp.getStopPoints().isEmpty()) continue;
							StopArea dest = jp.getStopPoints().get(jp.getStopPoints().size()-1).getContainedInStopArea();
							if (dests.contains(dest)) continue; // destination already added 
							dests.add(dest);
							AnnotatedDestinationStructure destination = destinations.addNewDestination();
							DestinationRefStructure destRef = destination.addNewDestinationRef();
							destRef.setStringValue(chouetteTool.toSiriId(dest.getObjectId(),SiriTool.ID_STOPPOINT, dest.getAreaType()));
							NaturalLanguageStringStructure name = destination.addNewPlaceName();
							name.setStringValue(dest.getName());
							name.setLang(Lang.FR);                        	
						}

					}
				}
		}
		response.setStatus(true);
		return response;
	}

	/* (non-Javadoc)
	 * @see irys.siri.server.DiscoveryServiceInterface#getStopPointsDiscovery(uk.org.siri.www.siri.StopPointsDiscoveryRequestStructure)
	 */
	@Override
	public StopPointsDeliveryStructure getStopPointsDiscovery(StopPointsDiscoveryRequestStructure request,Calendar responseTimestamp)
			throws SiriException
			{
		StopPointsDeliveryStructure response = StopPointsDeliveryStructure.Factory.newInstance();
		response.setResponseTimestamp(responseTimestamp);
		response.setVersion(getVersion());

		Collection<StopArea> chouetteStops = new ArrayList<StopArea>();
		chouetteStops.addAll(referential.getAllStopPlaces());
		chouetteStops.addAll(referential.getAllBoardingPositions());
		chouetteStops.addAll(referential.getAllQuays());

		for (StopArea chouetteStop : chouetteStops)
		{
			AnnotatedStopPointStructure stopPoint = response.addNewAnnotatedStopPointRef();
			NaturalLanguageStringStructure stopName = stopPoint.addNewStopName();
			stopName.setStringValue(chouetteStop.getName());
			stopName.setLang(Lang.FR);
			StopPointRefStructure stopRef = stopPoint.addNewStopPointRef();        
			stopRef.setStringValue(chouetteTool.toSiriId(chouetteStop.getObjectId(),SiriTool.ID_STOPPOINT,chouetteStop.getAreaType()));
			stopPoint.setMonitored(true);
			List<String> lineIds = referential.getLineOidsForAreaOid(chouetteStop.getObjectId());
			if (lineIds.size() > 0)
			{
				Lines lines = stopPoint.addNewLines();
				for (String lineId : lineIds)
				{
					LineRefStructure line = lines.addNewLineRef();
					line.setStringValue(chouetteTool.toSiriId(lineId, SiriTool.ID_LINE));
				}
			}
		}
		if (withStopPoints)
		{
			Collection<StopPoint> chouettePoints = referential.getAllStopPoints();
			for (StopPoint chouettePoint : chouettePoints)
			{
				AnnotatedStopPointStructure stopPoint = response.addNewAnnotatedStopPointRef();
				NaturalLanguageStringStructure stopName = stopPoint.addNewStopName();
				stopName.setStringValue(chouettePoint.getContainedInStopArea().getName());
				stopName.setLang(Lang.FR);
				StopPointRefStructure stopRef = stopPoint.addNewStopPointRef();
				stopRef.setStringValue(chouetteTool.toSiriId(chouettePoint.getObjectId(),SiriTool.ID_STOPPOINT,null));
				stopPoint.setMonitored(true);
				if (chouettePoint.getRoute().getLine() != null)
				{
					Lines lines = stopPoint.addNewLines();
					LineRefStructure line = lines.addNewLineRef();
					line.setStringValue(chouetteTool.toSiriId(chouettePoint.getRoute().getLine().getObjectId(),SiriTool.ID_LINE));
				}
			}
		}
		response.setStatus(true);
		return response;
			}

}

