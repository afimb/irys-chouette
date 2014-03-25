package test.server.model.realtime;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

@ContextConfiguration(locations={"classpath:initChouetteContext.xml"})

@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)


public class ChouetteDatabaseInitialisation extends AbstractTransactionalTestNGSpringContextTests
{
	@Autowired
	private INeptuneManager<Line> lineManager;

	@Test (groups = {"SIRI"}, description = "initialise Chouette data")
	public void initChouette() throws Exception 
	{
		Reporter.log("start initialisation");
		
			long count = lineManager.count(null, null);
			Reporter.log("line count = "+count);
			if (count == 0) 
			{
				List<ParameterValue> parameters = new ArrayList<ParameterValue>();
				{
					SimpleParameterValue param = new SimpleParameterValue(
					"inputFile");
					param.setFilepathValue("src/test/data/network.zip");
					parameters.add(param);
				}
				{
					SimpleParameterValue param = new SimpleParameterValue(
					"validate");
					param.setBooleanValue(Boolean.FALSE);
					parameters.add(param);
				}
				ReportHolder iholder = new ReportHolder();
				ReportHolder vholder = new ReportHolder();
				Reporter.log("call import");
				List<Line> lines = lineManager.doImport(null, "NEPTUNE",
						parameters, iholder,vholder);
				Reporter.log("end import");
				Assert.assertFalse(lines.isEmpty(), "neptune import failed");
				Reporter.log("lines to be saved = "+count);
				if (!lines.isEmpty()) 
				{
					for (Line line : lines) 
					{
						List<Line> beans = new ArrayList<Line>();
						beans.add(line);
						lineManager.saveAll(null, beans, true, true);

					}
				}
				Reporter.log("lines saved ");


			}

	}

}
