/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irys.siri.realtime.dao;

import irys.siri.realtime.model.DatedVehicleJourneyNeptune;

import java.util.Calendar;
import java.util.List;


import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author marc
 */
public class DatedVehicleJourneyDaoImpl implements DatedVehicleJourneyDao {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void save(DatedVehicleJourneyNeptune datedVehicleJourney)
    {
        this.sessionFactory.getCurrentSession().saveOrUpdate(datedVehicleJourney);
        this.sessionFactory.getCurrentSession().flush();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void deleteAll()
    {
        
    	List<DatedVehicleJourneyNeptune> beans = this.sessionFactory.getCurrentSession().createCriteria(DatedVehicleJourneyNeptune.class).list();
    	for (DatedVehicleJourneyNeptune datedVehicleJourneyNeptune : beans) 
    	{
    		this.sessionFactory.getCurrentSession().delete(datedVehicleJourneyNeptune);
		}
    	this.sessionFactory.getCurrentSession().flush();
    }

    @SuppressWarnings("unchecked")
	@Override
    public DatedVehicleJourneyNeptune get(String vehicleJourneyRef, Calendar originAimedDepartureTime)
    {
        List<DatedVehicleJourneyNeptune> results = this.sessionFactory.getCurrentSession().createCriteria(DatedVehicleJourneyNeptune.class).
                add( Restrictions.eq("datedVehicleJourneyRef", vehicleJourneyRef)).
                add( Restrictions.eq("originAimedDepartureTime", originAimedDepartureTime)).
                list();
        if ( results.isEmpty())
            return null;
        return results.iterator().next();
    }

}
