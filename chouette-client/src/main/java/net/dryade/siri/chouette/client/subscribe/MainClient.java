package net.dryade.siri.chouette.client.subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import lombok.Setter;

import net.dryade.siri.chouette.ChouetteTool;
import net.dryade.siri.chouette.Referential;
import net.dryade.siri.client.ws.ServiceInterface;
import net.dryade.siri.common.SiriTool;
import net.dryade.siri.sequencer.common.SequencerException;
import net.dryade.siri.sequencer.model.CheckStatusSubscriptionRequest;
import net.dryade.siri.sequencer.model.GeneralMessageSubscriptionRequest;
import net.dryade.siri.sequencer.model.SiriSubscription;
import net.dryade.siri.sequencer.model.StopMonitoringSubscriptionRequest;
import net.dryade.siri.sequencer.model.type.DetailLevel;
import net.dryade.siri.sequencer.model.type.InfoChannel;
import net.dryade.siri.sequencer.notification.NotificationEndPointInterface;
import net.dryade.siri.sequencer.subscription.SubscriptionServiceInterface;

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
		logger.debug("start demo");
		command.start();

	}

	private void start() 
	{

		addShutdownHook();
		Calendar endOfSubscription = Calendar.getInstance();
		endOfSubscription.add(Calendar.MINUTE, 120);
		// add CS subscription
		{
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
		// add SM subscription
		try 
		{
			if (smMode.equalsIgnoreCase("stoppoint"))
			{
				Collection<StopArea> stopareas = new ArrayList<StopArea>();
				stopareas.addAll(referential.getAllBoardingPositions());
				stopareas.addAll(referential.getAllQuays());

				for (StopArea stop : stopareas) 
				{
					String stopId = siriTool.toSiriId(stop.getObjectId(), SiriTool.ID_STOPPOINT,stop.getAreaType());

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
			else if (smMode.equalsIgnoreCase("journeypattern"))
			{
				Collection<JourneyPattern> journeyPatterns = referential.getAllJourneyPatterns();
				for (JourneyPattern jp : journeyPatterns) 
				{

					for (int i = 0; i < jp.getStopPoints().size(); i++)
					{
						SMargs args = new SMargs(jp,i);

						String stopId = siriTool.extractId(args.stopId, SiriTool.ID_STOPPOINT);

						String subscriptionId = "SM-"+stopId;
						if (args.destId != null)
						{
							String destId = siriTool.extractId(args.destId, SiriTool.ID_STOPPOINT);
							subscriptionId+="-"+destId;
						}
						if (args.lineId != null)
						{
							String lineId = siriTool.extractId(args.lineId, SiriTool.ID_LINE);
							subscriptionId+="-"+lineId;
						}
						SiriSubscription<StopMonitoringSubscriptionRequest> subscription = 
							new SiriSubscription<StopMonitoringSubscriptionRequest>(
									subscriptionId, 
									endOfSubscription, 
									smNotification, 
									serverId, 
									ServiceInterface.Service.StopMonitoringService);


						String monitoringRef = args.stopId;

						StopMonitoringSubscriptionRequest request = new StopMonitoringSubscriptionRequest("1",monitoringRef);
						if (args.destId != null)
							request.setDestinationRef(args.destId);
						if (args.lineId != null)
							request.setLineRef(args.lineId);

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

		} 
		catch (Exception e1) 
		{
			logger.error("prepare subscription failed",e1);
		} 


		// add GM subscription
		{
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

		SMargs(JourneyPattern jp,int stopRank)
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
