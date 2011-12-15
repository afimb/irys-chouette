/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.model.InfoMessageNeptune;

/**
 *
 * @author marc
 */
public interface InfoMessageDao {
    
    void save(InfoMessageNeptune infoMessage);

    InfoMessageNeptune get(String messageId);

    void deleteAll();
}
