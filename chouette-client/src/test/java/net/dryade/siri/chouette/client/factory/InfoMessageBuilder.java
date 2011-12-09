/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;

import java.util.Calendar;
import java.util.List;
import net.dryade.siri.sequencer.model.InfoMessage;
import net.dryade.siri.sequencer.model.Message;
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
    private String lang;
    private List<Message> messages;

    public static InfoMessageBuilder create() {
        Calendar end_validity = Calendar.getInstance();
        end_validity.add( Calendar.MONTH, 1);
        
        return new InfoMessageBuilder( Calendar.getInstance(),
                                       "123",
                                        1,
                                        InfoChannel.Commercial,
                                        end_validity,
                                        null);
    }

    public InfoMessageBuilder(Calendar recordedAtTime,
                String messageId,
                int messageVersion,
                InfoChannel infoChannel,
                Calendar validUntilTime,
                List<Message> messages) {
        this.recordedAtTime = recordedAtTime;
        this.messageId = messageId;
        this.messageVersion = messageVersion;
        this.infoChannel = infoChannel;
        this.validUntilTime = validUntilTime;
        this.lang = lang;
        this.messages = messages;
    }

    public InfoMessageBuilder withRecordedAtTime(Calendar recordedAtTime) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime, messages);
    }
    public InfoMessageBuilder withMessageId(String messageId) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime, messages);
    }
    public InfoMessageBuilder withMessageVersion(int messageVersion) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime, messages);
    }
    public InfoMessageBuilder withInfoChannel(InfoChannel infoChannel) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime, messages);
    }
    public InfoMessageBuilder withValidUntilTime(Calendar validUntilTime) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime, messages);
    }
    public InfoMessageBuilder withMessages(List<Message> messages) {
        return new InfoMessageBuilder(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime, messages);
    }
    public InfoMessage build() {
        InfoMessage instance = new InfoMessage(recordedAtTime, messageId, messageVersion, infoChannel, validUntilTime);
        instance.setMessages(messages);
        return instance;
    }
}
