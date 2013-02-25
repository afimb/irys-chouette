/**
 * 
 */
package irys.siri.chouette.client.notification;

import lombok.Setter;
import irys.siri.chouette.client.StopMonitoringService;
import irys.siri.realtime.model.type.RequestStatus;
import irys.siri.sequencer.model.AbstractNotificationResponse;
import irys.siri.sequencer.model.AbstractSubscriptionRequest;
import irys.siri.sequencer.model.SiriAcknowledge;
import irys.siri.sequencer.model.SiriNotification;
import irys.siri.sequencer.model.StopMonitoringNotificationResponse;
import irys.siri.sequencer.model.StopMonitoringSubscriptionRequest;
import irys.siri.sequencer.notification.NotificationEndPointInterface;

import org.apache.log4j.Logger;

/**
 * @author michel
 *
 */
public class StopMonitoringNotifier implements NotificationEndPointInterface 
{
	private static final Logger logger = Logger.getLogger(StopMonitoringNotifier.class);
	
	@Setter private StopMonitoringService stopMonitoringService ;
	/**
	 * 
	 */
	public StopMonitoringNotifier() 
	{
	}

	/* (non-Javadoc)
	 * @see irys.siri.sequencer.notification.NotificationEndPointInterface#acknowledge(irys.siri.sequencer.model.SiriAcknowledge)
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
	 * @see irys.siri.sequencer.notification.NotificationEndPointInterface#notify(irys.siri.sequencer.model.SiriNotification)
	 */
	@Override
	public void notify(SiriNotification notification) 
	{
		try
		{
		for (AbstractNotificationResponse response : notification.getResponses()) 
		{
			if (response instanceof StopMonitoringNotificationResponse)
			    stopMonitoringService.update((StopMonitoringNotificationResponse) response);
			else
				logger.error("type mismatch "+response.getClass().getSimpleName());
		}
		}
		catch (Exception ex)
		{
			logger.error("notify failed ",ex);
		}
        
	}

}
