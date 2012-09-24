/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import irys.siri.chouette.client.adapter.MonitoredVisitAdapter;
import irys.siri.realtime.dao.DatedCallDao;
import irys.siri.realtime.dao.DatedVehicleJourneyDao;
import irys.siri.realtime.model.DatedCallNeptune;
import irys.siri.realtime.model.DatedVehicleJourneyNeptune;
import irys.siri.realtime.model.MonitoredVisit;
import irys.siri.realtime.model.type.VisitStatus;
import org.junit.Before;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
@TransactionConfiguration(transactionManager="myTxManager", defaultRollback=false)
@Transactional
public class DatedCallDaoTest {
	private static int count = 1; 
    private DatedCallDao dcDAO;
    private DatedVehicleJourneyDao dvjDAO;
    private MonitoredVisitAdapter mvAdapter;
    
    private DatedVehicleJourneyNeptune datedVehicleJourney;
    
    @Before
    public void setUp() {
        dvjDAO.deleteAll();
        dcDAO.deleteAll();
        
        MonitoredVisit monitoredVisit = DomainObjectBuilder.aNew().monitoredVisitBuilder().
                        withDatedVehicleJourneyRef( "AAA:eazeaz:azeaze:azeazz"+count).
                        build();
        count++;
        datedVehicleJourney = this.mvAdapter.read( monitoredVisit);
        
        this.dvjDAO.save( datedVehicleJourney);
    }
    
    
    @Test
    public void testMessagePersistence() throws Exception {
        DatedCallNeptune datedCall = DomainObjectBuilder.aNew().datedCallBuilder().
                withDatedVehicleJourneyId( datedVehicleJourney.getId()).
                withArrivalStatus(VisitStatus.arrived).
                withStopPointNeptuneRef( "my_stop_ref").
                build();
                
        this.dcDAO.save( datedCall);
        
        DatedCallNeptune retrieveData = this.dcDAO.get( datedVehicleJourney.getId(),
                                                 "my_stop_ref");
        
        assertNotNull("should have retreive persisted instance", retrieveData);
        assertEquals("should have retreive properties", VisitStatus.arrived, retrieveData.getArrivalStatus());
    }

    @Autowired 
    public void setMvAdapter(MonitoredVisitAdapter mvAdapter) {
        this.mvAdapter = mvAdapter;
    }

    @Autowired 
    public void setDVJDAO(DatedVehicleJourneyDao dvjDAO) {
        this.dvjDAO = dvjDAO;
    }

    @Autowired 
    public void setDCDAO(DatedCallDao dcDAO) {
        this.dcDAO = dcDAO;
    }
   
}
