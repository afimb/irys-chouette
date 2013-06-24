/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import irys.siri.realtime.model.Message;
import irys.siri.realtime.model.type.InfoChannel;

/**
 *
 * @author marc
 */
@SuppressWarnings("serial")
public class InfoMessageNeptune implements Serializable {
    @Getter @Setter private Calendar creationTime = Calendar.getInstance();
    @Getter @Setter private Calendar recordedAtTime;
    @Getter @Setter private Calendar validUntilTime;
    @Getter @Setter private InfoChannel channel;
    @Getter @Setter private Long id;
    @Getter @Setter private String messageId;
    @Getter @Setter private int messageVersion;
    @Getter @Setter private List<Message> messages;
    @Getter @Setter private List<Long> lineNeptuneIds;
    @Getter @Setter private List<SectionNeptune> sectionNeptuneRefs;
    @Getter @Setter private List<Long> stopPointNeptuneIds;
    @Getter @Setter private List<Long> journeyPatternNeptuneIds;
    @Getter @Setter private List<Long> routeNeptuneIds;
    
    
    public InfoMessageNeptune() 
    {
    	
    }




    
}
