/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.dao;

import irys.siri.realtime.model.InfoMessageNeptune;

/**
 *
 * @author marc
 */
public interface InfoMessageDao {
    
    void save(InfoMessageNeptune infoMessage);

    InfoMessageNeptune get(String messageId);

    void deleteAll();
}
