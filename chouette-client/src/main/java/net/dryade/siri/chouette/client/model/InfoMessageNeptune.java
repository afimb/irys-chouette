/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import net.dryade.siri.sequencer.model.Message;
import net.dryade.siri.sequencer.model.type.InfoChannel;

/**
 *
 * @author marc
 */
public class InfoMessageNeptune implements Serializable {
    private Calendar recordedAtTime;
    private Calendar validUntilTime;
    private InfoChannel channel;
    private String messageId;
    private int messageVersion;
    private List<Message> messages;
    private List<String> lineNeptuneRefs;
    private List<SectionNeptune> sectionNeptuneRefs;
    private List<String> stopPointNeptuneRefs;
    private List<String> journeyPatternNeptuneRefs;
    private List<String> routeNeptuneRefs;
    
    
    public InfoMessageNeptune() {}

    /**
     * @return the recordedAtTime
     */
    public Calendar getRecordedAtTime() {
        return recordedAtTime;
    }

    /**
     * @param recordedAtTime the recordedAtTime to set
     */
    public void setRecordedAtTime(Calendar recordedAtTime) {
        this.recordedAtTime = recordedAtTime;
    }

    /**
     * @return the validUntilTime
     */
    public Calendar getValidUntilTime() {
        return validUntilTime;
    }

    /**
     * @param validUntilTime the validUntilTime to set
     */
    public void setValidUntilTime(Calendar validUntilTime) {
        this.validUntilTime = validUntilTime;
    }


    /**
     * @return the messageVersion
     */
    public int getMessageVersion() {
        return messageVersion;
    }

    /**
     * @param messageVersion the messageVersion to set
     */
    public void setMessageVersion(int messageVersion) {
        this.messageVersion = messageVersion;
    }

    /**
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * @return the lineNeptuneRefs
     */
    public List<String> getLineNeptuneRefs() {
        return lineNeptuneRefs;
    }

    /**
     * @param lineNeptuneRefs the lineNeptuneRefs to set
     */
    public void setLineNeptuneRefs(List<String> lineNeptuneRefs) {
        this.lineNeptuneRefs = lineNeptuneRefs;
    }

    /**
     * @return the channel
     */
    public InfoChannel getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(InfoChannel channel) {
        this.channel = channel;
    }

    /**
     * @return the sectionNeptuneRefs
     */
    public List<SectionNeptune> getSectionNeptuneRefs() {
        return sectionNeptuneRefs;
    }

    /**
     * @param sectionNeptuneRefs the sectionNeptuneRefs to set
     */
    public void setSectionNeptuneRefs(List<SectionNeptune> sectionNeptuneRefs) {
        this.sectionNeptuneRefs = sectionNeptuneRefs;
    }

    /**
     * @return the stopPointNeptuneRefs
     */
    public List<String> getStopPointNeptuneRefs() {
        return stopPointNeptuneRefs;
    }

    /**
     * @param stopPointNeptuneRefs the stopPointNeptuneRefs to set
     */
    public void setStopPointNeptuneRefs(List<String> stopPointNeptuneRefs) {
        this.stopPointNeptuneRefs = stopPointNeptuneRefs;
    }

    /**
     * @return the journeyPatternNeptuneRefs
     */
    public List<String> getJourneyPatternNeptuneRefs() {
        return journeyPatternNeptuneRefs;
    }

    /**
     * @param journeyPatternNeptuneRefs the journeyPatternNeptuneRefs to set
     */
    public void setJourneyPatternNeptuneRefs(List<String> journeyPatternNeptuneRefs) {
        this.journeyPatternNeptuneRefs = journeyPatternNeptuneRefs;
    }

    /**
     * @return the routeNeptuneRefs
     */
    public List<String> getRouteNeptuneRefs() {
        return routeNeptuneRefs;
    }

    /**
     * @param routeNeptuneRefs the routeNeptuneRefs to set
     */
    public void setRouteNeptuneRefs(List<String> routeNeptuneRefs) {
        this.routeNeptuneRefs = routeNeptuneRefs;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }



    
}
