package irys.siri.realtime.simulator;

import java.util.Calendar;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class AbstractSimulator implements DataSimulatorInterface
{
	@Getter @Setter private SessionFactory chouetteSessionFactory ;
	@Getter @Setter private SessionFactory siriSessionFactory ;
	
	@Getter private Random random = new Random(Calendar.getInstance().getTimeInMillis());
	
	protected abstract void produceData() throws Exception;
	
	protected abstract Logger getLogger();
	
	@Override
	public void produce() 
	{
		Session session = SessionFactoryUtils.getSession(chouetteSessionFactory, true);
		TransactionSynchronizationManager.bindResource(chouetteSessionFactory, new SessionHolder(session));
        try
        {
        	produceData();
        } 
        catch (Exception e) 
        {
        	getLogger().error(e.getMessage(),e);
		}
		finally
		{
			TransactionSynchronizationManager.unbindResource(chouetteSessionFactory);
			SessionFactoryUtils.closeSession(session);
		}

	}
	


}
