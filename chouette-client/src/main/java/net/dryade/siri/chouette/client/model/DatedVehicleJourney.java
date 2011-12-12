/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.model;

import java.io.Serializable;
import net.dryade.siri.sequencer.model.MonitoredVisit;
//import org.hibernate.annotations.Entity;

/**
 *
 * @author marc
 */
public class DatedVehicleJourney implements Serializable {
    public DatedVehicleJourney() {}
    
    public DatedVehicleJourney( MonitoredVisit monitoredVisit) {
        setDatedVehicleJourneyRef( monitoredVisit.getDatedVehicleJourneyRef());
    }
    
    Long id;
    private String datedVehicleJourneyRef;
    private String journeyPatternRef;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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
