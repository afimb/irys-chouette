/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.adapter;

import net.dryade.siri.chouette.client.model.InfoMessageNeptune;
import net.dryade.siri.sequencer.model.InfoMessage;
import net.dryade.siri.chouette.client.model.SectionNeptune;
import net.dryade.siri.sequencer.model.Section;
import net.dryade.siri.chouette.client.factory.DomainObjectBuilder;
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
public class InfoMessageAdapterTest {
    private InfoMessageAdapter adapter = null;

    @Test
    public void readSection() throws Exception {
        Section section = DomainObjectBuilder.aNew().sectionBuilder().
                withFirstStopPointId("titi").
                withFirstStopPointId("tata").
                withFirstStopPointId("toto").
                build();
        SectionNeptune neptune = adapter.read(section);
        
        assertEquals("should copy FirstStopPointNeptuneId",neptune.getFirstStopPointNeptuneId(),section.getFirstStopPointId());
        assertEquals("should copy LastStopPointId",neptune.getLastStopPointNeptuneId(),section.getLastStopPointId());
        assertEquals("should copy LineNeptuneId",neptune.getLineNeptuneId(),section.getLineId());
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
