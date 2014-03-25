/**
 * 
 */
package irys.siri.chouette.server.producer;

import lombok.Setter;
import irys.uk.org.siri.siri.CheckStatusResponseBodyStructure;
import irys.uk.org.siri.siri.ErrorDescriptionStructure;
import irys.uk.org.siri.siri.RequestStructure;
import irys.uk.org.siri.siri.ServiceNotAvailableErrorStructure;
import irys.uk.org.siri.siri.CheckStatusResponseBodyStructure.ErrorCondition;
import irys.common.SiriException;
import irys.siri.chouette.server.model.RealTimeDao;
import irys.siri.server.producer.CheckStatusInterface;
import irys.siri.server.producer.service.AbstractSiriService;

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
	 * @see irys.siri.server.producer.CheckStatusInterface#getCheckStatus(irys.uk.org.siri.siri.RequestStructure)
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
		if (!status)
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
