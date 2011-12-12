/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;

import java.util.Calendar;
import net.dryade.siri.sequencer.model.MonitoredVisit;
import net.dryade.siri.sequencer.model.type.VisitStatus;

/**
 *
 * @author marc
 */
public class MonitoredVisitBuilder {
    private String lineRef;
    private String datedVehicleJourneyRef;
    private String journeyPatternRef;
    private String stopPointRef;
    private int order;
    
    private Calendar expectedDepartureTime;
    private Calendar expectedArrivalTime;
    
    private VisitStatus arrivalStatus;
    private VisitStatus departureStatus;
    
    public static MonitoredVisitBuilder create() {
        Calendar departureTime = Calendar.getInstance();
        departureTime.add( Calendar.MINUTE, 3);
        
        Calendar arrivalTime = Calendar.getInstance();
        arrivalTime.add( Calendar.MINUTE, 2);
        
        return new MonitoredVisitBuilder( 
                "myLineRef",
                "myDatedVehicleJourneyRef",
                "myJourneyPatternRef",
                "myStopPointRef",
                1,
                departureTime,
                arrivalTime,
                VisitStatus.early,
                VisitStatus.onTime);
    }
    
    public MonitoredVisitBuilder(
            String lineRef,
            String datedVehicleJourneyRef,
            String journeyPatternRef,
            String stopPointRef,
            int order,
            Calendar expectedDepartureTime,
            Calendar expectedArrivalTime,
            VisitStatus arrivalStatus,
            VisitStatus departureStatus) 
    {
        this.lineRef = lineRef;
        this.datedVehicleJourneyRef = datedVehicleJourneyRef;
        this.journeyPatternRef = journeyPatternRef;
        this.stopPointRef = stopPointRef;
        this.order = order;

        this.expectedDepartureTime = expectedDepartureTime;
        this.expectedArrivalTime = expectedArrivalTime;

        this.arrivalStatus = arrivalStatus;
        this.departureStatus = departureStatus;
    }
    
    public MonitoredVisitBuilder withDatedVehicleJourneyRef( String datedVehicleJourneyRef) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public MonitoredVisit build() {
        return new MonitoredVisit( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
}
