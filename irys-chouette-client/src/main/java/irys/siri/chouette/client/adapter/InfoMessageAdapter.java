/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.chouette.client.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;

import lombok.Setter;
import irys.common.SiriException;
import irys.siri.chouette.ChouetteTool;
import irys.siri.chouette.Referential;
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

	@Setter private Referential referential;

	public InfoMessageAdapter(){}

	public InfoMessageNeptune read( InfoMessage infoMessage){
		InfoMessageNeptune infoMessageNeptune = new InfoMessageNeptune();
		copyInfoMessage( infoMessageNeptune, infoMessage);
		return infoMessageNeptune;
	}

	public SectionNeptune read( Section section)
	{
		SectionNeptune neptune = new SectionNeptune();

		Long id = getStopPointNeptuneRef( section.getFirstStopPointId());
		if (id == null) return null;
		neptune.setFirstStopPointNeptuneId( id);
		id = getStopPointNeptuneRef( section.getLastStopPointId());
		if (id == null) return null;
		neptune.setLastStopPointNeptuneId( id);
		id =  getLineNeptuneRef( section.getLineId());
		if (id == null) return null;
		neptune.setLineNeptuneId(id);

		return neptune;
	}

	public List<SectionNeptune> read( List<Section> sections)
	{
		List<SectionNeptune> neptunes = new ArrayList<SectionNeptune>();
		for( Section section : sections)
		{
			SectionNeptune sectionNeptune = read( section);
			if (sectionNeptune != null)
				neptunes.add(sectionNeptune );
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
		neptune.setLineNeptuneIds( getLineNeptuneRefs(infoMessage.getLineRefs()));
		neptune.setSectionNeptuneRefs( read( infoMessage.getLineSections()));
		neptune.setRouteNeptuneIds( getRouteNeptuneRefs(infoMessage.getRouteRefs()));
		neptune.setStopPointNeptuneIds( getStopPointNeptuneRefs(infoMessage.getStopPointRefs()));
		neptune.setJourneyPatternNeptuneIds( getJourneyPatternNeptuneRefs(infoMessage.getJourneyPatternRefs()));

	}
	public List<Long> getLineNeptuneRefs( List<String> lineRefs) {
		List<Long> neptuneRefs = new ArrayList<Long>();
		for( String ref : lineRefs) {
			Long id =  getLineNeptuneRef( ref);
			if (id != null)
				neptuneRefs.add( id);

		} 
		return neptuneRefs;
	}

	public Long getLineNeptuneRef( String lineRef) {
		try 
		{
			String oid = siriTool.toNeptuneId(lineRef, ChouetteTool.ID_LINE, NeptuneIdentifiedObject.LINE_KEY);
			if (oid != null)
			{
				Line area = referential.getLine(oid);
				if (area != null)
				{
					return area.getId();
				}
			}
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+lineRef);
			return null;
		}
		logger.warn("invalid id syntax "+lineRef);
		return null;
	}

	public List<Long> getStopPointNeptuneRefs( List<String> stopPointRefs) {
		List<Long> neptuneRefs = new ArrayList<Long>();
		for( String ref : stopPointRefs) {
			Long id =  getStopPointNeptuneRef( ref);
			if (id != null)
				neptuneRefs.add( id);

		} 
		return neptuneRefs;
	}

	public Long getStopPointNeptuneRef( String stopPointRef) 
	{
		try 
		{
			if (stopPointRef.contains(":SPOR:"))
			{
				logger.warn("invalid id syntax "+stopPointRef);
				return null;
			}

			String oid = siriTool.toNeptuneId(stopPointRef, ChouetteTool.ID_STOPPOINT, NeptuneIdentifiedObject.STOPAREA_KEY);
			if (oid != null)
			{
				StopArea area = referential.getStopArea(oid);
				if (area != null)
				{
					return area.getId();
				}
			}
		} 
		catch (SiriException e) 
		{
			logger.warn("invalid id syntax "+stopPointRef);
			return null;
		}
		logger.warn("invalid id syntax "+stopPointRef);
		return null;
	}

	public List<Long> getRouteNeptuneRefs( List<String> routeRefs) {
		List<Long> neptuneRefs = new ArrayList<Long>();
		for( String ref : routeRefs) {
			Long id = getRouteNeptuneRef( ref);
			if (id != null)
				neptuneRefs.add(id );
		} 
		return neptuneRefs;
	}

	public Long getRouteNeptuneRef( String routeRef) {
		try 
		{
			String oid = siriTool.toNeptuneId(routeRef, ChouetteTool.ID_ROUTE, NeptuneIdentifiedObject.ROUTE_KEY);
			if (oid != null)
			{
				Line area = referential.getLine(oid);
				if (area != null)
				{
					return area.getId();
				}
			}
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+routeRef);
			return null;
		}
		logger.warn("invalid id syntax "+routeRef);
		return null;
	}

	public List<Long> getJourneyPatternNeptuneRefs( List<String> journeyPatternRefs) {
		List<Long> neptuneRefs = new ArrayList<Long>();
		for( String ref : journeyPatternRefs) {
			Long id = getJourneyPatternNeptuneRef( ref);
			if (id != null)
				neptuneRefs.add( id);
		} 
		return neptuneRefs;
	}

	public Long getJourneyPatternNeptuneRef( String journeyPatternRef) {
		try 
		{

			String oid = siriTool.toNeptuneId(journeyPatternRef, ChouetteTool.ID_JOURNEYPATTERN, NeptuneIdentifiedObject.JOURNEYPATTERN_KEY);
			if (oid != null)
			{
				Line area = referential.getLine(oid);
				if (area != null)
				{
					return area.getId();
				}
			}
		} catch (SiriException e) {
			logger.warn("invalid id syntax "+journeyPatternRef);
			return null;
		}
		logger.warn("invalid id syntax "+journeyPatternRef);
		return null;

	}
}
