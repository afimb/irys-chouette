/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.model;

import java.io.Serializable;
import java.util.Calendar;
import net.dryade.siri.sequencer.model.type.VisitStatus;

/**
 *
 * @author marc
 */
public class DatedCall implements Serializable {
    
    // db key
    private Long datedVehicleJourneyId;
    private String stopPointNeptuneRef;
    
    private String datedVehicleJourneyNeptuneRef;
    private Calendar expectedDepartureTime;
    private VisitStatus departureStatus;
    private Calendar expectedArrivalTime;
    private VisitStatus arrivalStatus;
    private String journeyPatternNeptuneRef;
    
    public DatedCall() {}
    
    @Override
    public int hashCode()
    {
        return (datedVehicleJourneyId + ":" + stopPointNeptuneRef).hashCode();
    }
    
    @Override
    public boolean equals( Object other)
    {
        if ( !( other instanceof DatedCall))
            return false;

        return datedVehicleJourneyId.equals( ( ( DatedCall)other).datedVehicleJourneyId)
                && stopPointNeptuneRef.equals( ( ( DatedCall)other).stopPointNeptuneRef);
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
    public String getStopPointNeptuneRef() {
        return stopPointNeptuneRef;
    }

    /**
     * @param stopPointRef the stopPointRef to set
     */
    public void setStopPointNeptuneRef(String stopPointNeptuneRef) {
        this.stopPointNeptuneRef = stopPointNeptuneRef;
    }

    /**
     * @return the datedVehicleJourneyRef
     */
    public String getDatedVehicleJourneyNeptuneRef() {
        return datedVehicleJourneyNeptuneRef;
    }

    /**
     * @param datedVehicleJourneyRef the datedVehicleJourneyRef to set
     */
    public void setDatedVehicleJourneyNeptuneRef(String datedVehicleJourneyNeptuneRef) {
        this.datedVehicleJourneyNeptuneRef = datedVehicleJourneyNeptuneRef;
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
    public String getJourneyPatternNeptuneRef() {
        return journeyPatternNeptuneRef;
    }

    /**
     * @param journeyPatternRef the journeyPatternRef to set
     */
    public void setJourneyPatternNeptuneRef(String journeyPatternNeptuneRef) {
        this.journeyPatternNeptuneRef = journeyPatternNeptuneRef;
    }
    
    
    
}
