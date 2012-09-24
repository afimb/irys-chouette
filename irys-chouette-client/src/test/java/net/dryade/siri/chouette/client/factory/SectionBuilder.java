/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;

import irys.siri.realtime.model.Section;

/**
 *
 * @author marc
 */
public class SectionBuilder {
    private String firstStopPointId;
    private String lastStopPointId;
    private String lineId;
    
    public static SectionBuilder create() {
        return new SectionBuilder( "my_stop_A", "my_stop_b", "my_line");
    }
    public SectionBuilder(String firstStopPointId,
            String lastStopPointId,
            String lineId){
        this.firstStopPointId = firstStopPointId;
        this.lastStopPointId = lastStopPointId;
        this.lineId = lineId;
        
    }
    public SectionBuilder withFirstStopPointId( String firstStopPointId) {
        return new SectionBuilder( firstStopPointId,
            lastStopPointId, lineId);
    }
    public SectionBuilder withLastStopPointId( String lastStopPointId) {
        return new SectionBuilder( firstStopPointId,
            lastStopPointId, lineId);
    }
    public SectionBuilder withLineId( String lineId) {
        return new SectionBuilder( firstStopPointId,
            lastStopPointId, lineId);
    }
    public Section build(){
        Section section = new Section();
        section.setFirstStopPointId( firstStopPointId);
        section.setLastStopPointId( lastStopPointId);
        section.setLineId( lineId);
        return section;
    }
}
