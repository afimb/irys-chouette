/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.model;

import java.io.Serializable;
//import org.hibernate.annotations.Entity;

/**
 *
 * @author marc
 */
public class DatedVehicleJourneyNeptune implements Serializable {
    public DatedVehicleJourneyNeptune() {}
       
    Long id;
    private String datedVehicleJourneyNeptuneRef;
    private String journeyPatternNeptuneRef;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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
