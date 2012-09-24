/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.dao;

import irys.siri.realtime.model.DatedCallNeptune;

import java.util.List;


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
    public void save(DatedCallNeptune datedCall)
    {
        this.sessionFactory.getCurrentSession().saveOrUpdate( datedCall);
    }
    
    @Override
    public void deleteAll()
    {
        this.sessionFactory.getCurrentSession().createQuery( "delete DatedCallNeptune");
    }

    @SuppressWarnings("unchecked")
	@Override
    public DatedCallNeptune get(Long datedVehicleJourneyId, String stopPointNeptuneRef)
    {
        List<DatedCallNeptune> results = this.sessionFactory.getCurrentSession().createCriteria(DatedCallNeptune.class).
                add( Restrictions.eq("stopPointNeptuneRef", stopPointNeptuneRef)).
                add( Restrictions.eq("datedVehicleJourneyId", datedVehicleJourneyId)).
                list();
        if ( results.isEmpty())
            return null;
        return results.iterator().next();
    }

}
