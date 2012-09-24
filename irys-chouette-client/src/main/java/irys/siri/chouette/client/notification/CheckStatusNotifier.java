/**
 * 
 */
package irys.siri.chouette.client.notification;

import org.apache.log4j.Logger;

import irys.siri.realtime.model.type.RequestStatus;
import irys.siri.sequencer.model.AbstractNotificationResponse;
import irys.siri.sequencer.model.AbstractSubscriptionRequest;
import irys.siri.sequencer.model.CheckStatusNotificationResponse;
import irys.siri.sequencer.model.CheckStatusSubscriptionRequest;
import irys.siri.sequencer.model.SiriAcknowledge;
import irys.siri.sequencer.model.SiriNotification;
import irys.siri.sequencer.notification.NotificationEndPointInterface;

/**
 * @author michel
 *
 */
public class CheckStatusNotifier implements NotificationEndPointInterface 
{
	private static final Logger logger = Logger.getLogger(CheckStatusNotifier.class);
	/**
	 * 
	 */
	public CheckStatusNotifier() 
	{
	}

	/* (non-Javadoc)
	 * @see net.dryade.siri.sequencer.notification.NotificationEndPointInterface#acknowledge(net.dryade.siri.sequencer.model.SiriAcknowledge)
	 */
	@Override
	public void acknowledge(SiriAcknowledge acknowledge) 
	{
		logger.info("Acknowledge received :");

		logger.info("                      subscriptionId = "+acknowledge.getSubscriptionId());
		logger.info("                      status = "+acknowledge.getStatus().name());
		if (!acknowledge.getStatus().equals(RequestStatus.OK))
		{
			logger.info("                      failed requests : "+acknowledge.getRejectedRequests().size());
			for (AbstractSubscriptionRequest request : acknowledge.getRejectedRequests()) 
			{
				logger.info("                      request id : "+request.getRequestId());
				if (! (request instanceof CheckStatusSubscriptionRequest)) 
				{
					logger.error("                      wrong request type : "+request.getClass().getSimpleName());
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see net.dryade.siri.sequencer.notification.NotificationEndPointInterface#notify(net.dryade.siri.sequencer.model.SiriNotification)
	 */
	@Override
	public void notify(SiriNotification response) 
	{
		logger.info("Notification received :");

		logger.info("                      subscriptionId = "+response.getSubscriptionId());
		logger.info("                      responses : "+response.getResponses().size());
		for (AbstractNotificationResponse aResponse : response.getResponses()) 
		{
			logger.info("                         request id : "+aResponse.getRequestId());
			if (! (aResponse instanceof CheckStatusNotificationResponse)) 
			{
				logger.error("                      wrong response type : "+aResponse.getClass().getSimpleName());
			}
			else
			{
				CheckStatusNotificationResponse csResponse = (CheckStatusNotificationResponse) aResponse;
				logger.info("                         reponseId id : "+csResponse.getResponseId());
				logger.info("                         status       : "+csResponse.getStatus());
				if (! csResponse.getStatus())
				{
					logger.info("                         error        : "+csResponse.getError().getCode().name());
					logger.info("                         message      : "+csResponse.getError().getMessage());
				}
			}
		}
		// TODO : if down : when return to up , must send new subscriptions
	}

}
