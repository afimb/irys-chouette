package test.server.model.realtime;

import irys.siri.chouette.server.model.DatedCall;
import irys.siri.chouette.server.model.GeneralMessage;
import irys.siri.chouette.server.model.RealTimeDao;
import irys.siri.chouette.server.model.GeneralMessage.Message;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:testContext.xml"})

public class RealTimeDaoTest extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger.getLogger(RealTimeDaoTest.class);

	@Autowired private  RealTimeDao realTimeDao;


	@Test (groups = {"DatedCall"}, description = "realTimeDao should return a list of DatedCall" )
	@Parameters({ "stoppointids","startHour", "endHour", "filterOnDeparture", "lineId",
		"operatorId","destinationIds","limit","callCount" })
		public void verifyGetCalls(
				String stoppointids,
				String startHour,
				String endHour,
				boolean filterOnDeparture,
				String lineId,
				String operatorId,
				String destinationIds,
				int limit,
				int callCount)
	{

		Calendar today = Calendar.getInstance();
		Date activeDate = new Date(today.getTimeInMillis());
		Calendar startCal = Calendar.getInstance();
		startCal.clear();
		Calendar endCal = null;
		String[] hm = startHour.split(":");
		startCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH),Integer.parseInt(hm[0]),Integer.parseInt(hm[1])) ;
		if (!endHour.isEmpty())
		{
			hm = endHour.split(":");
			endCal = Calendar.getInstance();
			endCal.clear();
			endCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH),Integer.parseInt(hm[0]),Integer.parseInt(hm[1])) ;
		}

		List<String> stopids = stoppointids.isEmpty()?null:new ArrayList<String>(Arrays.asList(stoppointids.split(",")));
		List<String> destids = destinationIds.isEmpty()?null:new ArrayList<String>(Arrays.asList(destinationIds.split(",")));

		List<DatedCall> calls = realTimeDao.getCalls(activeDate, stopids, startCal, endCal, filterOnDeparture, lineId, operatorId, 
				destids, limit); 
		Assert.assertEquals(calls.size(),callCount,"realTimeDao returns "+calls.size()+" instead of "+callCount);
	}

	@Test (groups = {"DatedCall"}, description = "realTimeDao should return one DatedCall" )
	@Parameters({ "vehicleJourneyId","stopPointId" })
	public void verifyGetDatedCall(
			long vehicleJourneyId,
			String stopPointId)
	{
		DatedCall call = realTimeDao.getDatedCall(vehicleJourneyId, stopPointId); 
		Assert.assertNotNull(call,"realTimeDao returns null");
	}

	@Test (groups = {"DatedCall"}, description = "realTimeDao should return a list of DatedCalls" )
	@Parameters({ "vehicleJourneyId","position","maxOnwards","callCount" })
	public void verifyGetOnwardCalls(
			long vehicleJourneyId,int position,int maxOnwards,int callCount)
	{
		List<DatedCall> calls = realTimeDao.getOnwardCalls(vehicleJourneyId, position, maxOnwards); 
		Assert.assertEquals(calls.size(),callCount,"realTimeDao returns "+calls.size()+" instead of "+callCount);
		if (callCount > 0)
		{
			DatedCall first = calls.get(0);
			Assert.assertEquals(first.getPosition(),position+1,"realTimeDao returns first call on position "+first.getPosition()+" for "+(position+1)+" expected");
		}
	}

	@Test (groups = {"GeneralMessage"}, description = "realTimeDao should return a list of GeneralMessages" )
	@Parameters({ "infoChannels","lang","lines","stops","gmCount" })
	public void verifyGetGeneralMessages(
			String channels,String lang,
			String lines,String stops,int callCount)
	{

		List<String> infoChannels = null;
		List<String> lineIds = null;
		List<String> stopAreaIds = null;
		if (!channels.trim().isEmpty())
		{
			infoChannels = Arrays.asList(channels.split(","));
		}
		if (!lines.trim().isEmpty())
		{
			lineIds = Arrays.asList(lines.split(","));
		}
		if (!stops.trim().isEmpty())
		{
			stopAreaIds = Arrays.asList(stops.split(","));
		}
		List<GeneralMessage> gms = realTimeDao.getGeneralMessages(infoChannels, lang, lineIds, stopAreaIds);
		Assert.assertEquals(gms.size(),callCount,"realTimeDao returns "+gms.size()+" instead of "+callCount);
		// TODO verify responses validity
		for (GeneralMessage gm : gms) 
		{
			if (infoChannels != null)
			{
				String channel = gm.getInfoChannel();
				Assert.assertTrue(infoChannels.contains(channel),"generalMessage channel not requested "+channel);
			}
			if (lineIds != null && stopAreaIds == null)
			{
				List<String> gmLines = gm.getLineIds();
				Assert.assertFalse(ListUtils.intersection(lineIds, gmLines).isEmpty(),"generalMessage must refer at least one requested line ");
			}
			else if (lineIds == null && stopAreaIds != null)
			{
				List<String> gmStops = gm.getStopAreaIds();
				Assert.assertFalse(ListUtils.intersection(stopAreaIds, gmStops).isEmpty(),"generalMessage must refer at least one requested stop ");
			}
			else if (lineIds != null && stopAreaIds != null)
			{
				List<String> gmLines = gm.getLineIds();
				List<String> gmStops = gm.getStopAreaIds();
				Assert.assertFalse(ListUtils.intersection(lineIds, gmLines).isEmpty() 
						&& ListUtils.intersection(stopAreaIds, gmStops).isEmpty(),
				"generalMessage must refer at least one requested line or one requested stop");

			}
			if (lang.isEmpty()) lang = "FR";

			List<Message> msgs = gm.getMessages();
			for (Message message : msgs) 
			{
				Assert.assertTrue(lang.equals(message.getLang()),"generalMessage message ("+message.getLang()+") must be on language "+lang);
                Assert.assertNotNull(message.getType(),"generalMessage message type must not be null");
                Assert.assertNotNull(message.getText(),"generalMessage message text must not be null");
			}

		}
	}




}
