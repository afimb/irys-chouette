/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.dao.InfoMessageDaoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import net.dryade.siri.sequencer.model.InfoMessage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

//@RunWith(SpringJUnit4ClassRunner.class)

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/persistenceConfig.xml"})
@TransactionConfiguration(transactionManager="myTxManager", defaultRollback=false)
@Transactional
public class GeneralMessageDaoTest  {
    private InfoMessageDaoImpl gmDAO = null;
    

    @Test
    public void testLoadTitle() throws Exception {
        this.gmDAO.save( getInfoMessage());
    }
    
    public InfoMessage getInfoMessage()
    {
        return DomainObjectBuilder.aNew().infoMessage().build();
    }

    @Autowired 
    public void setGmDAO(InfoMessageDaoImpl gmDAO) {
        this.gmDAO = gmDAO;
    }
}
