package irys.siri.chouette.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class GeneralMessage extends NeptuneIdentifiedObject
{
	@Getter @Setter private String infoChannel ; // Information, Commercial ou Perturbation
	@Getter @Setter private Timestamp lastModificationDate ;
	@Getter @Setter private Timestamp validUntilDate ;
	@Getter @Setter private String status ;

	@Getter @Setter private List<Message> messages = new ArrayList<GeneralMessage.Message>();
	@Getter @Setter private List<Long> lineIds = new ArrayList<Long>();
	@Getter @Setter private List<Long> routeIds = new ArrayList<Long>();
	@Getter @Setter private List<Long> journeyPatternIds = new ArrayList<Long>();
	@Getter @Setter private List<Long> stopAreaIds = new ArrayList<Long>();
	@Getter @Setter private List<LineSection> lineSections = new ArrayList<GeneralMessage.LineSection>();

	public void addLineId(Long id)
	{
		if (!lineIds.contains(id))
			lineIds.add(id);
	}
	public void addRouteId(Long id)
	{
		if (!routeIds.contains(id))
			routeIds.add(id);
	}
	public void addJourneyPatternId(Long id)
	{
		if (!journeyPatternIds.contains(id))
			journeyPatternIds.add(id);
	}
	public void addStopAreaId(Long id)
	{
		if (!stopAreaIds.contains(id))
			stopAreaIds.add(id);
	}
	public void addLineSection(LineSection section)
	{
		if (!lineSections.contains(section))
			lineSections.add(section);
	}
	public void addMessage(Message msg)
	{
		if (!messages.contains(msg))
			messages.add(msg);
	}

	@AllArgsConstructor
	@EqualsAndHashCode
	public class LineSection 
	{
		@Getter @Setter private Long lineId;
		@Getter @Setter private Long startStopId;
		@Getter @Setter private Long endStopId;

	}

	@AllArgsConstructor
	@EqualsAndHashCode
	public class Message 
	{
		@Getter @Setter private String type;
		@Getter @Setter private String lang;
		@Getter @Setter private String text;
	}

	@Override
	public String toURL() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T extends NeptuneObject> boolean compareAttributes(T another) {
		// TODO Auto-generated method stub
		return false;
	}


}
