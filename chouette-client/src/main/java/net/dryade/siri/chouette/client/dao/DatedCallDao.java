/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.model.DatedCall;
import net.dryade.siri.chouette.client.model.DatedVehicleJourney;

/**
 *
 * @author marc
 */
public interface DatedCallDao {
       
    void save(DatedCall datedCall);

    DatedCall get(Long datedVehicleJourneyId, String stopPointNeptuneRef);
 
    void deleteAll();
}
