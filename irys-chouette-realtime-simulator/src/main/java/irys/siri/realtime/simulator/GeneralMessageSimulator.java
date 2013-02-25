package irys.siri.realtime.simulator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import irys.siri.realtime.dao.InfoMessageDao;
import irys.siri.realtime.model.InfoMessageNeptune;
import irys.siri.realtime.model.Message;
import irys.siri.realtime.model.type.InfoChannel;
import irys.siri.realtime.model.type.MessageType;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;

public class GeneralMessageSimulator extends AbstractSimulator 
{

	private static Logger logger = Logger.getLogger(GeneralMessageSimulator.class);

	@Getter @Setter private INeptuneManager<Line> lineManager;
	@Getter @Setter private INeptuneManager<StopArea> stopAreaManager;
	@Getter @Setter private InfoMessageDao gmDAO;
	@Getter @Setter private String infoChannel;
	@Getter @Setter private String objectIdPrefix;
	@Getter @Setter private int messageCount;
	@Getter @Setter private List<String> generalMessages;
    @Getter @Setter private List<String> lineMessages = new ArrayList<String>();
    @Getter @Setter private List<String> stopAreaMessages = new ArrayList<String>();
    
    private static boolean initialise = true;
    private static String gmObjectId ;
    private static int gmCount = 0 ;
    
    public void init()
    {
    	if (initialise)
    	{
    		Session session = SessionFactoryUtils.getSession(getSiriSessionFactory(), true);
    		TransactionSynchronizationManager.bindResource(getSiriSessionFactory(), new SessionHolder(session));
    		try
    		{
    		    gmDAO.deleteAll();
   				session.flush();
    		}
    		finally
    		{
    			TransactionSynchronizationManager.unbindResource(getSiriSessionFactory());
    			SessionFactoryUtils.closeSession(session);
    		}

    		Calendar c = Calendar.getInstance();
    		gmObjectId=MessageFormat.format("{0}:GeneralMessage:{1,date,yyyyMMdd}", objectIdPrefix,c.getTime());
    		initialise = false;
    	}
    }
    
	@Override
	public void produceData () throws Exception 
	{

		// load lines and stopAreas
		List<Line> lines = lineManager.getAll(null);
		List<StopArea> stopAreas = stopAreaManager.getAll(null);
		
		logger.info("generation for "+infoChannel+" started");
		logger.info("messageCount =  "+messageCount);

		Session session = SessionFactoryUtils.getSession(getSiriSessionFactory(), true);
		TransactionSynchronizationManager.bindResource(getSiriSessionFactory(), new SessionHolder(session));
		try
		{
		
			for (int i = 0; i < messageCount ; i++)
			{
				generateMessage(lines,stopAreas);
			}
			session.flush();
		}
		finally
		{
			TransactionSynchronizationManager.unbindResource(getSiriSessionFactory());
			SessionFactoryUtils.closeSession(session);
		}
		
        logger.info("generation for "+infoChannel+" ended");

	
	}
	private void generateMessage(List<Line> lines, List<StopArea> stopAreas) 
	{
		int sourceMessage = getRandom().nextInt(3);
		switch (sourceMessage)
		{
		case 0 : generateGeneralMessage(); break;
		case 1 : generateLineMessage(lines); break;
		case 2 : generateStopAreaMessage(stopAreas); break;
		}
		
	}
	private void generateGeneralMessage() 
	{
		if (generalMessages.size() == 0)
		{
			return;
		}
        String message = getRandomMessage(generalMessages,true);
        logger.info("selected message = "+message);
        buildInfoMessage(message,null,null);
		
	}
	private void generateLineMessage(List<Line> lines) 
	{
		if (lineMessages.size() == 0)
		{
			generateGeneralMessage();
			return;
		}
        Line line = lines.get(getRandom().nextInt(lines.size()));
        String message = getRandomMessage(lineMessages,false);
        message = MessageFormat.format(message, line.getNumber());
        logger.info("selected message = "+message);
        buildInfoMessage(message,line.getObjectId(),null);

	}
	
	private void generateStopAreaMessage(List<StopArea> stopAreas) 
	{
		if (stopAreaMessages.size() == 0)
		{
			generateGeneralMessage();
			return;
		}
        StopArea stop = stopAreas.get(getRandom().nextInt(stopAreas.size()));
        String message = getRandomMessage(stopAreaMessages,false);
        message = MessageFormat.format(message, stop.getName());
        logger.info("selected message = "+message);
        buildInfoMessage(message,null,stop.getObjectId());
	}
	
	private void buildInfoMessage(String message,String lineId, String stopId)
	{
		InfoMessageNeptune info = new InfoMessageNeptune();
		info.setChannel(InfoChannel.fromValue(infoChannel));
		info.setMessageId(gmObjectId+"_"+gmCount);
		info.setMessageVersion(1);
		gmCount++;
		Calendar c = Calendar.getInstance();
		info.setCreationTime(c);
		if (lineId != null)
		{
			List<String> ids = new ArrayList<String>();
			ids.add(lineId);
			info.setLineNeptuneRefs(ids);
		}
		if (stopId != null)
		{
			List<String> ids = new ArrayList<String>();
			ids.add(stopId);
			info.setStopPointNeptuneRefs(ids);
		}
		info.setRecordedAtTime(c);
		c=(Calendar) c.clone();
		c.add(Calendar.MINUTE, getRandom().nextInt(900));
		info.setValidUntilTime(c);
		List<Message> messages = new ArrayList<Message>();
		Message m = new Message();
		messages.add(m);
		m.setType(MessageType.longMessage);
		m.setLang("FR");
		m.setText(message);
		info.setMessages(messages );
		gmDAO.save(info);
		
	}
	
	private String getRandomMessage(List<String> messages, boolean remove) 
	{
		int max = messages.size();
		if (max == 0) return null;
		if (remove) return messages.remove(getRandom().nextInt(max));
		return messages.get(getRandom().nextInt(max));
	}
	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

}
