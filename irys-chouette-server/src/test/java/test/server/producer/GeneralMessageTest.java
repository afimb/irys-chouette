package test.server.producer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import irys.common.SiriException;
import irys.siri.server.producer.GeneralMessageInterface;

import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import uk.org.siri.siri.ContextualisedRequestStructure;
import uk.org.siri.siri.ExtensionsStructure;
import uk.org.siri.siri.GeneralMessageDeliveriesStructure;
import uk.org.siri.siri.GeneralMessageDeliveryStructure;
import uk.org.siri.siri.GeneralMessageRequestStructure;
import uk.org.siri.siri.IDFGeneralMessageRequestFilterDocument;
import uk.org.siri.siri.IDFGeneralMessageRequestFilterStructure;
import uk.org.siri.siri.IDFGeneralMessageStructure;
import uk.org.siri.siri.IDFMessageStructure;
import uk.org.siri.siri.InfoChannelRefStructure;
import uk.org.siri.siri.InfoMessageStructure;
import uk.org.siri.siri.LineRefStructure;
import uk.org.siri.siri.StopPointRefStructure;

@ContextConfiguration(locations={"classpath:testContext.xml"})

public class GeneralMessageTest extends AbstractTestNGSpringContextTests
{

	@Autowired private GeneralMessageInterface generalMessage;
	
	@Test (groups = {"GeneralMessage"}, description = "GeneralMessage should return messages" )
	@Parameters({ "infoChannels","lang","lines","stops","gmCount" })
	public void validateMessages(String channels,String lang,
			String lines,String stops,int gmCount) throws SiriException
	{
		ContextualisedRequestStructure crs = ContextualisedRequestStructure.Factory.newInstance(); // unused
		GeneralMessageRequestStructure request = GeneralMessageRequestStructure.Factory.newInstance();
		
		List<String> infoChannels = null;
		List<String> lineIds = null;
		List<String> stopAreaIds = null;
		
		if (!lang.isEmpty())
		   request.setLanguage(lang);

		if (!channels.trim().isEmpty())
		{
			infoChannels = Arrays.asList(channels.split(","));
			for (String channel : infoChannels) 
			{
			  InfoChannelRefStructure infochannel = request.addNewInfoChannelRef();	
			  infochannel.setStringValue(channel);
			}
		}
		
		IDFGeneralMessageRequestFilterDocument filterDoc = IDFGeneralMessageRequestFilterDocument.Factory.newInstance();
		IDFGeneralMessageRequestFilterStructure filter = filterDoc.addNewIDFGeneralMessageRequestFilter();
		boolean ext = false;
		if (!lines.trim().isEmpty())
		{
			lineIds = Arrays.asList(lines.split(","));
			for (String id : lineIds) 
			{
				LineRefStructure ref = filter.addNewLineRef();
				ref.setStringValue(id);
			}
			ext = true;
				
		}
		if (!stops.trim().isEmpty())
		{
			stopAreaIds = Arrays.asList(stops.split(","));
			for (String id : stopAreaIds) 
			{
				StopPointRefStructure ref = filter.addNewStopPointRef();
				ref.setStringValue(id);
			}
			ext = true;
		}
		
		if (ext) 
		{
			ExtensionsStructure extStr = request.addNewExtensions();
		    extStr.set(filterDoc);	
		}

		Calendar cal = Calendar.getInstance();
		
		GeneralMessageDeliveriesStructure anwser = generalMessage.getGeneralMessage(crs, request, cal);
		
		GeneralMessageDeliveryStructure[] gms = anwser.getGeneralMessageDeliveryArray();
		
		Assert.assertEquals(gms.length,1,"generalMessage returns "+gms.length+" deliveries instead of 1");
		
		InfoMessageStructure[] ims = gms[0].getGeneralMessageArray();
		Assert.assertEquals(ims.length,gmCount,"generalMessage returns "+ims.length+" infoMessages instead of "+gmCount);
		
		for (InfoMessageStructure gm : ims) 
		{
			if (infoChannels != null)
			{
				String channel = gm.getInfoChannelRef().getStringValue();
				Assert.assertTrue(infoChannels.contains(channel),"generalMessage channel not requested "+channel);
			}
			XmlObject anyContent = gm.getContent();
			IDFGeneralMessageStructure content = (IDFGeneralMessageStructure) anyContent.changeType(IDFGeneralMessageStructure.type);

			if (lang.isEmpty()) lang = "FR";

			IDFMessageStructure[] msgs = content.getMessageArray();
			for (IDFMessageStructure message : msgs) 
			{
				Assert.assertTrue(lang.equals(message.getMessageText().getLang().toString()),"generalMessage message ("+message.getMessageText().getLang().toString()+") must be on language "+lang);
                Assert.assertNotNull(message.getMessageType(),"generalMessage message type must not be null");
                Assert.assertNotNull(message.getMessageText().getStringValue(),"generalMessage message text must not be null");
			}

		}
		
	}

	
	@Test (groups = {"GeneralMessage"}, description = "GeneralMessage should throw exception" )
	@Parameters({ "infoChannels","lang","lines","stops","exceptionCode" })
	public void validateException(String channels,String lang,
			String lines,String stops,String exceptionCode)
	{
		ContextualisedRequestStructure crs = ContextualisedRequestStructure.Factory.newInstance(); // unused
		GeneralMessageRequestStructure request = GeneralMessageRequestStructure.Factory.newInstance();
		
		List<String> infoChannels = null;
		List<String> lineIds = null;
		List<String> stopAreaIds = null;
		
		if (!lang.isEmpty())
		   request.setLanguage(lang);

		if (!channels.trim().isEmpty())
		{
			infoChannels = Arrays.asList(channels.split(","));
			for (String channel : infoChannels) 
			{
			  InfoChannelRefStructure infochannel = request.addNewInfoChannelRef();	
			  infochannel.setStringValue(channel);
			}
		}
		
		IDFGeneralMessageRequestFilterDocument filterDoc = IDFGeneralMessageRequestFilterDocument.Factory.newInstance();
		IDFGeneralMessageRequestFilterStructure filter = filterDoc.addNewIDFGeneralMessageRequestFilter();
		boolean ext = false;
		if (!lines.trim().isEmpty())
		{
			lineIds = Arrays.asList(lines.split(","));
			for (String id : lineIds) 
			{
				LineRefStructure ref = filter.addNewLineRef();
				ref.setStringValue(id);
			}
			ext = true;
				
		}
		if (!stops.trim().isEmpty())
		{
			stopAreaIds = Arrays.asList(stops.split(","));
			for (String id : stopAreaIds) 
			{
				StopPointRefStructure ref = filter.addNewStopPointRef();
				ref.setStringValue(id);
			}
			ext = true;
		}
		
		if (ext) 
		{
			ExtensionsStructure extStr = request.addNewExtensions();
		    extStr.set(filterDoc);	
		}

		Calendar cal = Calendar.getInstance();
		
		try 
		{
			generalMessage.getGeneralMessage(crs, request, cal);
			Assert.fail("generalMessage did not throw SiriException");
		} 
		catch (SiriException e) 
		{
			Assert.assertEquals(exceptionCode, e.getCode().toString(),"generalMessage : wrong exception code : "+ e.getCode().toString()+", expected :"+exceptionCode);
		}
		
	}

}
