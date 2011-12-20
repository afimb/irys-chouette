/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client;

import net.dryade.siri.chouette.client.adapter.MonitoredVisitAdapter;
import net.dryade.siri.chouette.client.dao.DatedCallDao;
import net.dryade.siri.chouette.client.dao.DatedVehicleJourneyDao;
import net.dryade.siri.chouette.client.model.DatedCallNeptune;
import net.dryade.siri.chouette.client.model.DatedVehicleJourneyNeptune;
import net.dryade.siri.sequencer.model.MonitoredVisit;
import net.dryade.siri.sequencer.model.StopMonitoringNotificationResponse;

/**
 *
 * @author marc
 */
public class StopMonitoringService {
    private DatedCallDao datedCallDao;
    private DatedVehicleJourneyDao datedVehicleJourneyDao;
    private MonitoredVisitAdapter monitoredVisitAdapter;
    
    public void update(StopMonitoringNotificationResponse stopMonitoring) {
        for ( MonitoredVisit visit : stopMonitoring.getMonitoredVisits()) {
            //visit
            DatedVehicleJourneyNeptune datedVehicleJourney = retrieveDatedVehicleJourney( visit);
            
            if ( datedVehicleJourney==null)
            {
                datedVehicleJourney = monitoredVisitAdapter.read( visit);
                datedVehicleJourneyDao.save( datedVehicleJourney);
                datedVehicleJourney = retrieveDatedVehicleJourney( visit);
                if (datedVehicleJourney == null)
                {
                	throw new RuntimeException("datedVehicleJourney insertion failed");
                }
            }
            
            DatedCallNeptune datedCall = retrieveDatedCall( datedVehicleJourney.getId(), visit);
            
            if ( datedCall==null)
                datedCall = monitoredVisitAdapter.read( datedVehicleJourney.getId(), visit);
            
            monitoredVisitAdapter.updateDatedCall(datedCall, visit);
            datedCallDao.save( datedCall);
        }
    }
    
    public DatedVehicleJourneyNeptune retrieveDatedVehicleJourney( MonitoredVisit visit)
    {
        String vehicleJourneyNeptuneRef = monitoredVisitAdapter.vehicleJourneyNeptuneRef( visit.getDatedVehicleJourneyRef());

        // TODO: use originAimedDepartureTime on Visit when method will be defined
        return datedVehicleJourneyDao.get( vehicleJourneyNeptuneRef, visit.getAimedDepartureTime());
    }
    
    public DatedCallNeptune retrieveDatedCall( Long datedVehicleJourneyId, MonitoredVisit visit)
    {
        String stopPointNeptuneRef = monitoredVisitAdapter.stopPointNeptuneRef( visit.getStopPointRef());

        return datedCallDao.get( datedVehicleJourneyId, stopPointNeptuneRef);
    }

    /**
     * @return the datedVehicleJourneyDao
     */
    public DatedVehicleJourneyDao getDatedVehicleJourneyDao() {
        return datedVehicleJourneyDao;
    }

    /**
     * @param datedVehicleJourneyDao the datedVehicleJourneyDao to set
     */
    public void setDatedVehicleJourneyDao(DatedVehicleJourneyDao datedVehicleJourneyDao) {
        this.datedVehicleJourneyDao = datedVehicleJourneyDao;
    }

    /**
     * @return the monitoredVisitAdapter
     */
    public MonitoredVisitAdapter getMonitoredVisitAdapter() {
        return monitoredVisitAdapter;
    }

    /**
     * @param monitoredVisitAdapter the monitoredVisitAdapter to set
     */
    public void setMonitoredVisitAdapter(MonitoredVisitAdapter monitoredVisitAdapter) {
        this.monitoredVisitAdapter = monitoredVisitAdapter;
    }

    /**
     * @return the datedCallDao
     */
    public DatedCallDao getDatedCallDao() {
        return datedCallDao;
    }

    /**
     * @param datedCallDao the datedCallDao to set
     */
    public void setDatedCallDao(DatedCallDao datedCallDao) {
        this.datedCallDao = datedCallDao;
    }
    
}
