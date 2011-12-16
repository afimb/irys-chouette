/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client;

import net.dryade.siri.chouette.client.adapter.InfoMessageAdapter;
import net.dryade.siri.chouette.client.dao.InfoMessageDao;
import net.dryade.siri.sequencer.model.GeneralMessageNotificationResponse;
import net.dryade.siri.sequencer.model.InfoMessage;

/**
 *
 * @author marc
 */
public class GeneralMessageService
{
    private InfoMessageDao infoMessageDao;
    private InfoMessageAdapter adapter;
    

    public void setInfoMessageDao(InfoMessageDao infoMessageDao) {
        this.infoMessageDao = infoMessageDao;
    }
    
    public void update(GeneralMessageNotificationResponse generalMessage)
    {
        for ( InfoMessage infoMessage : generalMessage.getInfoMessages()) 
        {
            this.infoMessageDao.save( adapter.read(infoMessage));
        }
    }

    /**
     * @param adapter the adapter to set
     */
    public void setAdapter(InfoMessageAdapter adapter) {
        this.adapter = adapter;
    }
    
}
