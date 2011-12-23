/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client;

import net.dryade.siri.chouette.client.model.InfoMessageNeptune;
import net.dryade.siri.chouette.client.adapter.InfoMessageAdapter;
import net.dryade.siri.chouette.client.dao.InfoMessageDao;
import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import net.dryade.siri.sequencer.model.GeneralMessageNotificationResponse;
import net.dryade.siri.sequencer.model.InfoMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.easymock.EasyMock.*;

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class GeneralMessageServiceTest {
    
    private GeneralMessageService gmService = null;
    private InfoMessageAdapter adapter = null;
    
    private GeneralMessageNotificationResponse gm = null;
    private InfoMessage infoMessage = null;
    
    @Before
    public void setUp() {
        infoMessage = DomainObjectBuilder.aNew().infoMessage().build();
        
        gm = new GeneralMessageNotificationResponse( "req", "res");
        gm.addInfoMessage( infoMessage );
        
    }

    @Test
    public void testUpdate() {
        // do the actual test 
        gmService.update( gm); 
    }

    @Test
    public void testInfoMessageDaoDelegation() {
        InfoMessageNeptune neptune = adapter.read(infoMessage);
        
        InfoMessageAdapter adapterMock = createMock( InfoMessageAdapter.class);
        gmService.setAdapter(adapterMock);
        expect( adapterMock.read( infoMessage)).
                andReturn( neptune);
        replay(adapterMock);
        
        InfoMessageDao mock = createMock(InfoMessageDao.class);
        gmService.setInfoMessageDao( mock);
        mock.save( neptune);
        replay(mock);

        // do the actual test 
        gmService.update( gm); 
    }

    @Autowired 
    public void setGmService(GeneralMessageService gmService) {
        this.gmService = gmService;
    }

    @Autowired 
    public void setAdapter(InfoMessageAdapter adapter) {
        this.adapter = adapter;
    }
    
}
