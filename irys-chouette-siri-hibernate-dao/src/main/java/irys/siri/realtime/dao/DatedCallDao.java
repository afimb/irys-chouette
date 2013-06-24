/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.dao;

import irys.siri.realtime.model.DatedCallNeptune;

/**
 *
 * @author marc
 */
public interface DatedCallDao {
       
    void save(DatedCallNeptune datedCall);

    DatedCallNeptune get(Long datedVehicleJourneyId, Long stopPointNeptuneId);
 
    void deleteAll();
}
