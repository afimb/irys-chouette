/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.model;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import irys.siri.realtime.model.type.VisitStatus;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

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
    @Getter @Setter private Long stopPointNeptuneId;
    @Getter @Setter private Calendar lastModificationTime;
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
    
    public DatedCallNeptune()
    {
    	super();
    }
    public DatedCallNeptune(DatedVehicleJourneyNeptune dvj, VehicleJourneyAtStop vjas, DatedCallNeptune previousDC)
    {
    	datedVehicleJourneyId=dvj.getId();
    	StopPoint sp = vjas.getStopPoint();
    	stopPointNeptuneId=sp.getId();
    	datedVehicleJourneyNeptuneRef = dvj.getDatedVehicleJourneyRef();
    	aimedDepartureTime = toRTtime(dvj,vjas.getDepartureTime());
    	aimedArrivalTime = toRTtime(dvj,vjas.getArrivalTime());
    	if (previousDC != null)
    	{
    		if (aimedDepartureTime.before(previousDC.getAimedDepartureTime()))
    		{
    			aimedDepartureTime.add(Calendar.DATE, 1);
    		}
    		if (aimedArrivalTime.before(previousDC.getAimedArrivalTime()))
    		{
    			aimedArrivalTime.add(Calendar.DATE, 1);
    		}
    	}
    	expectedDepartureTime = (Calendar) aimedDepartureTime.clone();
    	expectedArrivalTime = (Calendar) aimedArrivalTime.clone();
    	JourneyPattern jp = vjas.getVehicleJourney().getJourneyPattern();
    	isDeparture = jp.getDepartureStopPoint().equals(sp);
    	isArrival = jp.getArrivalStopPoint().equals(sp);
	}
    
	private Calendar toRTtime(DatedVehicleJourneyNeptune dvj, Time time) 
	{
    	Calendar c = Calendar.getInstance();
    	c.setTime(dvj.getOriginAimedDepartureTime().getTime());
    	long seconds = time.getTime()/1000;
    	long minutes = seconds / 60;
    	long hours = minutes / 60;
    	minutes %= 60;
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
        return (datedVehicleJourneyId + ":" + stopPointNeptuneId).hashCode();
    }
    
    @Override
    public boolean equals( Object other)
    {
        if ( !( other instanceof DatedCallNeptune))
            return false;

        return datedVehicleJourneyId.equals( ( ( DatedCallNeptune)other).datedVehicleJourneyId)
                && stopPointNeptuneId.equals( ( ( DatedCallNeptune)other).stopPointNeptuneId);
    }

}
