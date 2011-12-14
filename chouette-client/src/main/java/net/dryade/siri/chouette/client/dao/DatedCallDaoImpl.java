/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.dao;

import java.util.List;
import net.dryade.siri.chouette.client.model.DatedCall;
import net.dryade.siri.chouette.client.model.DatedVehicleJourney;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author marc
 */
public class DatedCallDaoImpl implements DatedCallDao {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void save(DatedCall datedCall)
    {
        this.sessionFactory.getCurrentSession().save( datedCall);
    }
    
    @Override
    public void deleteAll()
    {
        this.sessionFactory.getCurrentSession().createQuery( "delete DatedCall");
    }

    @Override
    public DatedCall get(Long datedVehicleJourneyId, String stopPointNeptuneRef)
    {
        List<DatedCall> results = this.sessionFactory.getCurrentSession().createCriteria(DatedCall.class).
                add( Restrictions.eq("stopPointNeptuneRef", stopPointNeptuneRef)).
                add( Restrictions.eq("datedVehicleJourneyId", datedVehicleJourneyId)).
                list();
        if ( results.isEmpty())
            return null;
        return results.iterator().next();
    }

}
