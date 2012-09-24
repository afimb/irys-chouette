/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.chouette.client;

import irys.siri.chouette.client.adapter.InfoMessageAdapter;
import irys.siri.realtime.dao.InfoMessageDao;
import irys.siri.realtime.model.InfoMessage;
import irys.siri.sequencer.model.GeneralMessageNotificationResponse;

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
