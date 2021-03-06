/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.siri.chouette.client.factory;

import irys.siri.realtime.model.DatedCallNeptune;
import irys.siri.realtime.model.type.VisitStatus;

import java.util.Calendar;

/**
 *
 * @author marc
 */
public class DatedCallBuilder {
    private Long datedVehicleJourneyId;
    private Long stopPointNeptuneId;
    
    private Calendar expectedDepartureTime;
    private Calendar expectedArrivalTime;
    
    private VisitStatus arrivalStatus;
    private VisitStatus departureStatus;
    
    public static DatedCallBuilder create() {
        Calendar departureTime = Calendar.getInstance();
        departureTime.add( Calendar.MINUTE, 3);
        
        Calendar arrivalTime = Calendar.getInstance();
        arrivalTime.add( Calendar.MINUTE, 2);
        
        return new DatedCallBuilder( 
                123L,
                456L,
                departureTime,
                arrivalTime,
                VisitStatus.early,
                VisitStatus.onTime);
    }
    
    public DatedCallBuilder(
            Long datedVehicleJourneyId,
            Long stopPointNeptuneId,
            Calendar expectedDepartureTime,
            Calendar expectedArrivalTime,
            VisitStatus arrivalStatus,
            VisitStatus departureStatus) 
    {
        this.datedVehicleJourneyId = datedVehicleJourneyId;
        this.stopPointNeptuneId = stopPointNeptuneId;

        this.expectedDepartureTime = expectedDepartureTime;
        this.expectedArrivalTime = expectedArrivalTime;

        this.arrivalStatus = arrivalStatus;
        this.departureStatus = departureStatus;
    }
    
    public DatedCallBuilder withDatedVehicleJourneyId( Long datedVehicleJourneyId) {
        return new DatedCallBuilder( datedVehicleJourneyId,
            stopPointNeptuneId, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public DatedCallBuilder withStopPointNeptuneId( Long stopPointNeptuneId) {
        return new DatedCallBuilder( datedVehicleJourneyId,
            stopPointNeptuneId, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public DatedCallBuilder withExpectedDepartureTime( Calendar expectedDepartureTime) {
        return new DatedCallBuilder( datedVehicleJourneyId,
            stopPointNeptuneId, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public DatedCallBuilder withDepartureStatus( VisitStatus departureStatus) {
        return new DatedCallBuilder( datedVehicleJourneyId,
            stopPointNeptuneId, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public DatedCallBuilder withExpectedArrivalTime( Calendar expectedArrivalTime) {
        return new DatedCallBuilder( datedVehicleJourneyId,
            stopPointNeptuneId, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public DatedCallBuilder withArrivalStatus( VisitStatus arrivalStatus) {
        return new DatedCallBuilder( datedVehicleJourneyId,
            stopPointNeptuneId, expectedDepartureTime,
            expectedArrivalTime, arrivalStatus, departureStatus);
    }
    
    public DatedCallNeptune build() {
        DatedCallNeptune datedCall = new DatedCallNeptune();
        datedCall.setArrivalStatus(arrivalStatus);
        datedCall.setDepartureStatus(departureStatus);
        datedCall.setExpectedArrivalTime(expectedArrivalTime);
        datedCall.setExpectedDepartureTime(expectedDepartureTime);
        
        datedCall.setDatedVehicleJourneyId(datedVehicleJourneyId);
        datedCall.setStopPointNeptuneId(stopPointNeptuneId);
        return datedCall;
    }
}
