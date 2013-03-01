package irys.siri.chouette.client.subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;
import irys.siri.chouette.ChouetteTool;
import irys.siri.chouette.Referential;
import irys.siri.client.ws.ServiceInterface;
import irys.common.SiriException;
import irys.common.SiriTool;
import irys.siri.realtime.model.type.DetailLevel;
import irys.siri.realtime.model.type.InfoChannel;
import irys.siri.sequencer.common.SequencerException;
import irys.siri.sequencer.model.CheckStatusSubscriptionRequest;
import irys.siri.sequencer.model.GeneralMessageSubscriptionRequest;
import irys.siri.sequencer.model.SiriSubscription;
import irys.siri.sequencer.model.StopMonitoringSubscriptionRequest;
import irys.siri.sequencer.notification.NotificationEndPointInterface;
import irys.siri.sequencer.subscription.SubscriptionServiceInterface;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.GDuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;

public class MainClient 
{
	private static final Logger logger = Logger.getLogger(MainClient.class);
	private static ClassPathXmlApplicationContext applicationContext;

	@Setter private NotificationEndPointInterface csNotification ; 

	@Setter private NotificationEndPointInterface smNotification ; 

	@Setter private NotificationEndPointInterface gmNotification ; 

	@Setter private SubscriptionServiceInterface subscriber;

	@Setter private String serverId ; 

	@Setter private ChouetteTool siriTool;

	@Setter private Referential referential;

	@Setter private int maximumStopVisit = 3;

	@Setter private int changeBeforeUpdateInSeconds = 15;

	@Setter private String smMode = "stoppoint";

	@Setter private String smCommentFilter ;

	@Setter private String stopSubscriptionTime;

	@Setter private String startSubscriptionTime;

	private Thread mainThread = null;
	private boolean stopAsked = false;;

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		String[] context = null;

		PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
		try
		{
			List<String> newContext = new ArrayList<String>();  
			Resource[] re = test.getResources("classpath*:/chouetteContext.xml");
			for (Resource resource : re)
			{
				System.out.println(resource.getURL().toString());
				newContext.add(resource.getURL().toString());	
			}
			re = test.getResources("classpath*:/irysContext.xml");
			for (Resource resource : re)
			{
				System.out.println(resource.getURL().toString());
				newContext.add(resource.getURL().toString());	
			}
			context = newContext.toArray(new String[0]);

		} 
		catch (Exception e) 
		{
			System.err.println("cannot parse contexts : "+e.getLocalizedMessage());
		}


