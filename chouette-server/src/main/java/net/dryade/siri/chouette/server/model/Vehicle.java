package net.dryade.siri.chouette.server.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

@SuppressWarnings("serial")
public class Vehicle extends NeptuneIdentifiedObject
{
	@Getter @Setter private Date calendarDate;
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
	@Getter @Setter private Time delay;
}
