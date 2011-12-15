/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.model.DatedCallNeptune;
import net.dryade.siri.chouette.client.model.DatedVehicleJourneyNeptune;

/**
 *
 * @author marc
 */
public interface DatedCallDao {
       
    void save(DatedCallNeptune datedCall);

    DatedCallNeptune get(Long datedVehicleJourneyId, String stopPointNeptuneRef);
 
    void deleteAll();
}
