/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client;

import net.dryade.siri.sequencer.model.MonitoredVisit;
import net.dryade.siri.sequencer.model.StopMonitoringNotificationResponse;

/**
 *
 * @author marc
 */
public class StopMonitoringService {
    public void update(StopMonitoringNotificationResponse stopMonitoring) {
        for ( MonitoredVisit visit : stopMonitoring.getMonitoredVisits()) {
            //visit
        }
    }
}
