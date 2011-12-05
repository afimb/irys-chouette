/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;

import java.util.Calendar;
import net.dryade.siri.sequencer.model.InfoMessage;
import net.dryade.siri.sequencer.model.type.InfoChannel;

/**
 *
 * @author marc
 */
public class InfoMessageBuilder {
    private Calendar recordedAtTime;
    private String messageId;
    private int messageVersion;
    private InfoChannel infoChannel;
    private Calendar validUntilTime;

    public static InfoMessageBuilder create() {
        Calendar end_validity = Calendar.getInstance();
        end_validity.add( Calendar.MONTH, 1);
        
        return new InfoMessageBuilder( Calendar.getInstance(),
                                       "123",
                                        1,
                                        InfoChannel.Commercial,
                                        end_validity);
    }

    public InfoMessageBuilder(Calendar recordedAtTime,
                String messageId,
                int messageVersion,
                InfoChannel infoChannel,
                Calendar validUntilTime) {
        this.recordedAtTime = recordedAtTime;
        this.messageId = messageId;
        this.messageVersion = messageVersion;
        this.infoChannel = infoChannel;
        this.validUntilTime = validUntilTime;
    }

    public InfoMessageBuilder withRecordedAtTime(Calendar recordedAtTime) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
    }
    public InfoMessageBuilder withMessageId(String messageId) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
    }
    public InfoMessageBuilder withMessageVersion(int messageVersion) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
    }
    public InfoMessageBuilder withValidUntilTime(InfoChannel infoChannel) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
    }
    public InfoMessageBuilder withValidUntilTime(Calendar validUntilTime) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
    }
    public InfoMessage build() {
        return new InfoMessage(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
    }
}
