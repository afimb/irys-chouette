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
	@Getter @Setter private String datedVehicleJourneyRef;
	@Getter @Setter private String lineRef;
	@Getter @Setter private String routeRef;
	@Getter @Setter private String journeyPatternRef;
	@Getter @Setter private String vehicleJourneyRef;
	@Getter @Setter private String companyRef;
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
	
    public DatedVehicleJourneyNeptune() {}
    
	public DatedVehicleJourneyNeptune(VehicleJourney vj) 
	{
		this.setVehicleJourneyRef(vj.getObjectId());
		this.setDatedVehicleJourneyRef(vj.getObjectId());
		this.setJourneyPatternRef(vj.getJourneyPattern().getObjectId());
		this.setRouteRef(vj.getRoute().getObjectId());
		this.setLineRef(vj.getRoute().getLine().getObjectId());
		if (vj.getCompany() != null)
		{
			this.setCompanyRef(vj.getCompany().getObjectId());
		}
		else
		{
			this.setCompanyRef(vj.getRoute().getLine().getCompany().getObjectId());
		}
		this.setNumber((int) vj.getNumber());
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
