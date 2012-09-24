/**
 * 
 */
package irys.siri.realtime.simulator;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author michel
 *
 */
public class MainSimulator 
{

	private static ClassPathXmlApplicationContext applicationContext;
	private static Logger logger = Logger.getLogger(MainSimulator.class);

	@Getter @Setter private List<DataSimulatorInterface> simulators;
	
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
				newContext.add(resource.getURL().toString());	
			}
			re = test.getResources("classpath*:/irysContext.xml");
			for (Resource resource : re)
			{
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
		MainSimulator command = (MainSimulator) factory.getBean("MainSimulator");
		logger.debug("start simulator");
		command.start();
		factory.destroySingletons();
		System.exit(0);
	}

	private void start() 
	{
		for (DataSimulatorInterface simulator : simulators) 
		{
			simulator.produce();
		}
		
	}


}
