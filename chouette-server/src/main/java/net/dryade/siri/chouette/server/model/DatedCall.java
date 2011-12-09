package net.dryade.siri.chouette.server.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

public class DatedCall
{
	@Getter @Setter private DatedVehicleJourney vehicleJourney;
	@Getter @Setter private String stopPointId;
	@Getter @Setter private Timestamp expectedArrivalTime;
	@Getter @Setter private Timestamp expectedDepartureTime;
	@Getter @Setter private Timestamp aimedArrivalTime;
	@Getter @Setter private Timestamp aimedDepartureTime;
	@Getter @Setter private String status;
	@Getter @Setter private int position;
	@Getter @Setter private boolean isDeparture;
	@Getter @Setter private boolean isArrival;

	private static final DateFormat timeFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("stopPointId = "+stopPointId+"\n");
		builder.append("expectedArrivalTime = "+timeFormater.format(expectedArrivalTime)+"\n");
		builder.append("expectedDepartureTime = "+timeFormater.format(expectedDepartureTime)+"\n");
		builder.append("aimedArrivalTime = "+timeFormater.format(aimedArrivalTime)+"\n");
		builder.append("aimedDepartureTime = "+timeFormater.format(aimedDepartureTime)+"\n");
		builder.append("status = "+status+"\n");
		builder.append("position = "+position+"\n");
		builder.append("isDeparture = "+isDeparture+"\n");
		builder.append("isArrival = "+isArrival+"\n");
		if (vehicleJourney != null)
		{
			builder.append("vehicleJourney : \n");
			builder.append(vehicleJourney.toString("   ", 1));
		}
		return builder.toString();
	}

}
