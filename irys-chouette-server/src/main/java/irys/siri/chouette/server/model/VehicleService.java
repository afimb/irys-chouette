package irys.siri.chouette.server.model;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

@SuppressWarnings("serial")
public class VehicleService  extends NeptuneIdentifiedObject
{
	@Getter @Setter private Date calendarDate;
    @Getter @Setter private Vehicle vehicle;
}
