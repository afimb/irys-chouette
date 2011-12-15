/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import net.dryade.siri.chouette.client.model.InfoMessageNeptune;
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
    public void save(InfoMessageNeptune infoMessageNeptune) {
        InfoMessageNeptune old = ( InfoMessageNeptune)this.sessionFactory.getCurrentSession().get( InfoMessageNeptune.class, infoMessageNeptune.getMessageId());
        if (old!=null)
            this.sessionFactory.getCurrentSession().delete( old);
        
        this.sessionFactory.getCurrentSession().save( infoMessageNeptune);
    };
    
    @Override
    public InfoMessageNeptune get( String messageId) {
        return ( InfoMessageNeptune)this.sessionFactory.getCurrentSession().get( InfoMessageNeptune.class, messageId);
    }
    
    @Override
    public void deleteAll()
    {
        this.sessionFactory.getCurrentSession().createQuery( "delete InfoMessageNeptune");
    }

}
