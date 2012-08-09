/**
 * 
 */
package net.dryade.webtopo.chouette.client;

import java.util.Calendar;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author michel
 *
 */
public class WebtopoScheduler implements Runnable
{
	private static final Logger logger = Logger.getLogger(WebtopoScheduler.class);

	@Setter private boolean fixedScheduling = true ;
	@Setter private String scheduleTime;
	@Autowired
	@Setter private WebtopoLoader webtopoLoader;

	private Thread scheduleThread;
	private boolean stopAsked = false;
	private Calendar nextLoadingTime;
	private int incrementType;
	private int incrementValue;

	public void init()
	{
		logger.debug("init");
		Calendar c = Calendar.getInstance();
		if (fixedScheduling)
		{
			String[] timeToken = scheduleTime.split(":");
			if (timeToken.length != 2) 
			{
				logger.error("invalid syntax for scheduleTime "+scheduleTime+" must be : hh:mm ");
				throw new IllegalArgumentException("scheduleTime");
			}
			int hour = Integer.parseInt(timeToken[0]);
			int minute = Integer.parseInt(timeToken[1]);
			int currentHour = c.get(Calendar.HOUR_OF_DAY);
			int currentMinute = c.get(Calendar.MINUTE);
			if (hour < currentHour || (hour == currentHour && minute < currentMinute) )
			{
				c.add(Calendar.DATE, 1);
			}
			c.set(Calendar.HOUR_OF_DAY,hour);
			c.set(Calendar.MINUTE,minute);
			incrementType = Calendar.DATE;
			incrementValue = 1;
		}
		else
		{
			String[] timeToken = scheduleTime.split(":");
			int hour = 0;
			int minute = 0;
			if (timeToken.length == 2)
			{
				hour = Integer.parseInt(timeToken[0]);
				minute = Integer.parseInt(timeToken[1]);
			}
			else if (timeToken.length == 1) 
			{
				minute = Integer.parseInt(timeToken[0]);
			}
			else
			{
				logger.error("invalid syntax for scheduleTime "+scheduleTime+" must be : hh:mm or mm");
				throw new IllegalArgumentException("scheduleTime");
			}
			minute += hour * 60;
			c.add(Calendar.MINUTE, minute);
			incrementType = Calendar.MINUTE;
			incrementValue = minute;
		}
		if (incrementValue <= 0) 
		{
			logger.error("invalid syntax for scheduleTime "+scheduleTime+" must be strictly positive");
			throw new IllegalArgumentException("scheduleTime");
		}
		
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		nextLoadingTime = c;

		logger.debug("launch webtopo");
		scheduleThread = new Thread(this);
		scheduleThread.start();
	}

	public void run()
	{
		try 
		{
			// wait a minute for end of initialisations
			Thread.sleep(60000L); 
			logger.debug("start webtopo");
			while (!stopAsked)
			{
				webtopoLoader.load();
				Calendar c = Calendar.getInstance();
				while (c.after(nextLoadingTime))
				{
					nextLoadingTime.add(incrementType, incrementValue);
				}
				long waitingTime = nextLoadingTime.getTimeInMillis() - c.getTimeInMillis();
				Thread.sleep(waitingTime);
			} 
		} 
		catch (InterruptedException e) 
		{
			logger.info("webtopoScheduling stopped");
		}
	}

	public void close()
	{
		logger.debug("stop asked");
		stopAsked = true;
		if (scheduleThread != null)
		{
			scheduleThread.interrupt();
			scheduleThread = null;
		}
	}

}
