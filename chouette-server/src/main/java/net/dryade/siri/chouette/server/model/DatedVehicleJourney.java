package net.dryade.siri.chouette.server.model;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

@SuppressWarnings("serial")
public class DatedVehicleJourney extends NeptuneIdentifiedObject
{
	@Getter @Setter private Date calendarDate;
	@Getter @Setter private String lineId;
	@Getter @Setter private String routeId;
	@Getter @Setter private String journeyPatternId;
	@Getter @Setter private String vehicleJourneyId;
	@Getter @Setter private String companyId;
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

}
