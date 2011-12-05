/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client;

import net.dryade.siri.chouette.client.GeneralMessageService;
import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
import net.dryade.siri.sequencer.model.GeneralMessageNotificationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/persistenceConfig.xml"})
public class GeneralMessageServiceTest {
    private GeneralMessageService gmService = null;
    

    @Test
    public void testUpdate() throws Exception {
        this.gmService.update( getGM());
    }
    
    public GeneralMessageNotificationResponse getGM()
    {
        GeneralMessageNotificationResponse gm = new GeneralMessageNotificationResponse( "req", "res");
        gm.addInfoMessage( DomainObjectBuilder.aNew().infoMessage().build() );
        return gm;
    }
    

    @Autowired 
    public void setGmService(GeneralMessageService gmService) {
        this.gmService = gmService;
    }
    
}
