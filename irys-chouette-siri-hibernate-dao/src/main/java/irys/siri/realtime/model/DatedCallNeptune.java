/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import irys.siri.realtime.model.type.VisitStatus;

/**
 *
 * @author marc
 */
public class DatedCallNeptune implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4080705701124127408L;
	
	// db key
    @Getter @Setter private Long datedVehicleJourneyId;
    @Getter @Setter private String stopPointNeptuneRef;
    
    @Getter @Setter private String datedVehicleJourneyNeptuneRef;
    @Getter @Setter private Calendar expectedDepartureTime;
    @Getter @Setter private VisitStatus departureStatus;
    @Getter @Setter private Calendar expectedArrivalTime;
    @Getter @Setter private VisitStatus arrivalStatus;
    @Getter @Setter private boolean isDeparture ;
    @Getter @Setter private int position;
    @Getter @Setter private boolean isArrival;
    @Getter @Setter private Calendar aimedArrivalTime ;
    @Getter @Setter private Calendar aimedDepartureTime ;
    
    public DatedCallNeptune() {}
    
    public DatedCallNeptune(DatedVehicleJourneyNeptune dvj, VehicleJourneyAtStop vjas) 
    {
    	datedVehicleJourneyId=dvj.getId();
    	stopPointNeptuneRef=vjas.getStopPoint().getObjectId();
    	datedVehicleJourneyNeptuneRef = dvj.getDatedVehicleJourneyRef();
    	expectedDepartureTime = toRTtime(dvj,vjas.getDepartureTime());
    	expectedArrivalTime = toRTtime(dvj,vjas.getArrivalTime());
    	aimedDepartureTime = expectedDepartureTime;
    	aimedArrivalTime = expectedDepartureTime;
    	isDeparture = vjas.isDeparture();
    	isArrival = vjas.isArrival();
    	position = (int) vjas.getOrder();
	}

	private Calendar toRTtime(DatedVehicleJourneyNeptune dvj, Time time) 
	{
    	Calendar c = Calendar.getInstance();
    	c.setTime(dvj.getOriginAimedDepartureTime().getTime());
    	long seconds = time.getTime()/1000;
    	long minutes = seconds / 60;
    	long hours = minutes / 24;
    	minutes %= 24;
    	seconds %= 60;
    	c.set(Calendar.HOUR_OF_DAY,(int) hours);
    	c.set(Calendar.MINUTE,(int) minutes);
    	c.set(Calendar.SECOND,(int) seconds);
    	c.set(Calendar.MILLISECOND,0);
		return c;
	}

	@Override
    public int hashCode()
    {
        return (datedVehicleJourneyId + ":" + stopPointNeptuneRef).hashCode();
    }
    
    @Override
    public boolean equals( Object other)
    {
        if ( !( other instanceof DatedCallNeptune))
            return false;

        return datedVehicleJourneyId.equals( ( ( DatedCallNeptune)other).datedVehicleJourneyId)
                && stopPointNeptuneRef.equals( ( ( DatedCallNeptune)other).stopPointNeptuneRef);
    }

}
