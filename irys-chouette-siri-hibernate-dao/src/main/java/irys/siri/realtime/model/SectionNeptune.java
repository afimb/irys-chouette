/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author marc
 */
public class SectionNeptune {
	@Getter @Setter private Long firstStopPointNeptuneId;
	@Getter @Setter private Long lastStopPointNeptuneId;
	@Getter @Setter private Long lineNeptuneId;
    
    public SectionNeptune() {}

    
    
}
