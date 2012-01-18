package net.dryade.siri.chouette.server.producer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dryade.siri.chouette.ChouetteTool;
import net.dryade.siri.chouette.Referential;
import net.dryade.siri.common.SiriException;
import net.dryade.siri.common.SiriTool;
import net.dryade.siri.server.producer.DiscoveryInterface;
import net.dryade.siri.server.producer.service.AbstractSiriService;

import org.w3.xml.x1998.namespace.LangAttribute.Lang;

import uk.org.siri.siri.AnnotatedDestinationStructure;
import uk.org.siri.siri.AnnotatedLineStructure;
import uk.org.siri.siri.AnnotatedLineStructure.Destinations;
import uk.org.siri.siri.AnnotatedLineStructure.Directions;
import uk.org.siri.siri.AnnotatedStopPointStructure;
import uk.org.siri.siri.AnnotatedStopPointStructure.Lines;
import uk.org.siri.siri.DestinationRefStructure;
import uk.org.siri.siri.DirectionStructure;
import uk.org.siri.siri.LineRefStructure;
import uk.org.siri.siri.LinesDeliveryStructure;
import uk.org.siri.siri.LinesDiscoveryRequestStructure;
import uk.org.siri.siri.NaturalLanguageStringStructure;
import uk.org.siri.siri.StopPointRefStructure;
import uk.org.siri.siri.StopPointsDeliveryStructure;
import uk.org.siri.siri.StopPointsDiscoveryRequestStructure;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

public class ChouetteDiscoveryService extends AbstractSiriService implements DiscoveryInterface
{
	/**
	 * Logger for this class
	 */
	// private static final Logger logger = Logger.getLogger(ChouetteDiscoveryService.class);


	@Setter @Getter private String version ="1.3";

	@Setter private ChouetteTool chouetteTool;

	@Setter private Referential referential;


	/**
	 * 
	 */
	public ChouetteDiscoveryService()
	{
		super();
	}


	/* (non-Javadoc)
	 * @see net.dryade.siri.server.DiscoveryServiceInterface#getLinesDiscovery(uk.org.siri.www.siri.LinesDiscoveryRequestStructure)
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
			lineRef.setStringValue(chouetteLine.getObjectId());
			line.setMonitored(true);
			// directions and destinations: 
			if (chouetteLine.getRoutes().size() > 0)
			{
				Directions directions = line.addNewDirections();
				Destinations destinations = line.addNewDestinations();
				for (Route route : chouetteLine.getRoutes())
				{
					for (JourneyPattern jp : route.getJourneyPatterns())
					{
						String dirName = jp.getDestination();
						if (dirName != null && !dirName.isEmpty())
						{
							DirectionStructure direction = directions.addNewDirection();
							NaturalLanguageStringStructure name = direction.addNewDirectionName();
							name.setStringValue(dirName);
							name.setLang(Lang.FR);
						}
						if (jp.getStopPoints() == null || jp.getStopPoints().isEmpty()) continue;
						AnnotatedDestinationStructure destination = destinations.addNewDestination();
						DestinationRefStructure destRef = destination.addNewDestinationRef();
						StopArea dest = jp.getStopPoints().get(jp.getStopPoints().size()-1).getContainedInStopArea();
						destRef.setStringValue(chouetteTool.toSiriId(dest.getObjectId(),SiriTool.ID_STOPPOINT, dest.getAreaType()));
						NaturalLanguageStringStructure name = destination.addNewPlaceName();
						name.setStringValue(dest.getName());
						name.setLang(Lang.FR);
                        if (dirName == null || dirName.isEmpty())
                        {
                        	StopArea com = dest;
                        	if (dest.getParents() != null)
                        	{
                        		for (StopArea parent : dest.getParents()) 
                        		{
									if (parent.getAreaType().equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
									{
										com=parent;
										break;
									}
								}
                        	}
        					DirectionStructure direction = directions.addNewDirection();
							name = direction.addNewDirectionName();
							name.setStringValue(com.getName());
							name.setLang(Lang.FR);
                        }
                        	
					}

				}
			}
		}
		response.setStatus(true);
		return response;
	}

	/* (non-Javadoc)
	 * @see net.dryade.siri.server.DiscoveryServiceInterface#getStopPointsDiscovery(uk.org.siri.www.siri.StopPointsDiscoveryRequestStructure)
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
			stopRef.setStringValue(chouetteStop.getObjectId());
			stopPoint.setMonitored(true);
			List<String> lineIds = referential.getLineIdsForArea(chouetteStop.getObjectId());
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
		Collection<StopPoint> chouettePoints = referential.getAllStopPoints();
		for (StopPoint chouettePoint : chouettePoints)
		{
			AnnotatedStopPointStructure stopPoint = response.addNewAnnotatedStopPointRef();
			NaturalLanguageStringStructure stopName = stopPoint.addNewStopName();
			stopName.setStringValue(chouettePoint.getContainedInStopArea().getName());
			stopName.setLang(Lang.FR);
			StopPointRefStructure stopRef = stopPoint.addNewStopPointRef();
			stopRef.setStringValue(chouettePoint.getObjectId());
			stopPoint.setMonitored(true);
			if (chouettePoint.getRoute().getLine() != null)
			{
				Lines lines = stopPoint.addNewLines();
				LineRefStructure line = lines.addNewLineRef();
				line.setStringValue(chouettePoint.getRoute().getLine().getObjectId());
			}
		}
		response.setStatus(true);
		return response;
	}

}

