/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.model.DatedVehicleJourneyNeptune;

/**
 *
 * @author marc
 */
public interface DatedVehicleJourneyDao {
       
    void save(DatedVehicleJourneyNeptune datedVehicleJourney);

    DatedVehicleJourneyNeptune get(String datedVehicleJourneyNeptuneRef);
 
    void deleteAll();
}
