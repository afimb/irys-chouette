/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;

import java.util.Calendar;

import irys.siri.realtime.model.MonitoredVisit;
import irys.siri.realtime.model.type.VisitStatus;

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

    // TODO: add originAimedDepartureTime when method will be defined on MonitoredVisit
    private Calendar aimedDepartureTime;
    
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
                VisitStatus.onTime,
                departureTime);
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
            VisitStatus departureStatus,
            Calendar aimedDepartureTime) 
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
        
        this.aimedDepartureTime = aimedDepartureTime;
    }
    
    public MonitoredVisitBuilder withDatedVehicleJourneyRef( String datedVehicleJourneyRef) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus, aimedDepartureTime);
    }
    
    public MonitoredVisitBuilder withExpectedDepartureTime( Calendar expectedDepartureTime) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus, aimedDepartureTime);
    }
    
    public MonitoredVisitBuilder withDepartureStatus( VisitStatus departureStatus) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus, aimedDepartureTime);
    }
    
    public MonitoredVisitBuilder withExpectedArrivalTime( Calendar expectedArrivalTime) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus, aimedDepartureTime);
    }
    
    public MonitoredVisitBuilder withArrivalStatus( VisitStatus arrivalStatus) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus, aimedDepartureTime);
    }
    
    public MonitoredVisitBuilder withAimedDepartureTime( Calendar aimedDepartureTime) {
        return new MonitoredVisitBuilder( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus, aimedDepartureTime);
    }
    
    public MonitoredVisit build() {
        MonitoredVisit monitoredVisit = new MonitoredVisit( lineRef, datedVehicleJourneyRef,
            journeyPatternRef, stopPointRef, order, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
        monitoredVisit.setAimedDepartureTime(aimedDepartureTime);
        return monitoredVisit;
    }
}
