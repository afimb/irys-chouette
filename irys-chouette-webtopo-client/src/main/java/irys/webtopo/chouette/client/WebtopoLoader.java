/**
 * 
 */
package irys.webtopo.chouette.client;

import irys.siri.chouette.Referential;
import irys.webtopo.client.ws.WebtopoClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import webtopo.xsd.LineDefinitionType;
import webtopo.xsd.TopologyType;
import webtopo.xsd.TopologyVersionType;
import fr.certu.chouette.model.neptune.PTNetwork;

/**
 * @author michel
 *
 */
public class WebtopoLoader 
{
	private static final Logger logger = Logger.getLogger(WebtopoLoader.class);

	@Autowired
	@Setter private Referential referential;
	@Autowired
	@Setter private WebtopoClient webtopoClient;

	@Setter private String tempWebtopoDirectoryName;

	@Setter private String requestorRef;

	private String lastVersion = "no version";
	private boolean stopAsked = false;

	public void init()
	{
		Collection<PTNetwork> networks = referential.getAllNetworks();
		for (PTNetwork ptNetwork : networks) 
		{
			if (ptNetwork.getComment() != null && !ptNetwork.getComment().isEmpty())
			{
				lastVersion = ptNetwork.getComment();
				break;
			}
		}
		logger.debug("init : lastVersion ="+lastVersion);
	}

	public void close()
	{
		logger.info("Process asked to stop");
		stopAsked = true;
	}

	public boolean load()
	{
		logger.debug("load started");
		try
		{
			TopologyVersionType topologyVersion = webtopoClient.getTopologyVersion(requestorRef, true);
			if (stopAsked) return false;
			if (! topologyVersion.getTopologyAvailable())
			{
				logger.info("no topology available, retry later "); 
				return false; 
			}
			String version = topologyVersion.getVersion();
			if (version.equals(lastVersion)) 
			{
				logger.info("topology version already loaded : "+version+" , retry later");
				return false; // unchanged data
			}
			List<String> lines = new ArrayList<String>(Arrays.asList(topologyVersion.getAvailableLineListArray()));

			File dir = new File(tempWebtopoDirectoryName,version);

			if (!dir.exists())
			{
				if (!dir.mkdirs())
				{
					logger.error("cannot create working directory "+version+" in "+tempWebtopoDirectoryName);
					return false;
				}
			}

			for (String lineId : lines) 
			{
				List<String> lineList = new ArrayList<String>();
				lineList.add(lineId);
				TopologyType topology = webtopoClient.getTopology(requestorRef, lineList);
				if (stopAsked) return false;
				if (!topology.getTopologyAvailable())
				{
					logger.info("no topology available, retry later "); 
					return false; 
				}
				LineDefinitionType[] dataList = topology.getLineDefinitionArray();
				if (dataList.length == 1)
				{
					String id = dataList[0].getName();
					if (id.equals(lineId))
					{
						String data = dataList[0].getNeptuneData();
						File lineFile = new File(dir,lineId+".xml");
						try 
						{
							FileUtils.writeStringToFile(lineFile, data, "UTF-8");
						} 
						catch (IOException e) 
						{
							logger.error("fail to save line "+lineId+" to file "+lineFile.getName(),e);
							return false;
						}
					}
					else
					{
						logger.error("line name mismatch : asked = "+lineId+" , returned = "+id);
						return false;
					}
				}
			}

			// save to referential 
			if (referential.loadNeptuneFiles(dir,version))
			{
				logger.info("new version = "+version);
				lastVersion = version;
				return true;
			}

		}
		catch (Exception e) 
		{
			logger.error("error when loading webtopo ",e);
		}
		logger.error("save failed");
		return false;
	}

}
