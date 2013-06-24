/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.siri.chouette.client.adapter;

import irys.siri.chouette.client.adapter.InfoMessageAdapter;
import irys.siri.realtime.model.InfoMessage;
import irys.siri.realtime.model.InfoMessageNeptune;
import irys.siri.realtime.model.Section;
import irys.siri.realtime.model.SectionNeptune;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.siri.chouette.client.factory.DomainObjectBuilder;
import static org.junit.Assert.*;

/**
 *
 * @author marc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class InfoMessageAdapterTest {
    private InfoMessageAdapter adapter = null;

    @Test
    public void readSection() throws Exception {
        Section section = DomainObjectBuilder.aNew().sectionBuilder().
                withFirstStopPointId("titi").
                withLastStopPointId("tata").
                withLineId("toto").
                build();
        SectionNeptune neptune = adapter.read(section);
        
        // assertEquals("should copy FirstStopPointNeptuneId",neptune.getFirstStopPointNeptuneId(),section.getFirstStopPointId());
        // assertEquals("should copy LastStopPointId",neptune.getLastStopPointNeptuneId(),section.getLastStopPointId());
        // assertEquals("should copy LineNeptuneId",neptune.getLineNeptuneId(),section.getLineId());
    }

    @Test
    public void copyInfoMessage() throws Exception {

        InfoMessage infoMessage = DomainObjectBuilder.aNew().infoMessage().build();
        InfoMessageNeptune neptune = new InfoMessageNeptune();
        
        
        adapter.copyInfoMessage(neptune, infoMessage);
        
        assertEquals("should copy Channel",neptune.getChannel(),infoMessage.getChannel());
        assertEquals("should copy ValidUntilTime",neptune.getValidUntilTime(),infoMessage.getValidUntilTime());
        assertEquals("should copy RecordedAtTime",neptune.getRecordedAtTime(),infoMessage.getRecordedAtTime());
        assertEquals("should copy MessageId",neptune.getMessageId(),infoMessage.getMessageId());
        assertEquals("should copy MessageVersion",neptune.getMessageVersion(),infoMessage.getMessageVersion());
    }

    @Autowired 
    public void setAdapter(InfoMessageAdapter adapter) {
        this.adapter = adapter;
    }
}
