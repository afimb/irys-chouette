/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.adapter;

import java.util.ArrayList;
import java.util.List;
import net.dryade.siri.chouette.client.model.InfoMessageNeptune;
import net.dryade.siri.chouette.client.model.SectionNeptune;
import net.dryade.siri.sequencer.model.InfoMessage;
import net.dryade.siri.sequencer.model.Section;

/**
 *
 * @author marc
 */
public class InfoMessageAdapter {
    public InfoMessageAdapter(){}
    
    public InfoMessageNeptune read( InfoMessage infoMessage){
        InfoMessageNeptune infoMessageNeptune = new InfoMessageNeptune();
        copyInfoMessage( infoMessageNeptune, infoMessage);
        return infoMessageNeptune;
    }
    
    public SectionNeptune read( Section section)
    {
        SectionNeptune neptune = new SectionNeptune();
        
        neptune.setFirstStopPointNeptuneId( getStopPointNeptuneRef( section.getFirstStopPointId()));
        neptune.setLastStopPointNeptuneId( getStopPointNeptuneRef( section.getLastStopPointId()));
        neptune.setLineNeptuneId( getLineNeptuneRef( section.getLineId()));
        return neptune;
    }
    
    public List<SectionNeptune> read( List<Section> sections)
    {
        List<SectionNeptune> neptunes = new ArrayList<SectionNeptune>();
        for( Section section : sections){
            neptunes.add( read( section));
        }
        return neptunes;
    }
    
    public void copyInfoMessage(InfoMessageNeptune neptune, InfoMessage infoMessage)
    {
        neptune.setChannel( infoMessage.getChannel());
        neptune.setRecordedAtTime( infoMessage.getRecordedAtTime());
        neptune.setValidUntilTime( infoMessage.getValidUntilTime());
        neptune.setMessageVersion( infoMessage.getMessageVersion());
        neptune.setMessages( infoMessage.getMessages());
        neptune.setLineNeptuneRefs( infoMessage.getLineRefs());
        neptune.setSectionNeptuneRefs( read( infoMessage.getLineSections()));
        neptune.setRouteNeptuneRefs( infoMessage.getRouteRefs());
        neptune.setStopPointNeptuneRefs( infoMessage.getStopPointRefs());
        neptune.setJourneyPatternNeptuneRefs( infoMessage.getJourneyPatternRefs());
        
    }
    public List<String> getLineNeptuneRefs( List<String> lineRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : lineRefs) {
            neptuneRefs.add( getLineNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getLineNeptuneRef( String lineRef) {
        return lineRef;
    }
    
    public List<String> getStopPointNeptuneRefs( List<String> stopPointRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : stopPointRefs) {
            neptuneRefs.add( getStopPointNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getStopPointNeptuneRef( String stopPointRef) {
        return stopPointRef;
    }
    
    public List<String> getRouteNeptuneRefs( List<String> routeRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : routeRefs) {
            neptuneRefs.add( getRouteNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getRouteNeptuneRef( String routeRef) {
        return routeRef;
    }
    
    public List<String> getJourneyPatternNeptuneRefs( List<String> journeyPatternRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : journeyPatternRefs) {
            neptuneRefs.add( getJourneyPatternNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getJourneyPatternNeptuneRef( String journeyPatternRef) {
        return journeyPatternRef;
    }
}
