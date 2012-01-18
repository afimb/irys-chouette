package test.server.producer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import net.dryade.siri.common.SiriException;
import net.dryade.siri.server.producer.DiscoveryInterface;
import net.dryade.siri.server.producer.GeneralMessageInterface;

import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import uk.org.siri.siri.AnnotatedLineStructure;
import uk.org.siri.siri.AnnotatedStopPointStructure;
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
import uk.org.siri.siri.LinesDeliveryStructure;
import uk.org.siri.siri.LinesDiscoveryRequestStructure;
import uk.org.siri.siri.StopPointRefStructure;
import uk.org.siri.siri.StopPointsDeliveryStructure;
import uk.org.siri.siri.StopPointsDiscoveryRequestStructure;

@ContextConfiguration(locations={"classpath:testContext.xml"})

public class DiscoveryTest extends AbstractTestNGSpringContextTests
{

	@Autowired private DiscoveryInterface discovery;

	@Test (groups = {"Discovery"}, description = "LineDiscovery should retunr line descriptions" )
	@Parameters({ "lineCount"})
	public void validateLineDiscovery(int lineCount) throws SiriException
	{
		LinesDiscoveryRequestStructure request = LinesDiscoveryRequestStructure.Factory.newInstance();

		Calendar now = Calendar.getInstance();

		LinesDeliveryStructure response = discovery.getLinesDiscovery(request, now );

		AnnotatedLineStructure[] lines = response.getAnnotatedLineRefArray();

		Assert.assertEquals(lines.length,lineCount,"lineDiscovery returns "+lines.length+" lines instead of "+lineCount);

		for (AnnotatedLineStructure line: lines) 
		{

			Assert.assertNotNull(line.getLineName(),"line name must not be null");
			Assert.assertNotNull(line.getLineRef(),"line ref must not be null");
            Assert.assertTrue(line.isSetDestinations(),"line must have destinations"); 
            Assert.assertTrue(line.isSetDirections(),"line must have directions"); 
		}

	}

	@Test (groups = {"Discovery"}, description = "StopDiscovery should return stop descriptions" )
	@Parameters({ "stopCount"})
	public void validateStopDiscovery(int stopCount) throws SiriException
	{
		StopPointsDiscoveryRequestStructure request = StopPointsDiscoveryRequestStructure.Factory.newInstance();

		Calendar now = Calendar.getInstance();

		StopPointsDeliveryStructure response = discovery.getStopPointsDiscovery(request, now );

		AnnotatedStopPointStructure[] stops = response.getAnnotatedStopPointRefArray();

		Assert.assertEquals(stops.length,stopCount,"stopDiscovery returns "+stops.length+" stops instead of "+stopCount);

		for (AnnotatedStopPointStructure stop: stops) 
		{

			Assert.assertNotNull(stop.getStopName(),"stop name must not be null");
			Assert.assertNotNull(stop.getStopPointRef(),"stop ref must not be null");
            Assert.assertTrue(stop.isSetLines(),"stop must have lines"); 
		}

	}

}
