package irys.siri.chouette.server.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

public class DatedCall
{
	@Getter @Setter private Long datedVehicleJourneyId;
	@Getter @Setter private Timestamp lastModificationDate ;
	@Getter @Setter private DatedVehicleJourney vehicleJourney;
	@Getter @Setter private String stopPointId;
	@Getter @Setter private Timestamp expectedArrivalTime;
	@Getter @Setter private Timestamp expectedDepartureTime;
	@Getter @Setter private Timestamp aimedArrivalTime;
	@Getter @Setter private Timestamp aimedDepartureTime;
	@Getter @Setter private String departureStatus;
	@Getter @Setter private String arrivalStatus;
	@Getter @Setter private int position;
	@Getter @Setter private boolean departure;
	@Getter @Setter private boolean arrival;

	private static final DateFormat timeFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("datedVehicleJourneyId = "+datedVehicleJourneyId+"\n");
		builder.append("stopPointId = "+stopPointId+"\n");
		builder.append("lastModificationDate = "+timeFormater.format(lastModificationDate)+"\n");
		builder.append("expectedArrivalTime = "+timeFormater.format(expectedArrivalTime)+"\n");
		builder.append("expectedDepartureTime = "+timeFormater.format(expectedDepartureTime)+"\n");
		builder.append("aimedArrivalTime = "+timeFormater.format(aimedArrivalTime)+"\n");
		builder.append("aimedDepartureTime = "+timeFormater.format(aimedDepartureTime)+"\n");
		builder.append("departureStatus = "+departureStatus+"\n");
		builder.append("arrivalStatus = "+arrivalStatus+"\n");
		builder.append("position = "+position+"\n");
		builder.append("isDeparture = "+departure+"\n");
		builder.append("isArrival = "+arrival+"\n");
		if (vehicleJourney != null)
		{
			builder.append("vehicleJourney : \n");
			builder.append(vehicleJourney.toString("   ", 1));
		}
		return builder.toString();
	}

}
