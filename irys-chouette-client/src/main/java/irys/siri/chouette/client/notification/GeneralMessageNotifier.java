/**
 * 
 */
package irys.siri.chouette.client.notification;

import lombok.Setter;
import irys.siri.chouette.client.GeneralMessageService;
import irys.siri.realtime.model.type.RequestStatus;
import irys.siri.sequencer.model.AbstractNotificationResponse;
import irys.siri.sequencer.model.AbstractSubscriptionRequest;
import irys.siri.sequencer.model.GeneralMessageNotificationResponse;
import irys.siri.sequencer.model.SiriAcknowledge;
import irys.siri.sequencer.model.SiriNotification;
import irys.siri.sequencer.model.StopMonitoringSubscriptionRequest;
import irys.siri.sequencer.notification.NotificationEndPointInterface;

import org.apache.log4j.Logger;

/**
 * @author michel
 *
 */
public class GeneralMessageNotifier implements NotificationEndPointInterface 
{
	private static final Logger logger = Logger.getLogger(GeneralMessageNotifier.class);

	@Setter private GeneralMessageService generalMessageService;
	
	public GeneralMessageNotifier() 
	{
	}

	public void init()
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
				if (! (request instanceof StopMonitoringSubscriptionRequest)) 
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
	public void notify(SiriNotification notification) 
	{
		for (AbstractNotificationResponse response : notification.getResponses()) 
		{
			if (response instanceof GeneralMessageNotificationResponse)
			    generalMessageService.update((GeneralMessageNotificationResponse) response);
			else
				logger.error("type mismatch "+response.getClass().getSimpleName());
		}
	}
}
