/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.model;

import java.io.Serializable;
import java.util.Calendar;
import net.dryade.siri.sequencer.model.MonitoredVisit;
import net.dryade.siri.sequencer.model.type.VisitStatus;

/**
 *
 * @author marc
 */
public class DatedCall implements Serializable {
    
    // db key
    private Long datedVehicleJourneyId;
    private String stopPointRef;
    
    private String datedVehicleJourneyRef;
    private Calendar expectedDepartureTime;
    private VisitStatus departureStatus;
    private Calendar expectedArrivalTime;
    private VisitStatus arrivalStatus;
    private String journeyPatternRef;
    
    public DatedCall() {}
    
    public DatedCall( Long datedVehicleJourneyId, MonitoredVisit monitoredVisit) {
        
        // key
        setDatedVehicleJourneyId( datedVehicleJourneyId);
        setStopPointRef( monitoredVisit.getStopPointRef());
        
        setDatedVehicleJourneyRef( monitoredVisit.getDatedVehicleJourneyRef());
        setExpectedDepartureTime( monitoredVisit.getExpectedDepartureTime());
        setExpectedArrivalTime( monitoredVisit.getExpectedArrivalTime());
        setDepartureStatus( monitoredVisit.getDepartureStatus());
        setArrivalStatus( monitoredVisit.getArrivalStatus());
    }

    /**
     * @return the datedVehicleJourneyId
     */
    public Long getDatedVehicleJourneyId() {
        return datedVehicleJourneyId;
    }

    /**
     * @param datedVehicleJourneyId the datedVehicleJourneyId to set
     */
    public void setDatedVehicleJourneyId(Long datedVehicleJourneyId) {
        this.datedVehicleJourneyId = datedVehicleJourneyId;
    }

    /**
     * @return the stopPointRef
     */
    public String getStopPointRef() {
        return stopPointRef;
    }

    /**
     * @param stopPointRef the stopPointRef to set
     */
    public void setStopPointRef(String stopPointRef) {
        this.stopPointRef = stopPointRef;
    }

    /**
     * @return the datedVehicleJourneyRef
     */
    public String getDatedVehicleJourneyRef() {
        return datedVehicleJourneyRef;
    }

    /**
     * @param datedVehicleJourneyRef the datedVehicleJourneyRef to set
     */
    public void setDatedVehicleJourneyRef(String datedVehicleJourneyRef) {
        this.datedVehicleJourneyRef = datedVehicleJourneyRef;
    }

    /**
     * @return the expectedDepartureTime
     */
    public Calendar getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    /**
     * @param expectedDepartureTime the expectedDepartureTime to set
     */
    public void setExpectedDepartureTime(Calendar expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    /**
     * @return the departureStatus
     */
    public VisitStatus getDepartureStatus() {
        return departureStatus;
    }

    /**
     * @param departureStatus the departureStatus to set
     */
    public void setDepartureStatus(VisitStatus departureStatus) {
        this.departureStatus = departureStatus;
    }

    /**
     * @return the expectedArrivalTime
     */
    public Calendar getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    /**
     * @param expectedArrivalTime the expectedArrivalTime to set
     */
    public void setExpectedArrivalTime(Calendar expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    /**
     * @return the arrivalStatus
     */
    public VisitStatus getArrivalStatus() {
        return arrivalStatus;
    }

    /**
     * @param arrivalStatus the arrivalStatus to set
     */
    public void setArrivalStatus(VisitStatus arrivalStatus) {
        this.arrivalStatus = arrivalStatus;
    }

    /**
     * @return the journeyPatternRef
     */
    public String getJourneyPatternRef() {
        return journeyPatternRef;
    }

    /**
     * @param journeyPatternRef the journeyPatternRef to set
     */
    public void setJourneyPatternRef(String journeyPatternRef) {
        this.journeyPatternRef = journeyPatternRef;
    }
    
    
    
}