		applicationContext = new ClassPathXmlApplicationContext(context);
		ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
		MainClient command = (MainClient) factory.getBean("MainClient");
		logger.debug("start Irys Client");
		command.start();

	}

	public void start() 
	{
		addShutdownHook();
		mainThread = Thread.currentThread();

		String [] stopTime = stopSubscriptionTime.split(":");
		String [] startTime = startSubscriptionTime.split(":");

		int stopHour = Integer.parseInt(stopTime[0]);
		int stopMinute = Integer.parseInt(stopTime[1]);
		int stopMinutes =  stopHour * 60 + stopMinute;
		int startHour = Integer.parseInt(startTime[0]);
		int startMinute = Integer.parseInt(startTime[1]);
		int startMinutes = startHour*60 + startMinute;

		boolean sameDay = (startMinutes < stopMinutes);

		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int minutes = hour * 60 + minute;


		if (( sameDay && (minutes >= stopMinutes || minutes < startMinutes)) || 
				(!sameDay && minutes >= stopMinutes && minutes < startMinutes))
		{

			long waitingTime = startMinutes - minutes;
			if (waitingTime < 0) waitingTime += 24*60;
			logger.info("Subscriptions will start in "+waitingTime+" minutes");
			waitingTime *= 60000;
			try 
			{
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				logger.info("Process stopped before first subscription");
				return;
			}
		}

		logger.info("Subscriptions start now");

		while (!stopAsked )
		{
			now = Calendar.getInstance();
			hour = now.get(Calendar.HOUR_OF_DAY);
			minute = now.get(Calendar.MINUTE);
			minutes = hour * 60 + minute;

			Calendar endOfSubscription = Calendar.getInstance();
			endOfSubscription.set(Calendar.SECOND, 0);
			endOfSubscription.set(Calendar.MILLISECOND, 0);

			if (minutes > stopMinutes)
			{
				endOfSubscription.add(Calendar.DATE, 1);
			}
			endOfSubscription.set(Calendar.HOUR_OF_DAY, stopHour);
			endOfSubscription.set(Calendar.MINUTE, stopMinute);

			if (stopAsked) break;
			// add CS subscription
			csSubscribe(endOfSubscription);
			if (stopAsked) break;
			// add SM subscription
			smSubscribe(endOfSubscription); 
			if (stopAsked) break;
			// add GM subscription
			gmSubscribe(endOfSubscription);
			if (stopAsked) break;
			
			long waitingTime = startMinutes - minutes;
			if (waitingTime <= 0) waitingTime += 24*60;
			logger.info("next Subscription will start in "+waitingTime+" minutes");
			
			waitingTime *= 60000;
			try 
			{
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				logger.info("Process stopped");
				break;
			}
			catch (Exception e) 
			{
				logger.error("Process broken",e);
				break;
			}
			if (!stopAsked) referential.init();
		}

	}

	public void close()
	{
		logger.info("Process asked to stop");
		stopAsked = true;
		mainThread.interrupt();
	}

	public void gmSubscribe(Calendar endOfSubscription) {

		String subscriptionId = "GM-1";
		SiriSubscription<GeneralMessageSubscriptionRequest> subscription = 
			new SiriSubscription<GeneralMessageSubscriptionRequest>(
					subscriptionId, 
					endOfSubscription, 
					gmNotification, 
					serverId, 
					ServiceInterface.Service.GeneralMessageService);


		GeneralMessageSubscriptionRequest request = new GeneralMessageSubscriptionRequest("1",InfoChannel.Perturbation);

		subscription.addRequest(request);
		try 
		{
			logger.info("launch GM subscription "+subscriptionId );
			subscriber.subscribe(subscription);
		} 
		catch (SequencerException e) 
		{
			logger.error("subscription failed",e);
		}

	}

	public void smSubscribe(Calendar endOfSubscription) {
		try 
		{
			if (smMode.equalsIgnoreCase("stoppoint"))
			{
				stopPointSMSubscribe(endOfSubscription);
			}
			else if (smMode.equalsIgnoreCase("line"))
			{
				lineSMSubscribe(endOfSubscription);
			}
			else if (smMode.equalsIgnoreCase("destination"))
			{
				destinationSMSubscribe(endOfSubscription);
			}
			else if (smMode.equalsIgnoreCase("journeypattern"))
			{
				journeyPatternSMSubscribe(endOfSubscription);
			}

		} 
		catch (Exception e1) 
		{
			logger.error("prepare subscription failed",e1);
		}
	}

	public void csSubscribe(Calendar endOfSubscription) {

		String subscriptionId = "CS-1";
		SiriSubscription<CheckStatusSubscriptionRequest> subscription = 
			new SiriSubscription<CheckStatusSubscriptionRequest>(
					subscriptionId, 
					endOfSubscription, 
					csNotification, 
					serverId, 
					ServiceInterface.Service.CheckStatusService);

		CheckStatusSubscriptionRequest request = new CheckStatusSubscriptionRequest("1");
		subscription.addRequest(request);
		try 
		{
			logger.info("launch CS subscription" );
			subscriber.subscribe(subscription);
		} 
		catch (SequencerException e) 
		{
			logger.error("subscription failed",e);
		}

	}

	private void destinationSMSubscribe(Calendar endOfSubscription) throws SiriException{
		Collection<JourneyPattern> journeyPatterns = referential.getAllJourneyPatterns();
		Set<String> subscriptionKeys = new HashSet<String>();
		for (JourneyPattern jp : journeyPatterns) 
		{
			Line line = jp.getRoute().getLine();
			String lineSiriId = siriTool.toSiriId(line.getObjectId(), SiriTool.ID_LINE);
			StopArea dest = jp.getStopPoints().get(jp.getStopPoints().size()-1).getContainedInStopArea();
			String destSiriId = siriTool.toSiriId(dest.getObjectId(), SiriTool.ID_STOPPOINT, dest.getAreaType());
			String destId = siriTool.extractId(destSiriId, SiriTool.ID_STOPPOINT);
			String lineId = siriTool.extractId(lineSiriId, SiriTool.ID_LINE);

			for (int i = 0; i < jp.getStopPoints().size(); i++)
			{
				StopArea area = jp.getStopPoints().get(i).getContainedInStopArea();
				if (!isValidForSm(area)) continue;
				String stopSiriId = siriTool.toSiriId(area.getObjectId(), SiriTool.ID_STOPPOINT, area.getAreaType());

				String stopId = siriTool.extractId(stopSiriId, SiriTool.ID_STOPPOINT);
				String subscriptionId = "SM-"+stopId+"-"+destId+"-"+lineId;

				if (subscriptionKeys.contains(subscriptionId)) continue;
				subscriptionKeys.add(subscriptionId);
				SiriSubscription<StopMonitoringSubscriptionRequest> subscription = 
					new SiriSubscription<StopMonitoringSubscriptionRequest>(
							subscriptionId, 
							endOfSubscription, 
							smNotification, 
							serverId, 
							ServiceInterface.Service.StopMonitoringService);

				String monitoringRef = stopSiriId;

				StopMonitoringSubscriptionRequest request = new StopMonitoringSubscriptionRequest("1",monitoringRef);

				request.setDestinationRef(destSiriId);
				request.setLineRef(lineSiriId);

				GDuration changeBeforeUpdate = new GDuration(1, 0, 0, 0, 0, changeBeforeUpdateInSeconds/60, changeBeforeUpdateInSeconds%60, BigDecimal.ZERO);
				request.setChangeBeforeUpdate(changeBeforeUpdate );
				request.setIncrementalUpdate(true);
				request.setMaximumStopVisits(maximumStopVisit);
				request.setDetailLevel(DetailLevel.complete);
				subscription.addRequest(request);
				try 
				{
					logger.info("launch SM subscription "+subscriptionId );
					subscriber.subscribe(subscription);
				} 
				catch (SequencerException e) 
				{
					logger.error("subscription failed",e);
				}
			}
		}
	}

	private void lineSMSubscribe(Calendar endOfSubscription) throws SiriException{
		Collection<StopArea> stopareas = new ArrayList<StopArea>();
		stopareas.addAll(referential.getAllBoardingPositions());
		stopareas.addAll(referential.getAllQuays());

		for (StopArea stop : stopareas) 
		{
			if (!isValidForSm(stop)) continue;

			String stopId = siriTool.toSiriId(stop.getObjectId(), SiriTool.ID_STOPPOINT,stop.getAreaType());

			List<String> lineIds = referential.getLineIdsForArea(stop.getObjectId());

			for (String lineNeptuneId : lineIds) 
			{
				String lineSiriId = siriTool.toSiriId(lineNeptuneId, SiriTool.ID_LINE);
				String lineId = siriTool.extractId(lineSiriId, SiriTool.ID_LINE);

				String subscriptionId = "SM-"+stopId+"-"+lineId;
				SiriSubscription<StopMonitoringSubscriptionRequest> subscription = 
					new SiriSubscription<StopMonitoringSubscriptionRequest>(
							subscriptionId, 
							endOfSubscription, 
							smNotification, 
							serverId, 
							ServiceInterface.Service.StopMonitoringService);


				String monitoringRef = stopId;

				StopMonitoringSubscriptionRequest request = new StopMonitoringSubscriptionRequest("1",monitoringRef);
				request.setLineRef(lineSiriId);

				GDuration changeBeforeUpdate = new GDuration(1, 0, 0, 0, 0, changeBeforeUpdateInSeconds/60, changeBeforeUpdateInSeconds%60, BigDecimal.ZERO);
				request.setChangeBeforeUpdate(changeBeforeUpdate );
				request.setIncrementalUpdate(true);
				request.setMaximumStopVisits(maximumStopVisit);
				request.setDetailLevel(DetailLevel.complete);
				subscription.addRequest(request);
				try 
				{
					logger.info("launch SM subscription "+subscriptionId );
					subscriber.subscribe(subscription);
				} 
				catch (SequencerException e) 
				{
					logger.error("subscription failed",e);
				}

			}
		}

	}

	private void journeyPatternSMSubscribe(Calendar endOfSubscription)
	throws SiriException {
		// actually not implemented, forwarded to destination because journeyPattern filter not available
		destinationSMSubscribe(endOfSubscription);
	}

	private void stopPointSMSubscribe(Calendar endOfSubscription) {
		Collection<StopArea> stopareas = new ArrayList<StopArea>();
		stopareas.addAll(referential.getAllBoardingPositions());
		stopareas.addAll(referential.getAllQuays());

		for (StopArea stop : stopareas) 
		{
			if (!isValidForSm(stop)) continue;

			String stopId;
			try {
				stopId = siriTool.toSiriId(stop.getObjectId(), SiriTool.ID_STOPPOINT,stop.getAreaType());
			} catch (SiriException e1) {
				logger.warn("invalid stopAreaId "+stop.getObjectId());
				continue;
			}

			String subscriptionId = "SM-"+stopId;
			SiriSubscription<StopMonitoringSubscriptionRequest> subscription = 
				new SiriSubscription<StopMonitoringSubscriptionRequest>(
						subscriptionId, 
						endOfSubscription, 
						smNotification, 
						serverId, 
						ServiceInterface.Service.StopMonitoringService);


			String monitoringRef = stopId;

			StopMonitoringSubscriptionRequest request = new StopMonitoringSubscriptionRequest("1",monitoringRef);

			GDuration changeBeforeUpdate = new GDuration(1, 0, 0, 0, 0, changeBeforeUpdateInSeconds/60, changeBeforeUpdateInSeconds%60, BigDecimal.ZERO);
			request.setChangeBeforeUpdate(changeBeforeUpdate );
			request.setIncrementalUpdate(true);
			request.setMaximumStopVisits(maximumStopVisit);
			request.setDetailLevel(DetailLevel.complete);
			subscription.addRequest(request);
			try 
			{
				logger.info("launch SM subscription "+subscriptionId );
				subscriber.subscribe(subscription);
			} 
			catch (SequencerException e) 
			{
				logger.error("subscription failed",e);
			}
		}
	}

	private boolean isValidForSm(StopArea stop) 
	{
		if (stop == null) return false;
		if (smCommentFilter == null || smCommentFilter.isEmpty()) return true;
		String comment = stop.getComment();
		if (comment == null || comment.isEmpty()) return false;
		return comment.contains(smCommentFilter);
	}

	public void addShutdownHook()
	{
		logger.debug("Set Shutdown Hook, application will be stop properly on stop application request");       
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(applicationContext.getBeanFactory())));
	}

	private class ShutdownHook implements Runnable
	{
		ConfigurableBeanFactory factory;
		public ShutdownHook(ConfigurableBeanFactory factory)
		{
			this.factory = factory;
		}

		/** 
		 * lancement des destructions des singletons
		 */
		public void run()
		{
			logger.info("shutdown received, stopping application");          
			factory.destroySingletons();
			logger.info("application stopped");
		}
	}


	class SMargs 
	{
		String stopId = null; 
		String lineId = null; 
		String destId = null;  

		SMargs(JourneyPattern jp,int stopRank) throws SiriException 
		{
			StopArea area = jp.getStopPoints().get(stopRank).getContainedInStopArea();
			StopArea dest = jp.getStopPoints().get(jp.getStopPoints().size()-1).getContainedInStopArea();
			Line line = jp.getRoute().getLine();
			stopId = siriTool.toSiriId(area.getObjectId(), SiriTool.ID_STOPPOINT, area.getAreaType());
			destId = siriTool.toSiriId(dest.getObjectId(), SiriTool.ID_STOPPOINT, area.getAreaType());
			lineId = siriTool.toSiriId(line.getObjectId(), SiriTool.ID_LINE);
		}
	}

}
