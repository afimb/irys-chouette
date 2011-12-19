/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.adapter.MonitoredVisitAdapter;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import net.dryade.siri.chouette.client.model.DatedVehicleJourneyNeptune;
import net.dryade.siri.sequencer.model.MonitoredVisit;
import org.junit.Before;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/persistenceConfig.xml"})
@TransactionConfiguration(transactionManager="myTxManager", defaultRollback=false)
@Transactional
public class DatedVehicleJourneyDaoTest {
    private DatedVehicleJourneyDao dvjDAO;
    private MonitoredVisitAdapter mvAdapter;
    
    private MonitoredVisit monitoredVisit;
    
    @Before
    public void setUp() {
        dvjDAO.deleteAll();
        
        monitoredVisit = DomainObjectBuilder.aNew().monitoredVisitBuilder().
                        withDatedVehicleJourneyRef( "AAA:eazeaz:azeaze:azeaze").
                        build();
    }
    
    
    @Test
    public void testMessagePersistence() throws Exception {
        DatedVehicleJourneyNeptune datedVehicleJourney = this.mvAdapter.read( monitoredVisit);
        
        this.dvjDAO.save( datedVehicleJourney);
        
        // TODO: use originAimedDepartureTime when method will be defined
        DatedVehicleJourneyNeptune retrieveData = this.dvjDAO.get( monitoredVisit.getDatedVehicleJourneyRef(), monitoredVisit.getAimedDepartureTime());
        
        assertNotNull("should have retreive persisted instance", retrieveData);
    }

    @Autowired 
    public void setMvAdapter(MonitoredVisitAdapter mvAdapter) {
        this.mvAdapter = mvAdapter;
    }

    @Autowired 
    public void setDVJDAO(DatedVehicleJourneyDao dvjDAO) {
        this.dvjDAO = dvjDAO;
    }
   
}
