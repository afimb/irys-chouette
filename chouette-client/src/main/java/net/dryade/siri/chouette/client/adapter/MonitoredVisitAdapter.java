/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.adapter;

import net.dryade.siri.chouette.client.model.DatedCall;
import net.dryade.siri.chouette.client.model.DatedVehicleJourney;
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
  
  public DatedVehicleJourney read( MonitoredVisit visit)
  {
    DatedVehicleJourney dvj = new DatedVehicleJourney();
    dvj.setDatedVehicleJourneyNeptuneRef( vehicleJourneyNeptuneRef( visit.getDatedVehicleJourneyRef()));
    return dvj;
  }
  
  public DatedCall read( Long datedVehicleJourneyId, MonitoredVisit visit)
  {
    DatedCall datedCall = new DatedCall();
    return datedCall;
  }
  
  public void updateDatedCall( DatedCall datedCall, MonitoredVisit visit)
  {
      datedCall.setArrivalStatus( visit.getArrivalStatus());
      datedCall.setDepartureStatus( visit.getDepartureStatus());
      datedCall.setExpectedArrivalTime( visit.getExpectedArrivalTime());
      datedCall.setExpectedDepartureTime( visit.getExpectedDepartureTime());
  }
}
