package irys.siri.chouette.server.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

@SuppressWarnings("serial")
public class Vehicle extends NeptuneIdentifiedObject
{
	@Getter @Setter private Date calendarDate;
	@Getter @Setter private Timestamp lastModificationDate ;
	@Getter @Setter private String vehicleTypeIdentifier;
	@Getter @Setter private String status;
	@Getter @Setter private boolean inCongestion;
	@Getter @Setter private boolean inPanic;
	@Getter @Setter private BigDecimal longitude;
	@Getter @Setter private BigDecimal latitude;
	@Getter @Setter private String longLatType;
	@Getter @Setter private BigDecimal x;
	@Getter @Setter private BigDecimal y;
	@Getter @Setter private String projectionType;
	@Getter @Setter private boolean monitored;
	@Getter @Setter private String monitoringError;
	@Getter @Setter private BigDecimal bearing;
	@Getter @Setter private Long delay;
	@Getter @Setter private Long link_distance;
	@Getter @Setter private Double link_percentage;
	@Getter @Setter private String message;
	@Getter @Setter private Long current_vehicle_journey_id;
	@Getter @Setter private Long current_stop_id ;
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
