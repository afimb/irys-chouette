/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.adapter;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import lombok.Setter;
import net.dryade.siri.chouette.ChouetteTool;
import net.dryade.siri.chouette.client.model.DatedCallNeptune;
import net.dryade.siri.chouette.client.model.DatedVehicleJourneyNeptune;
import net.dryade.siri.common.SiriException;
import net.dryade.siri.sequencer.model.MonitoredVisit;

/**
 *
 * @author marc
 */
public class MonitoredVisitAdapter {
	private static Logger logger = Logger.getLogger(MonitoredVisitAdapter.class);

	@Setter private ChouetteTool siriTool; 	

	public MonitoredVisitAdapter() {};

	public String vehicleJourneyNeptuneRef( String vehicleJourneyRef)
	{
		if (siriTool == null) throw new RuntimeException("siriTool not set");
		try 
		{
			return siriTool.toNeptuneId(vehicleJourneyRef, ChouetteTool.ID_VEHICLEJOURNEY, NeptuneIdentifiedObject.VEHICLEJOURNEY_KEY);
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+vehicleJourneyRef);
			return vehicleJourneyRef;
		}
	}
	public String stopPointNeptuneRef( String stopPointRef)
	{
		try 
		{
			if (stopPointRef.contains(":SPOR:"))
				return siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPPOINT_KEY);

			return siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPAREA_KEY);
		} 
		catch (SiriException e) 
		{
			logger.warn("invalid id syntax "+stopPointRef);
			return stopPointRef;
		}
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
		datedCall.setStopPointNeptuneRef( stopPointNeptuneRef(visit.getStopPointRef()));
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
