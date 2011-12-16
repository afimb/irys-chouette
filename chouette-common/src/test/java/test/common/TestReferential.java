package test.common;

import net.dryade.siri.chouette.Referential;
import net.dryade.siri.common.SiriException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;

@ContextConfiguration(locations={"classpath:testContext.xml"})


public class TestReferential extends AbstractTestNGSpringContextTests
{
    @Autowired private Referential referential;
    
    @Test(groups = {"Referential"}, description = "Referential should return objects" )
    public void testExistingObject() throws SiriException
    {
    	Line line = referential.getLine("NINOXE:Line:15626053");
    	Assert.assertNotNull(line,"line with id NINOXE:Line:15626053 was not found" );
    	Assert.assertEquals(line.getObjectId(), "NINOXE:Line:15626053","line with id NINOXE:Line:15626053 has wrong objectId "+line.getObjectId());
    	line = referential.getLineFromSiri("NINOXE:Line:15626053:LOC");
    	Assert.assertNotNull(line,"line with id NINOXE:Line:15626053 was not found" );
    	Assert.assertEquals(line.getObjectId(), "NINOXE:Line:15626053","line with siri id NINOXE:Line:15626053:LOC has wrong objectId "+line.getObjectId());
    	Route route = referential.getRoute("NINOXE:Route:15577794");
    	Assert.assertNotNull(route,"route with id NINOXE:Route:15577794:LOC was not found" );
    	Assert.assertEquals(route.getObjectId(), "NINOXE:Route:15577794","route with id NINOXE:Route:15577794 has wrong objectId "+route.getObjectId());
    	route = referential.getRouteFromSiri("NINOXE:Route:15577794:LOC");
    	Assert.assertNotNull(route,"route with id NINOXE:Route:15577794:LOC was not found" );
    	Assert.assertEquals(route.getObjectId(), "NINOXE:Route:15577794","route with siri id NINOXE:Route:15577794:LOC has wrong objectId "+route.getObjectId());
    }
}
