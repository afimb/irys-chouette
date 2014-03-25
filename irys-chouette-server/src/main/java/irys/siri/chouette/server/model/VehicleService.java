package irys.siri.chouette.server.model;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

@SuppressWarnings("serial")
public class VehicleService  extends NeptuneIdentifiedObject
{
	@Getter @Setter private Date calendarDate;
    @Getter @Setter private Vehicle vehicle;
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
