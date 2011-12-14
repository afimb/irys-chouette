/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.sequencer.model.InfoMessage;
import org.hibernate.SessionFactory;

/**
 *
 * @author marc
 */
public class InfoMessageDaoImpl implements InfoMessageDao {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void save(InfoMessage infoMessage) {
        InfoMessage old = ( InfoMessage)this.sessionFactory.getCurrentSession().get( InfoMessage.class, infoMessage.getMessageId());
        if (old!=null)
            this.sessionFactory.getCurrentSession().delete( old);
        
        this.sessionFactory.getCurrentSession().save( infoMessage);
    };
    
    @Override
    public InfoMessage get( String messageId) {
        return ( InfoMessage)this.sessionFactory.getCurrentSession().get( InfoMessage.class, messageId);
    }
    
    @Override
    public void deleteAll()
    {
        this.sessionFactory.getCurrentSession().createQuery( "delete InfoMessage");
    }

}
