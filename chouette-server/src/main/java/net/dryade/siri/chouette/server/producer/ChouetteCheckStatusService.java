/**
 * 
 */
package net.dryade.siri.chouette.server.producer;

import lombok.Setter;
import uk.org.siri.siri.CheckStatusResponseBodyStructure;
import uk.org.siri.siri.ErrorDescriptionStructure;
import uk.org.siri.siri.RequestStructure;
import uk.org.siri.siri.ServiceNotAvailableErrorStructure;
import uk.org.siri.siri.CheckStatusResponseBodyStructure.ErrorCondition;
import net.dryade.siri.chouette.server.model.RealTimeDao;
import net.dryade.siri.common.SiriException;
import net.dryade.siri.server.producer.CheckStatusInterface;
import net.dryade.siri.server.producer.service.AbstractSiriService;

/**
 * @author michel
 *
 */
public class ChouetteCheckStatusService extends AbstractSiriService implements
CheckStatusInterface {

	@Setter private RealTimeDao realTimeDao;

	/**
	 * 
	 */
	public ChouetteCheckStatusService() 
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.dryade.siri.server.producer.CheckStatusInterface#getCheckStatus(uk.org.siri.siri.RequestStructure)
	 */
	@Override
	public CheckStatusResponseBodyStructure getCheckStatus(RequestStructure arg0)
	throws SiriException 
	{
		CheckStatusResponseBodyStructure answer = CheckStatusResponseBodyStructure.Factory.newInstance();
		// populate CheckStatusResponseBodyStructure

		// check database access
		boolean status = realTimeDao.checkDatabase();

		answer.setStatus(status);
		if (status)
		{
			ErrorCondition errorCondition = answer.addNewErrorCondition();
			ErrorDescriptionStructure description = errorCondition.addNewDescription();
			description.setStringValue("Database access failed");

			// ServiceNotAvailableError
			ServiceNotAvailableErrorStructure serviceNotAvailableError = errorCondition.addNewServiceNotAvailableError();
			serviceNotAvailableError.setErrorText("[DATA_UNAVAILABLE] : Database access unavailable");
		}

		return answer;
	}

}
