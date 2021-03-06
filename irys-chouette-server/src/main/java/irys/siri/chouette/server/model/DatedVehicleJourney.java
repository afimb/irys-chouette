package irys.siri.chouette.server.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

@SuppressWarnings("serial")
public class DatedVehicleJourney extends NeptuneIdentifiedObject
{
	private static final DateFormat timeFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@Getter @Setter private Date calendarDate;
	@Getter @Setter private Timestamp lastModificationDate ;
	@Getter @Setter private Long lineId;
	@Getter @Setter private Long routeId;
	@Getter @Setter private Long journeyPatternId;
	@Getter @Setter private Long vehicleJourneyId;
	@Getter @Setter private Long companyId;
	@Getter @Setter private String publishedJourneyName;
	@Getter @Setter private String publishedJourneyIdentifier;
	@Getter @Setter private String transportMode;
	@Getter @Setter private String vehicleTypeIdentifier;
	@Getter @Setter private String status;
	@Getter @Setter private int number;
	@Getter @Setter private VehicleService service;
	@Getter @Setter private long serviceOrder;
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
	 */
	@Override
	public String toString(String indent, int level) 
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(super.toString(indent, level));
		builder.append("lastModificationDate = "+dateToString(lastModificationDate)+"\n");
		builder.append("lineId = "+lineId+"\n");
		builder.append("routeId = "+routeId+"\n");
		builder.append("journeyPatternId = "+journeyPatternId+"\n");
		builder.append("vehicleJourneyId = "+vehicleJourneyId+"\n");
		builder.append("companyId = "+companyId+"\n");
		builder.append("publishedJourneyName = "+publishedJourneyName+"\n");
		builder.append("publishedJourneyIdentifier = "+publishedJourneyIdentifier+"\n");
		builder.append("transportMode = "+transportMode+"\n");
		builder.append("vehicleTypeIdentifier = "+vehicleTypeIdentifier+"\n");
		builder.append("status = "+status+"\n");
		builder.append("number = "+number+"\n");
		
		return builder.toString();
	}
	private static String dateToString(Timestamp t)
	{
		if (t == null) return "null";
		return timeFormater.format(t);
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
