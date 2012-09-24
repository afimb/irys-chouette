/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.chouette.client.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

import lombok.Setter;
import irys.common.SiriException;
import irys.siri.chouette.ChouetteTool;
import irys.siri.realtime.model.InfoMessage;
import irys.siri.realtime.model.InfoMessageNeptune;
import irys.siri.realtime.model.Section;
import irys.siri.realtime.model.SectionNeptune;

/**
 *
 * @author marc
 */
public class InfoMessageAdapter {
	
	private static final Logger logger = Logger.getLogger(InfoMessageAdapter.class); 
	  @Setter private ChouetteTool siriTool; 	

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
        neptune.setMessageId( infoMessage.getMessageId());
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
		try 
		{
			return siriTool.toNeptuneId(lineRef, ChouetteTool.ID_LINE, NeptuneIdentifiedObject.LINE_KEY);
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+lineRef);
			return lineRef;
		}
    }
    
    public List<String> getStopPointNeptuneRefs( List<String> stopPointRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : stopPointRefs) {
            neptuneRefs.add( getStopPointNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getStopPointNeptuneRef( String stopPointRef) 
    {
		try 
		{
			if (stopPointRef.contains(":SPOR:"))
				return siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPPOINT_KEY);

			return siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPAREA_KEY);
		} 
		catch (SiriException e) 
		{
			logger.warn("invalid id syntax "+stopPointRef);
			return stopPointRef;
		}
    }
    
    public List<String> getRouteNeptuneRefs( List<String> routeRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : routeRefs) {
            neptuneRefs.add( getRouteNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getRouteNeptuneRef( String routeRef) {
		try 
		{
			return siriTool.toNeptuneId(routeRef, ChouetteTool.ID_ROUTE, NeptuneIdentifiedObject.ROUTE_KEY);
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+routeRef);
			return routeRef;
		}
    	
    }
    
    public List<String> getJourneyPatternNeptuneRefs( List<String> journeyPatternRefs) {
        List<String> neptuneRefs = new ArrayList<String>();
        for( String ref : journeyPatternRefs) {
            neptuneRefs.add( getJourneyPatternNeptuneRef( ref));
        } 
        return neptuneRefs;
    }
    
    public String getJourneyPatternNeptuneRef( String journeyPatternRef) {
		try 
		{
			return siriTool.toNeptuneId(journeyPatternRef, ChouetteTool.ID_JOURNEYPATTERN, NeptuneIdentifiedObject.JOURNEYPATTERN_KEY);
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+journeyPatternRef);
			return journeyPatternRef;
		}

    }
}
