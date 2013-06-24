/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.VehicleJourney;

/**
 *
 * @author marc
 */
public class DatedVehicleJourneyNeptune implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3052029251737937798L;


	@Getter @Setter private Long id;
	@Getter @Setter private Calendar originAimedDepartureTime;
	@Getter @Setter private Calendar lastModificationTime;
	@Getter @Setter private String datedVehicleJourneyRef;
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
	@Getter @Setter private Integer number;
	@Getter @Setter private Integer objectVersion;
	@Getter @Setter private String facility;
	@Getter @Setter private String comment;
	@Getter @Setter private Date creationTime;
	//@Getter @Setter private VehicleService service;
	@Getter @Setter private long serviceOrder;

	public DatedVehicleJourneyNeptune() {super();}

	public DatedVehicleJourneyNeptune(VehicleJourney vj) 
	{
		this.setVehicleJourneyId(vj.getId());
		this.setDatedVehicleJourneyRef(vj.getObjectId());
		this.setJourneyPatternId(vj.getJourneyPattern().getId());
		this.setRouteId(vj.getRoute().getId());
		this.setLineId(vj.getRoute().getLine().getId());
		if (vj.getCompany() != null)
		{
			this.setCompanyId(vj.getCompany().getId());
		}
		else
		{
			this.setCompanyId(vj.getRoute().getLine().getCompany().getId());
		}
		if (vj.getNumber()!= null)
			this.setNumber((int) vj.getNumber().longValue());
		else
			this.setNumber(0);
		this.setObjectVersion(vj.getObjectVersion());
		this.setPublishedJourneyIdentifier(vj.getPublishedJourneyIdentifier());
		this.setPublishedJourneyName(vj.getPublishedJourneyName());
		if (vj.getTransportMode() == null)
		{
			this.setTransportMode(vj.getRoute().getLine().getTransportModeName().name());
		}
		else
		{
			this.setTransportMode(vj.getTransportMode().name());
		}
		this.setVehicleTypeIdentifier(vj.getVehicleTypeIdentifier());
		this.setComment(vj.getComment());
		if (vj.getServiceStatusValue() != null)
			this.setStatus(vj.getServiceStatusValue().name());
		this.setCreationTime(Calendar.getInstance().getTime());
	}

}
