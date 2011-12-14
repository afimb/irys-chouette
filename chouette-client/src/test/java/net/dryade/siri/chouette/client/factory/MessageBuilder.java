/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;

import net.dryade.siri.sequencer.model.Message;
import net.dryade.siri.sequencer.model.type.MessageType;

/**
 *
 * @author marc
 */
public class MessageBuilder {
    private String text;
    private MessageType messageType;
    private String lang;
    

    public static MessageBuilder create() {
        return new MessageBuilder( MessageType.shortMessage,
                                   "Trafic devié",
                                   "FR");
    }
    
    public MessageBuilder(
                MessageType messageType,
                String text,
                String lang) {
        this.messageType = messageType;
        this.text = text;
        this.lang = lang;
    }
    public MessageBuilder withMessageType(MessageType messageType) {
        return new MessageBuilder( messageType, text, lang);
    }
    public MessageBuilder withText(String text) {
        return new MessageBuilder( messageType, text, lang);
    }
    public MessageBuilder withLang(String lang) {
        return new MessageBuilder( messageType, text, lang);
    }

    public Message build() {
        return new Message( messageType, text, lang);
    }
}
