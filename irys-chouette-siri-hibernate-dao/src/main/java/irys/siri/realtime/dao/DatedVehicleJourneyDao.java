/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.dao;

import irys.siri.realtime.model.DatedVehicleJourneyNeptune;

import java.util.Calendar;

/**
 *
 * @author marc
 */
public interface DatedVehicleJourneyDao {
       
    void save(DatedVehicleJourneyNeptune datedVehicleJourney);

    DatedVehicleJourneyNeptune get(String datedVehicleJourneyNeptuneRef, Calendar originAimedDepartureTime);
 
    void deleteAll();
}
