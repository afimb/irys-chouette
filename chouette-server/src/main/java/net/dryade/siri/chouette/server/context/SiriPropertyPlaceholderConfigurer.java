package net.dryade.siri.chouette.server.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;

public class SiriPropertyPlaceholderConfigurer extends
ServletContextPropertyPlaceholderConfigurer {

private ServletContext servletContext;
private Resource[] locations;

	
	   @SuppressWarnings("unchecked")
	@Override
	   protected void loadProperties(Properties props) throws IOException 
	   {
		      Set<String> pathNames = servletContext.getResourcePaths("/");
		      String contextName = null;
		      if (pathNames != null)
		         for (String pathName : pathNames) {
		            String realPath = servletContext.getRealPath(pathName);
		            if (contextName == null) {
		               contextName = realPath;
		               if (contextName.endsWith(File.separator))
		                  contextName = contextName.substring(0, contextName.length()-1);
		               contextName = contextName.substring(0, contextName.lastIndexOf(File.separator));
		               contextName = contextName.substring(contextName.lastIndexOf(File.separator)+1);
		            }
		         }
		      if (contextName != null) {
		         String irysConfigRootPath =  System.getProperty("IRYS_SERVER_CONFIG_ROOT_DIR");

		         if (irysConfigRootPath == null)
		         {
		        	 String osName = System.getProperty("os.name").toLowerCase();
		            if (osName.equals("linux"))
		               irysConfigRootPath = servletContext.getInitParameter("IrysServerLinuxConfigRootPath");
		            else if (osName.contains("windows"))
		               irysConfigRootPath = servletContext.getInitParameter("IrysServerWindowsConfigRootPath");
		         }

		         if (irysConfigRootPath != null) {
		            if (!irysConfigRootPath.endsWith(File.separator))
		               irysConfigRootPath += File.separator;
		            String irysConfig = irysConfigRootPath + contextName + File.separator + "irys.properties";
		            File configFile = new File(irysConfig);
		            if (configFile.exists())
		               addLocation(new FileSystemResource(configFile));
		            else
		            {
		            	 irysConfig = irysConfigRootPath + "irys.properties";
		            	 configFile = new File(irysConfig);
		            	 if (configFile.exists())
				               addLocation(new FileSystemResource(configFile));
		            }
		            String logConfig = irysConfigRootPath + contextName + File.separator + "log4j.properties";
		            File logConfigFile = new File(logConfig);
		            if (logConfigFile.exists())
		               addLocation(new FileSystemResource(logConfigFile));
		            else
		            {
		            	logConfig = irysConfigRootPath + "log4j.properties";
		            	logConfigFile = new File(logConfig);
		            	 if (logConfigFile.exists())
				               addLocation(new FileSystemResource(logConfigFile));
		            }
		         }
		      }

	   super.loadProperties(props);
	   }
	   
	   protected void addLocation(Resource resource) {
		  
	      // TODO Use ArrayUtils
	      List<Resource> locationList = new ArrayList<Resource>(Arrays.asList(locations));
	      locationList.add(resource);
	      this.locations = locationList.toArray(locations);
	      this.setLocations(locations);
	   }

	   @Override
	   public void setServletContext(ServletContext servletContext) {
	      this.servletContext = servletContext;
	      super.setServletContext(servletContext);
	   }

	   @Override
	   public void setLocations(Resource[] locations) {
	      this.locations = locations;
	      super.setLocations(locations);
	   }

}
