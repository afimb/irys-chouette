/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.sequencer.model.InfoMessage;

/**
 *
 * @author marc
 */
public interface InfoMessageDao {
    
    void save(InfoMessage infoMessage);

    InfoMessage get(String messageId);

    void deleteAll();
}
