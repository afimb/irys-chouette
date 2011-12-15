/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.adapter;

import java.util.Calendar;
import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import net.dryade.siri.chouette.client.model.DatedCallNeptune;
import net.dryade.siri.sequencer.model.MonitoredVisit;
import net.dryade.siri.sequencer.model.type.VisitStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/persistenceConfig.xml"})
public class MonitoredVisitAdapterTest {
    private MonitoredVisitAdapter adapter = null;

    @Test
    public void updateDatedCall() throws Exception {
        Calendar d1 = Calendar.getInstance();
        
        Calendar d2 = (Calendar)d1.clone();
        d2.add( Calendar.MINUTE, 4);
        
        Calendar d3 = (Calendar)d2.clone();
        d3.add( Calendar.MINUTE, 8);        
        
        Calendar d4 = (Calendar)d2.clone();
        d4.add( Calendar.MINUTE, 12);
        
        DatedCallNeptune datedCall = DomainObjectBuilder.aNew().datedCallBuilder().
                withArrivalStatus(VisitStatus.arrived).
                withDepartureStatus(VisitStatus.cancelled).
                withExpectedArrivalTime(d1).
                withExpectedDepartureTime(d2).
                build();
                

        MonitoredVisit mv = DomainObjectBuilder.aNew().
                monitoredVisitBuilder().
                withArrivalStatus(VisitStatus.early).
                withDepartureStatus(VisitStatus.noReport).
                withExpectedArrivalTime(d3).
                withExpectedDepartureTime(d4).
                build();
                
        
        adapter.updateDatedCall(datedCall, mv);
        
        assertEquals("should copy ArrivalStatus",datedCall.getArrivalStatus(),mv.getArrivalStatus());
        assertEquals("should copy DepartureStatus",datedCall.getDepartureStatus(),mv.getDepartureStatus());
        assertEquals("should copy ArrivalTime",datedCall.getExpectedArrivalTime(),mv.getExpectedArrivalTime());
        assertEquals("should copy ExpectedDepartureTime",datedCall.getExpectedDepartureTime(),mv.getExpectedDepartureTime());
    }

    @Autowired 
    public void setAdapter(MonitoredVisitAdapter adapter) {
        this.adapter = adapter;
    }
}
