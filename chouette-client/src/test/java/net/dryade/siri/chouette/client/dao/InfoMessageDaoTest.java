/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import net.dryade.siri.sequencer.model.InfoMessage;
import net.dryade.siri.sequencer.model.Message;
import org.junit.Before;
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
public class InfoMessageDaoTest  {
    private InfoMessageDaoImpl gmDAO = null;
    private InfoMessage infoMessage;
    private List<Message> messages;
    private Message firstMessage;

    @Before
    public void cleanDB() {
        firstMessage = DomainObjectBuilder.aNew().message().build();
        messages = new ArrayList<Message>();
        messages.add( firstMessage);
        infoMessage = DomainObjectBuilder.aNew().infoMessage().
                        withMessages( messages).
                        build();
        this.gmDAO.deleteAll();
    }

    @Test
    public void testInstanceFoundAfterSave() throws Exception {
        this.gmDAO.save( infoMessage);
        
        InfoMessage retreiveData = this.gmDAO.get( infoMessage.getMessageId());
        assertNotNull("should have retreive persisted instance", retreiveData);
    }

    @Test
    public void testPropertiesPersistence() throws Exception {
        this.gmDAO.save( infoMessage);
        
        InfoMessage retreiveData = this.gmDAO.get( infoMessage.getMessageId());
        assertEquals("should have persisted ValidUntilTime", infoMessage.getValidUntilTime(), retreiveData.getValidUntilTime());
        assertEquals("should have persisted MessageVersion", infoMessage.getMessageVersion(), retreiveData.getMessageVersion());
        assertEquals("should have persisted RecordedAtTime", infoMessage.getRecordedAtTime(), retreiveData.getRecordedAtTime());
    }

    @Test
    public void testMessageListPersistence() throws Exception {
        this.gmDAO.save( infoMessage);
        
        InfoMessage retreiveData = this.gmDAO.get( infoMessage.getMessageId());
        assertEquals("should have persisted all messages", infoMessage.getMessages().size(), retreiveData.getMessages().size());
    }

    @Test
    public void testMessagePersistence() throws Exception {
        this.gmDAO.save( infoMessage);
        
        InfoMessage retreiveData = this.gmDAO.get( infoMessage.getMessageId());
        
        Message retrieveFirstMessage = retreiveData.getMessages().iterator().next();
        assertEquals("should have persisted text", firstMessage.getText(), retrieveFirstMessage.getText());
        assertEquals("should have persisted type", firstMessage.getType(), retrieveFirstMessage.getType());
        assertEquals("should have persisted lang", firstMessage.getLang(), retrieveFirstMessage.getLang());
    }
    
    @Autowired 
    public void setGmDAO(InfoMessageDaoImpl gmDAO) {
        this.gmDAO = gmDAO;
    }
}
