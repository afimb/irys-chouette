package net.dryade.siri.chouette.subscribe;

import java.util.Calendar;
import java.util.List;

import lombok.Setter;

import net.dryade.siri.chouette.client.subscribe.MainClient;
import net.dryade.siri.sequencer.common.SequencerException;
import net.dryade.siri.sequencer.model.AbstractSubscriptionRequest;
import net.dryade.siri.sequencer.model.SiriSubscription;
import net.dryade.siri.sequencer.subscription.SubscriptionServiceInterface;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class MainClientTest 
{
	@Autowired @Setter private MainClient mainClient;

	@Test
	public void smSubscribeOnStopPoints() 
	{
		SubscriberMock subscriberMock = new SubscriberMock();
		mainClient.setSubscriber(subscriberMock);
		mainClient.setSmMode("stoppoint");
		Calendar endOfSubscription = Calendar.getInstance();
		endOfSubscription.add(Calendar.MINUTE, 120);
		mainClient.smSubscribe(endOfSubscription);
		assertEquals("subscription called",subscriberMock.callCount, 78);
	}
	
	@Test
	public void smSubscribeOnLines() 
	{
		SubscriberMock subscriberMock = new SubscriberMock();
		mainClient.setSubscriber(subscriberMock);
		mainClient.setSmMode("line");
		Calendar endOfSubscription = Calendar.getInstance();
		endOfSubscription.add(Calendar.MINUTE, 120);
		mainClient.smSubscribe(endOfSubscription);
		assertEquals("subscription called",subscriberMock.callCount, 86);
	}
	
	@Test
	public void smSubscribeOnDestinations() 
	{
		SubscriberMock subscriberMock = new SubscriberMock();
		mainClient.setSubscriber(subscriberMock);
		mainClient.setSmMode("destination");
		Calendar endOfSubscription = Calendar.getInstance();
		endOfSubscription.add(Calendar.MINUTE, 120);
		mainClient.smSubscribe(endOfSubscription);
		assertEquals("subscription called",subscriberMock.callCount, 92);
	}

	@Test
	public void gmSubscribe() 
	{
		SubscriberMock subscriberMock = new SubscriberMock();
		mainClient.setSubscriber(subscriberMock);
		Calendar endOfSubscription = Calendar.getInstance();
		endOfSubscription.add(Calendar.MINUTE, 120);
		mainClient.gmSubscribe(endOfSubscription);
		assertEquals("subscription called",subscriberMock.callCount, 1);
	}

	@Test
	public void csSubscribe() 
	{
		SubscriberMock subscriberMock = new SubscriberMock();
		mainClient.setSubscriber(subscriberMock);
		Calendar endOfSubscription = Calendar.getInstance();
		endOfSubscription.add(Calendar.MINUTE, 120);
		mainClient.csSubscribe(endOfSubscription);
		assertEquals("subscription called",subscriberMock.callCount, 1);
	}

    private class SubscriberMock implements SubscriptionServiceInterface
    {

    	int callCount = 0;
    	
		@Override
		public void subscribe(
				SiriSubscription<? extends AbstractSubscriptionRequest> arg0)
				throws SequencerException {
			callCount ++;
			
		}

		@Override
		public void unsubscribe(List<String> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
