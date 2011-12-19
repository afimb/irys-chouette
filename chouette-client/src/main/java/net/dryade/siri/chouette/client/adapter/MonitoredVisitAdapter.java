/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.adapter;

import net.dryade.siri.chouette.client.model.DatedCallNeptune;
import net.dryade.siri.chouette.client.model.DatedVehicleJourneyNeptune;
import net.dryade.siri.sequencer.model.MonitoredVisit;
import net.dryade.siri.sequencer.model.type.VisitStatus;

/**
 *
 * @author marc
 */
public class MonitoredVisitAdapter {
    public MonitoredVisitAdapter() {};
    
  public String vehicleJourneyNeptuneRef( String vehicleJourneyRef)
  {
    return vehicleJourneyRef;
  }
  public String stopPointNeptuneRef( String stopPointRef)
  {
    return stopPointRef;
  }
  
  public DatedVehicleJourneyNeptune read( MonitoredVisit visit)
  {
    DatedVehicleJourneyNeptune dvj = new DatedVehicleJourneyNeptune();
    dvj.setDatedVehicleJourneyNeptuneRef( vehicleJourneyNeptuneRef( visit.getDatedVehicleJourneyRef()));
    // TODO: use OriginAimedDepartureTime when method will be defined on MonitoredVisit
    dvj.setOriginAimedDepartureTime( visit.getAimedDepartureTime());
    return dvj;
  }
  
  public DatedCallNeptune read( Long datedVehicleJourneyId, MonitoredVisit visit)
  {
    DatedCallNeptune datedCall = new DatedCallNeptune();
    datedCall.setDatedVehicleJourneyNeptuneRef( stopPointNeptuneRef(visit.getStopPointRef()));
    datedCall.setDatedVehicleJourneyId( datedVehicleJourneyId);
    
    updateDatedCall( datedCall, visit);
    return datedCall;
  }
  
  public void updateDatedCall( DatedCallNeptune datedCall, MonitoredVisit visit)
  {
      datedCall.setArrivalStatus( visit.getArrivalStatus());
      datedCall.setDepartureStatus( visit.getDepartureStatus());
      datedCall.setExpectedArrivalTime( visit.getExpectedArrivalTime());
      datedCall.setExpectedDepartureTime( visit.getExpectedDepartureTime());
  }
}
