package irys.siri.chouette.demo.web;

import irys.common.SiriException;
import irys.siri.client.cmd.CSCommand;
import irys.siri.client.cmd.DSCommand;
import irys.siri.client.cmd.GMCommand;
import irys.siri.client.cmd.SMCommand;
import irys.siri.client.ws.GeneralMessageClientInterface;
import irys.siri.client.ws.ServiceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import uk.org.siri.wsdl.CheckStatusResponseDocument;
import uk.org.siri.wsdl.GetGeneralMessageResponseDocument;
import uk.org.siri.wsdl.GetStopMonitoringResponseDocument;
import uk.org.siri.wsdl.LinesDiscoveryResponseDocument;
import uk.org.siri.wsdl.StopPointsDiscoveryResponseDocument;
import uk.org.siri.siri.AnnotatedDestinationStructure;
import uk.org.siri.siri.AnnotatedLineStructure;
import uk.org.siri.siri.AnnotatedStopPointStructure;
import uk.org.siri.siri.CheckStatusResponseBodyStructure;
import uk.org.siri.siri.DirectionStructure;
import uk.org.siri.siri.GeneralMessageDeliveryStructure;
import uk.org.siri.siri.IDFGeneralMessageStructure;
import uk.org.siri.siri.IDFMessageStructure;
import uk.org.siri.siri.InfoMessageStructure;
import uk.org.siri.siri.JourneyPatternRefStructure;
import uk.org.siri.siri.LineRefStructure;
import uk.org.siri.siri.LocationStructure;
import uk.org.siri.siri.MonitoredCallStructure;
import uk.org.siri.siri.MonitoredStopVisitStructure;
import uk.org.siri.siri.MonitoredVehicleJourneyStructure;
import uk.org.siri.siri.OnwardCallStructure;
import uk.org.siri.siri.OnwardCallsStructure;
import uk.org.siri.siri.PlaceNameStructure;
import uk.org.siri.siri.ServiceDeliveryErrorConditionStructure;
import uk.org.siri.siri.StopMonitoringDeliveryStructure;
import uk.org.siri.siri.StopPointRefStructure;
import uk.org.siri.siri.VehicleFeatureRefStructure;
import uk.org.siri.siri.CheckStatusResponseBodyStructure.ErrorCondition;
import uk.org.siri.siri.VehicleModesEnumeration.Enum;

public class SiriClient extends HttpServlet
{
    private static final Logger log = Logger.getLogger(SiriClient.class);
    
    private static final ClassPathXmlApplicationContext applicationContext;
    
    private static final ConfigurableBeanFactory factory;
    
    static 
    {
		String[] context = null;

		PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
		try
		{
			List<String> newContext = new ArrayList<String>();  
			Resource[] re = test.getResources("classpath*:/irysContext.xml");
			for (Resource resource : re)
			{
				log.debug(resource.getURL().toString());
				newContext.add(resource.getURL().toString());	
			}
			context = newContext.toArray(new String[0]);

		} 
		catch (Exception e) 
		{
			log.error("cannot parse contexts : "+e.getLocalizedMessage());
		}


		applicationContext = new ClassPathXmlApplicationContext(context);
		 factory = applicationContext.getBeanFactory();

    }

	class HtmlPrinter extends Printer
	{

		String adaptString(String inString)
		{
			String outString =  inString.replaceAll("<", "\n&lt;").replaceAll(">", "&gt;\n").replaceAll("\n\n","\n").replaceAll("\n", "<br>");
			return outString.substring(4,outString.length()-4);
		}

		/* (non-Javadoc)
		 * @see com.ineo.clients.SMClient.Printer#print(java.lang.StringBuilder, java.lang.String)
		 */
		void print(StringBuilder sb,String name, String data)
		{
			if (data == null)
			{
				sb.append("<tr><td>"+name+" : </td><td>null value</td></tr>\n");
			}
			else
			{
				sb.append("<tr><td>"+name+" : </td><td>").append(data).append("</td></tr>\n");
			}

		}


	}
	abstract class Printer
	{
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		String adaptString(String inString)
		{
			return inString;
		}

		/**
		 * @param sb
		 * @param name
		 * @param data
		 */
		public void print(StringBuilder sb, String name, BigInteger data)
		{
			if (data == null)
			{
				printNull(sb,name);
			}
			else
			{
				print(sb,name,data.toString());
			}

		}

		void print(StringBuilder sb, String name, boolean data)
		{
			print(sb,name,Boolean.toString(data));

		}

		/*
     void print(StringBuilder sb,String name, Object data)
     {

        print(sb,name,data.toString());

     }
		 */

		/**
		 * formate une date
		 * @param sb
		 * @param name
		 * @param data
		 */
		void print(StringBuilder sb, String name, Calendar data)
		{
			if (data == null)
			{
				printNull(sb,name);
			}
			else
			{
				print(sb,name,dateFormat.format(data.getTime()));
			}
		}

		/**
		 * formate une heure
		 * @param sb
		 * @param name
		 * @param data
		 */
		void print(StringBuilder sb, String name, Date data)
		{
			if (data == null)
			{
				printNull(sb,name);
			}
			else
			{
				print(sb,name,timeFormat.format(data));
			}

		}

		/**
		 * @param sb
		 * @param name
		 * @param data
		 */
		public void print(StringBuilder sb, String name, float data)
		{
			print(sb,name,Float.toString(data));
		}
		void print(StringBuilder sb,String name, int data)
		{
			print(sb,name,Integer.toString(data));

		}

		void print(StringBuilder sb,String name, long data)
		{
			print(sb,name,Long.toString(data));

		}

		/**
		 * @param sb
		 * @param name
		 * @param data
		 */
		public void print(StringBuilder sb, String name, org.apache.xmlbeans.StringEnumAbstractBase data)
		{
			if (data == null)
			{
				printNull(sb,name);
			}
			else
			{
				print(sb,name,data.toString());
			}

		}

		/**
		 * @param sb
		 * @param name
		 * @param data
		 */
		public void print(StringBuilder sb, String name, org.apache.xmlbeans.XmlString data)
		{
			if (data == null)
			{
				printNull(sb,name);
			}
			else
			{
				print(sb,name,data.getStringValue());
			}

		}

		abstract void print(StringBuilder sb,String name,String data) ;
		/**
		 * @param sb
		 * @param name
		 */
		private void printNull(StringBuilder sb, String name)
		{
			print(sb,name,"null value");
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4113539077582345800L;

	private static String lock = "lock";

	private static Map<String,AnnotatedLineStructure> linesInfo  = null;

	private static Map<String,AnnotatedStopPointStructure> stopPointsInfo  = null;

	private String buildHTMLPage(String answerHTMLStruct){
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Client Siri</title></head>\n<body>\n<div align=\"center\">\n");
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n");
		sb.append(answerHTMLStruct);
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n</div>\n");
		sb.append("</body></html>");
		return sb.toString();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		String format = req.getParameter("format");
		log.debug("call siri : action = "+action+" format = "+format);
		resp.setContentType("text/html;charset=UTF-8");
		if (format == null) format = "defaut";
		if(action != null && action.equals("stopMonitoring"))
		{
			String stopId = req.getParameter("StopId");
			String lineId = req.getParameter("LineId");
			String destId = req.getParameter("DestId");
			String operatorId = req.getParameter("OperatorId");
			String start = req.getParameter("Start");
			String typeVisit = req.getParameter("TypeVisit");

			int preview = ServiceInterface.UNDEFINED_NUMBER;
			int maxStop = ServiceInterface.UNDEFINED_NUMBER;
			int minStLine = ServiceInterface.UNDEFINED_NUMBER;
			int onWard = ServiceInterface.UNDEFINED_NUMBER;

			try 
			{
				preview = Integer.parseInt(req.getParameter("Preview"));
			} 
			catch (NumberFormatException e) {}

			try 
			{
				maxStop = Integer.parseInt(req.getParameter("MaxStop"));
			} 
			catch (NumberFormatException e) {}

			try 
			{
				minStLine = Integer.parseInt(req.getParameter("MinStLine"));
			} 
			catch (NumberFormatException e) {}

			try 
			{
				onWard = Integer.parseInt(req.getParameter("OnWard"));
			} 
			catch (NumberFormatException e) {}

			SMCommand client = (SMCommand) factory.getBean("SMClient");
			try 
			{
				GetStopMonitoringResponseDocument response = client.call(stopId,lineId,destId,operatorId,start,typeVisit,preview,maxStop,minStLine,onWard, null);
				print(resp,format,response);
			} 
			catch (SiriException e) 
			{
				log.error("call SIRI failed ",e);
				StringWriter sb = new StringWriter();
				e.printStackTrace(new PrintWriter(sb));
				String error = sb.toString();
				error.replaceAll("\n", "<br/>\n");
				resp.getWriter().print(buildHTMLPage(error));
				return;
			}
		}
		else if(action != null && action.equals("generalMessage"))
		{
			String infoChannel = req.getParameter("InfoChannel");
			String language = req.getParameter("Language");
			String stopId = req.getParameter("StopId");
			String lineId = req.getParameter("LineId");

			GMCommand client = (GMCommand) factory.getBean("GMClient");
			try 
			{
				GeneralMessageClientInterface.IDFItemRefFilterType type = GeneralMessageClientInterface.IDFItemRefFilterType.None;
				List<String> ids = null;
				if (lineId != null && lineId.trim().length() > 0)
				{
					type = GeneralMessageClientInterface.IDFItemRefFilterType.LineRef;
					ids = new ArrayList<String>();
					String[] id = lineId.split(",");
					ids.addAll(Arrays.asList(id));
				}
				else if (stopId != null && stopId.trim().length() > 0)
				{
					type = GeneralMessageClientInterface.IDFItemRefFilterType.StopRef;
					ids = new ArrayList<String>();
					String[] id = stopId.split(",");
					ids.addAll(Arrays.asList(id));
				}
				GetGeneralMessageResponseDocument response = client.call(infoChannel,language,type,ids);
				print(resp,format,response);
			}
			catch (SiriException e) 
			{
				log.error("call SIRI failed ",e);
				StringWriter sb = new StringWriter();
				e.printStackTrace(new PrintWriter(sb));
				String error = sb.toString();
				error.replaceAll("\n", "<br/>\n");
				resp.getWriter().print(buildHTMLPage(error));
				return;
			}
		}
		else if(action != null && action.equals("checkStatus"))
		{
			CSCommand client = (CSCommand) factory.getBean("CSClient");
			try 
			{
				CheckStatusResponseDocument response = client.call();
				print(resp,format,response);
			}
			catch (SiriException e) 
			{
				log.error("call SIRI failed ",e);
				StringWriter sb = new StringWriter();
				e.printStackTrace(new PrintWriter(sb));
				String error = sb.toString();
				error.replaceAll("\n", "<br/>\n");
				resp.getWriter().print(buildHTMLPage(error));
				return;
			}

		}
		else if(action != null && action.equals("lineDiscovery"))
		{
			DSCommand client = (DSCommand) factory.getBean("DSClient");
			try 
			{
				LinesDiscoveryResponseDocument response = client.callLineDiscovery();
				print(resp,format,response);
			}
			catch (SiriException e) 
			{
				log.error("call SIRI failed ",e);
				StringWriter sb = new StringWriter();
				e.printStackTrace(new PrintWriter(sb));
				String error = sb.toString();
				error.replaceAll("\n", "<br/>\n");
				resp.getWriter().print(buildHTMLPage(error));
				return;
			}

		}
		else if(action != null && action.equals("stopDiscovery"))
		{
			DSCommand client = (DSCommand) factory.getBean("DSClient");
			try 
			{
				StopPointsDiscoveryResponseDocument response = client.callStopPointsDiscovery();
				print(resp,format,response);
			}
			catch (SiriException e) 
			{
				log.error("call SIRI failed ",e);
				StringWriter sb = new StringWriter();
				e.printStackTrace(new PrintWriter(sb));
				String error = sb.toString();
				error.replaceAll("\n", "<br/>\n");
				resp.getWriter().print(buildHTMLPage(error));
				return;
			}

		}
		else
		{
			BufferedReader form = new BufferedReader(new InputStreamReader(SiriClient.class.getResourceAsStream("/resources/siriForm.html")));
			String line = null;
			while((line = form.readLine()) != null)
			{
				resp.getWriter().print(line);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		this.doGet(req, resp);
	}

	/**
	 * @param errorCondition
	 * @return
	 */
	private String errorToJsonString(ServiceDeliveryErrorConditionStructure errorCondition)
	{
		StringBuffer sb2 = new StringBuffer("\"error\": {");
		if(errorCondition.getAccessNotAllowedError() != null)
		{
			sb2.append("\"type\":\"Access not Allowed\",\"text\":\"").append(errorCondition.getAccessNotAllowedError().getErrorText()).append("\"");
		}
		else if(errorCondition.getAllowedResourceUsageExceededError() != null)
		{
			sb2.append("\"type\":\"Allowed Resource Usage Exceeded\",\"text\":\"").append(errorCondition.getAllowedResourceUsageExceededError().getErrorText()).append("\"");
		}
		else if(errorCondition.getCapabilityNotSupportedError() != null)
		{
			sb2.append("\"type\":\"Capability Not Supported\",\"text\":\"").append(errorCondition.getCapabilityNotSupportedError().getErrorText()).append("\"");
		}
		else if(errorCondition.getNoInfoForTopicError() != null)
		{
			sb2.append("\"type\":\"No Info Topic\",\"text\":\"").append(errorCondition.getNoInfoForTopicError().getErrorText()).append("\"");
		}
		else if(errorCondition.getOtherError() != null)
		{
			sb2.append("\"type\":\"Other Error\",\"text\":\"").append(errorCondition.getOtherError().getErrorText()).append("\"");
		}
		if(errorCondition.getDescription() != null)
		{
			sb2.append(",\"description\":\"").append(errorCondition.getDescription()).append("\"");
		}
		sb2.append("}\n");
		String val = sb2.toString();
		return val;
	}


	private void getFormattedError(StringBuilder sb,Printer printer,ServiceDeliveryErrorConditionStructure errorCondition) 
	{
		if(errorCondition.getAccessNotAllowedError() != null)
		{
			printer.print(sb,"Access not Allowed",errorCondition.getAccessNotAllowedError().getErrorText());
		}
		else if(errorCondition.getAllowedResourceUsageExceededError() != null)
		{
			printer.print(sb,"Allowed Resource Usage Exceeded",errorCondition.getAllowedResourceUsageExceededError().getErrorText());
		}
		else if(errorCondition.getCapabilityNotSupportedError() != null)
		{
			printer.print(sb,"Capability Not Supported",errorCondition.getCapabilityNotSupportedError().getErrorText());
		}
		else if(errorCondition.getNoInfoForTopicError() != null)
		{
			printer.print(sb,"No Info Topic",errorCondition.getNoInfoForTopicError().getErrorText());
		}
		else if(errorCondition.getOtherError() != null)
		{
			printer.print(sb,"Other Error",errorCondition.getOtherError().getErrorText());
		}
		if(errorCondition.getDescription() != null)
		{
			printer.print(sb,"Description",errorCondition.getDescription());
		}
	}

	private void getFormattedPrint(StringBuilder sb,Printer printer,InfoMessageStructure infoMessage) 
	{
		printer.print(sb,"FormatRef",infoMessage.getFormatRef().getStringValue());
		printer.print(sb,"InfoMessageVersion",infoMessage.getInfoMessageVersion());
		printer.print(sb,"InfoChannelRef",infoMessage.getInfoChannelRef());

		printer.print(sb,"RecordedAtTime",infoMessage.getRecordedAtTime());
		if (infoMessage.isSetValidUntilTime())
		{
			printer.print(sb,"ValidUntilTime",infoMessage.getValidUntilTime());
		}

		printer.print(sb,"InfoMessageIdentifier",infoMessage.getInfoMessageIdentifier());
		printer.print(sb,"ItemIdentifier",infoMessage.getItemIdentifier());

		XmlObject anyContent = infoMessage.getContent();
		IDFGeneralMessageStructure content = (IDFGeneralMessageStructure) anyContent.changeType(IDFGeneralMessageStructure.type);

		JourneyPatternRefStructure[] journeyPatterns = content.getJourneyPatternRefArray();
		for (JourneyPatternRefStructure journeyPattern : journeyPatterns) 
		{
			printer.print(sb,"JourneyPatternRef",journeyPattern);
		}
		/*
      NetworkRefStructure[] networks = content.getNetworkRefArray();
      for (NetworkRefStructure network : networks)
      {
         printer.print(sb,"NetworkRef",network);
      }
		 */
		LineRefStructure[] lines = content.getLineRefArray();
		for (LineRefStructure line : lines) 
		{
			printer.print(sb,"LineRef",line);
		}
		StopPointRefStructure[] stopPoints = content.getStopPointRefArray();
		for (StopPointRefStructure stopPoint : stopPoints) 
		{
			printer.print(sb,"stopPointRef",stopPoint);
		}

		IDFMessageStructure[] messages = content.getMessageArray();
		printer.print(sb,"Nbre Messages",messages.length);
		for(IDFMessageStructure message : messages)
		{
			printer.print(sb,"Language",message.getMessageText().getLang());
			printer.print(sb,"Message",message.getMessageText().getStringValue());
		}

		return ;
	}

	private void getFormattedPrint(StringBuilder sb, Printer printer,
			AnnotatedLineStructure lineStructure) 
	{
		printer.print(sb,"LineRef",lineStructure.getLineRef().getStringValue());
		printer.print(sb,"LineName",lineStructure.getLineName().getStringValue());
		printer.print(sb,"Monitored",lineStructure.getMonitored());
		if (lineStructure.isSetDestinations())
		{
			for (AnnotatedDestinationStructure destination : lineStructure.getDestinations().getDestinationArray())
			{
				printer.print(sb,"DestinationRef",destination.getDestinationRef().getStringValue());
				printer.print(sb,"PlaceName",destination.getPlaceName().getStringValue());
			}
		}
		if (lineStructure.isSetDirections())
		{
			for (DirectionStructure direction : lineStructure.getDirections().getDirectionArray())
			{
				printer.print(sb,"DirectionRef",direction.getDirectionRef().getStringValue());
				printer.print(sb,"DirectionName",direction.getDirectionName().getStringValue());
			}
		}

	}
	

	private void getFormattedPrint(StringBuilder sb, Printer printer,
			AnnotatedStopPointStructure stopStructure) {
		printer.print(sb,"StopPointRef",stopStructure.getStopPointRef().getStringValue());
		printer.print(sb,"StopName",stopStructure.getStopName().getStringValue());
		printer.print(sb,"Monitored",stopStructure.getMonitored());
		if (stopStructure.isSetLines())
		{
			for (LineRefStructure line : stopStructure.getLines().getLineRefArray())
			{
				printer.print(sb,"LineRef",line.getStringValue());
			}
		}
		
	}


	@SuppressWarnings("unchecked")
	public void getFormattedPrint(StringBuilder sb,Printer printer,MonitoredStopVisitStructure monitoredStopVisit) 
	{
		printer.print(sb,"RecordedAtTime",monitoredStopVisit.getRecordedAtTime());
		printer.print(sb,"ItemIdentifier",monitoredStopVisit.getItemIdentifier());
		printer.print(sb,"MonitoringRef",monitoredStopVisit.getMonitoringRef());
		MonitoredVehicleJourneyStructure monitoredVehicleJourney = monitoredStopVisit.getMonitoredVehicleJourney();
		printer.print(sb,"LineRef",monitoredVehicleJourney.getLineRef());
		printer.print(sb,"DataFrameRef",monitoredVehicleJourney.getFramedVehicleJourneyRef().getDataFrameRef());
		printer.print(sb,"DatedVehicleJourneyRef",monitoredVehicleJourney.getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());
		printer.print(sb,"PublishedLineName",monitoredVehicleJourney.getPublishedLineName());
		printer.print(sb,"DirectionName",monitoredVehicleJourney.getDirectionName());
		if(monitoredVehicleJourney.isSetProductCategoryRef())
		{
			printer.print(sb,"ProductCategoryRef",monitoredVehicleJourney.getProductCategoryRef());
		}

		printer.print(sb,"OperatorRef",monitoredVehicleJourney.getOperatorRef());
		printer.print(sb,"JourneyPatternRef",monitoredVehicleJourney.getJourneyPatternRef());
		Enum[] vehicleModeArray = monitoredVehicleJourney.getVehicleModeArray();
		for (Enum vehicleMode : vehicleModeArray)
		{
			printer.print(sb,"VehicleMode",vehicleMode);
		}

		boolean monitored = monitoredVehicleJourney.getMonitored();

		if (!monitored)
		{
			List<String> msg = monitoredVehicleJourney.getMonitoringError();
			printer.print(sb,"Monitored",false);
			if (msg != null)
				printer.print(sb,"MonitoringError",Arrays.toString(msg.toArray(new String[0])));

		}
		else
		{
			printer.print(sb,"Monitored",true);
		}
		LocationStructure location = monitoredVehicleJourney.getVehicleLocation();
		if(location != null)
		{
			printer.print(sb,"SrsName",location.getSrsName());
			printer.print(sb,"Coordinates",printer.adaptString(location.getCoordinates().getStringValue()));
		}

		printer.print(sb,"Bearing",monitoredVehicleJourney.getBearing());
		GDuration delay = monitoredVehicleJourney.getDelay();
		if(delay != null)
			printer.print(sb,"Delay",(delay.getSign() == 1 ?"+":"-")+delay.getHour()+":"+delay.getMinute()+":"+delay.getSecond());
		VehicleFeatureRefStructure[] vehicleFeatureRefArray = monitoredVehicleJourney.getVehicleFeatureRefArray();
		for (VehicleFeatureRefStructure vehicleFeatureRef : vehicleFeatureRefArray)
		{
			printer.print(sb,"VehicleFeatureRef",vehicleFeatureRef);
		}

		MonitoredCallStructure monitoredCall = monitoredStopVisit.getMonitoredVehicleJourney().getMonitoredCall();
		printer.print(sb,"StopPointRef",monitoredCall.getStopPointRef());
		printer.print(sb,"StopPointName",monitoredCall.getStopPointName());
		printer.print(sb,"Order",monitoredCall.getOrder());

		printer.print(sb,"PlatformTraversal",monitoredCall.getPlatformTraversal());
		if (monitoredCall.isSetDestinationDisplay())
			printer.print(sb,"DestinationDisplay",monitoredCall.getDestinationDisplay());
		if (monitoredCall.isSetExpectedArrivalTime())
			printer.print(sb,"ExpectedArrivalTime",monitoredCall.getExpectedArrivalTime().getTime());
		if (monitoredCall.isSetActualArrivalTime())
			printer.print(sb,"ActualArrivalTime",monitoredCall.getActualArrivalTime().getTime());
		if (monitoredCall.isSetAimedArrivalTime())
		{
			printer.print(sb,"AimedArrivalTime",monitoredCall.getAimedArrivalTime().getTime());
			printer.print(sb,"ArrivalStatus",monitoredCall.getArrivalStatus());
			if (monitoredCall.isSetArrivalPlatformName())
				printer.print(sb,"ArrivalPlatformName",monitoredCall.getArrivalPlatformName());
		}

		if (monitoredCall.isSetActualDepartureTime())
			printer.print(sb,"ActualDepartureTime",monitoredCall.getActualDepartureTime().getTime());
		if (monitoredCall.isSetExpectedDepartureTime())
			printer.print(sb,"ExpectedDepartureTime",monitoredCall.getExpectedDepartureTime().getTime());
		if (monitoredCall.isSetAimedDepartureTime())
		{
			printer.print(sb,"AimedDepartureTime",monitoredCall.getAimedDepartureTime().getTime());
			printer.print(sb,"DepartureStatus",monitoredCall.getDepartureStatus());
			if (monitoredCall.isSetDeparturePlatformName())
				printer.print(sb,"DeparturePlatformName",monitoredCall.getDeparturePlatformName());
		}
		//sb.append("<tr><td>DepartureBoardingActivity : </td><td>").append(monitoredCall.getDepartureBoardingActivity()).append("</td></tr>\n");

		if(monitoredVehicleJourney.isSetOriginRef())
		{
			printer.print(sb,"OriginRef",monitoredVehicleJourney.getOriginRef());
			printer.print(sb,"OriginName",monitoredVehicleJourney.getOriginName());
			if (monitoredVehicleJourney.isSetOriginAimedDepartureTime())
				printer.print(sb,"OriginAimedDepartureTime",monitoredVehicleJourney.getOriginAimedDepartureTime().getTime());
		}

		if(monitoredVehicleJourney.isSetDestinationRef())
		{
			printer.print(sb,"DestinationRef",monitoredVehicleJourney.getDestinationRef());
			printer.print(sb,"DestinationName",monitoredVehicleJourney.getDestinationName());
			if (monitoredVehicleJourney.isSetDestinationAimedArrivalTime())
				printer.print(sb,"DestinationAimedArrivalTime",monitoredVehicleJourney.getDestinationAimedArrivalTime().getTime());
		}

		PlaceNameStructure[] vias = monitoredVehicleJourney.getViaArray(); 
		if(vias != null)
		{
			for(PlaceNameStructure via : vias)
			{
				printer.print(sb,"Via",via.getPlaceName());
			}
		}

		if(monitoredVehicleJourney.getOnwardCalls() != null)
		{
			OnwardCallStructure[] onwardCalls = monitoredVehicleJourney.getOnwardCalls().getOnwardCallArray();
			for (OnwardCallStructure onwardCall : onwardCalls) 
			{
				printer.print(sb,"OnwardStopPointRef",onwardCall.getStopPointRef());
				printer.print(sb,"OnwardStopPointName",onwardCall.getStopPointName());
				printer.print(sb,"OnwardOrder",onwardCall.getOrder());
				//sb.append("<tr><td>DepartureBoardingActivity : </td><td>").append(onwardCall.getDepartureBoardingActivity()).append("</td></tr>\n");
				if (onwardCall.getExpectedArrivalTime() != null)
					printer.print(sb,"OnwardExpectedArrivalTime",onwardCall.getExpectedArrivalTime().getTime());
				if (onwardCall.getAimedArrivalTime() != null)
				{
					printer.print(sb,"OnwardAimedArrivalTime",onwardCall.getAimedArrivalTime().getTime());
					printer.print(sb,"OnwardArrivalStatus",onwardCall.getArrivalStatus());
					if (onwardCall.getArrivalPlatformName() != null)
						printer.print(sb,"OnwardArrivalPlatformName",onwardCall.getArrivalPlatformName());
				}

				if (onwardCall.getExpectedDepartureTime() != null)
					printer.print(sb,"OnwardExpectedDepartureTime",onwardCall.getExpectedDepartureTime().getTime());
				if (onwardCall.getAimedDepartureTime() != null)
				{
					printer.print(sb,"OnwardAimedDepartureTime",onwardCall.getAimedDepartureTime().getTime());
					printer.print(sb,"OnwardDepartureStatus",onwardCall.getDepartureStatus());
					if (onwardCall.getDeparturePlatformName() != null )
						printer.print(sb,"OnwardDeparturePlatformName",onwardCall.getDeparturePlatformName());
				}
			}
		}

		return ;
	}


	private void loadStructures()
	{
		linesInfo = new HashMap<String, AnnotatedLineStructure>();
		stopPointsInfo = new HashMap<String, AnnotatedStopPointStructure>();
		try
		{
			DSCommand disc = new DSCommand();
			LinesDiscoveryResponseDocument repLine = disc.callLineDiscovery();
			StopPointsDiscoveryResponseDocument repStop = disc.callStopPointsDiscovery();

			for (AnnotatedLineStructure line : repLine.getLinesDiscoveryResponse().getAnswer().getAnnotatedLineRefArray())
			{
				linesInfo.put(line.getLineRef().getStringValue(),line);
			}

			for (AnnotatedStopPointStructure stop : repStop.getStopPointsDiscoveryResponse().getAnswer().getAnnotatedStopPointRefArray())
			{
				stopPointsInfo.put(stop.getStopPointRef().getStringValue(),stop);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void print(HttpServletResponse resp, String format, CheckStatusResponseDocument response) throws IOException
	{
		if (format.equals("json"))
		{
			printJson(resp,response);
		}
		else
		{
			printHTML(resp,response);
		}       
	}

	private void print(HttpServletResponse resp, String format,
			LinesDiscoveryResponseDocument response) throws IOException 
			{
		if (format.equals("json"))
		{
			printJson(resp,response);
		}
		else
		{
			printHTML(resp,response);
		}       
			}



	private void print(HttpServletResponse resp, String format, GetGeneralMessageResponseDocument response) throws IOException
	{
		if (format.equals("json"))
		{
			printJson(resp,response);
		}
		else
		{
			printHTML(resp,response);
		}       


	}

	private void print(HttpServletResponse resp, String format, GetStopMonitoringResponseDocument response) throws IOException
	{
		if (format.equals("json"))
		{
			printJson(resp,response);
		}
		else
		{
			printHTML(resp,response);
		}       

	}

	private void printHTML(HttpServletResponse resp, CheckStatusResponseDocument response) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Client Siri</title></head>\n<body>\n<div align=\"center\">\n");
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n");

		CheckStatusResponseBodyStructure answer = response.getCheckStatusResponse().getAnswer();

		if(answer.getStatus())
		{
			sb.append("status : ok<br/>");
			sb.append("<br/><br/>");
		}
		else
		{
			ErrorCondition errorCondition = answer.getErrorCondition();
			sb.append("<table>");
			Printer printer = new HtmlPrinter();
			if(errorCondition.getServiceNotAvailableError() != null)
			{
				printer.print(sb,"Service not available",errorCondition.getServiceNotAvailableError().getErrorText());
			}
			else if(errorCondition.getOtherError() != null)
			{
				printer.print(sb,"Other Error",errorCondition.getOtherError().getErrorText());
			}
			if(errorCondition.getDescription() != null)
			{
				printer.print(sb,"Description",errorCondition.getDescription());
			}
			sb.append("</table>");
		}


		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n</div>\n");
		sb.append("</body></html>");
		resp.getWriter().print(sb.toString());
		return ;
	}

	private void printHTML(HttpServletResponse resp, GetGeneralMessageResponseDocument response) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Client Siri</title></head>\n<body>\n<div align=\"center\">\n");
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n");
		Printer printer = new HtmlPrinter();
		GeneralMessageDeliveryStructure[] messageDeliveries = response.getGetGeneralMessageResponse().getAnswer().getGeneralMessageDeliveryArray();
		for(GeneralMessageDeliveryStructure messageDelivery : messageDeliveries){
			if(messageDelivery.getStatus())
			{
				InfoMessageStructure[] infoMessages = messageDelivery.getGeneralMessageArray();
				int i = 1;
				for (InfoMessageStructure infoMessage : infoMessages) 
				{
					if (i > 1)
						sb.append("<hr/>\n");
					sb.append("Response Number ").append((i++)).append("<br/>");
					sb.append("<table>");
					getFormattedPrint( sb, printer, infoMessage);
					sb.append("</table>");
					sb.append("<br/><br/>");
				}
			}
			else
			{
				ServiceDeliveryErrorConditionStructure errorCondition = messageDelivery.getErrorCondition();
				sb.append("<table>");
				getFormattedError( sb, printer, errorCondition);
				sb.append("</table>");
			}
		}
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n</div>\n");
		sb.append("</body></html>");
		resp.getWriter().print(sb.toString());
		return ;

	}

	private void printHTML(HttpServletResponse resp,
			LinesDiscoveryResponseDocument response) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Client Siri</title></head>\n<body>\n<div align=\"center\">\n");
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n");
		Printer printer = new HtmlPrinter();

		int i = 1;
		for (AnnotatedLineStructure lineStructure : response.getLinesDiscoveryResponse().getAnswer().getAnnotatedLineRefArray()) 
		{
			if (i > 1)
				sb.append("<hr/>\n");
			sb.append("Response Number ").append((i++)).append("<br/>\n");
			sb.append("<table VALIGN='TOP'>\n");
			getFormattedPrint(sb,printer,lineStructure);  
			sb.append("</table>\n");

		}

		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n</div>\n");
		sb.append("</body></html>");
		resp.getWriter().print(sb.toString());
		return ;
	}

	private void print(HttpServletResponse resp, String format, StopPointsDiscoveryResponseDocument response) throws IOException 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Client Siri</title></head>\n<body>\n<div align=\"center\">\n");
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n");
		Printer printer = new HtmlPrinter();

		int i = 1;
		for (AnnotatedStopPointStructure stopStructure : response.getStopPointsDiscoveryResponse().getAnswer().getAnnotatedStopPointRefArray()) 
		{
			if (i > 1)
				sb.append("<hr/>\n");
			sb.append("Response Number ").append((i++)).append("<br/>\n");
			sb.append("<table VALIGN='TOP'>\n");
			getFormattedPrint(sb,printer,stopStructure);  
			sb.append("</table>\n");

		}

		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n</div>\n");
		sb.append("</body></html>");
		resp.getWriter().print(sb.toString());
		return ;

	}



	private void printHTML(HttpServletResponse resp, GetStopMonitoringResponseDocument response) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Client Siri</title></head>\n<body>\n<div align=\"center\">\n");
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n");
		Printer printer = new HtmlPrinter();
		StopMonitoringDeliveryStructure[] stopMonitoringDeliveries = response.getGetStopMonitoringResponse().getAnswer().getStopMonitoringDeliveryArray();
		for (StopMonitoringDeliveryStructure stopMonitoringDelivery : stopMonitoringDeliveries) 
		{
			if(stopMonitoringDelivery.getStatus())
			{
				MonitoredStopVisitStructure[] monitoredStopVisits = stopMonitoringDelivery.getMonitoredStopVisitArray();
				int i = 1;
				for (MonitoredStopVisitStructure monitoredStopVisit : monitoredStopVisits) 
				{
					if (i > 1)
						sb.append("<hr/>\n");
					sb.append("Response Number ").append((i++)).append("<br/>\n");
					sb.append("<table VALIGN='TOP'>\n");
					getFormattedPrint(sb,printer,monitoredStopVisit);  
					sb.append("</table>\n");
				}
			}

			else
			{
				ServiceDeliveryErrorConditionStructure errorCondition = stopMonitoringDelivery.getErrorCondition();
				sb.append("<table>");
				getFormattedError( sb, printer, errorCondition);
				sb.append("</table>");
			}
		}
		sb.append("\n<form action=\"siri\"><input type=\"submit\" value=\"retour\"/></form>\n</div>\n");
		sb.append("</body></html>");
		resp.getWriter().print(sb.toString());
		return ;
	}

	private void printJson(HttpServletResponse resp, CheckStatusResponseDocument response) throws IOException
	{
		// TODO Auto-generated method stub

	}

	private void printJson(HttpServletResponse resp, GetGeneralMessageResponseDocument response) throws IOException
	{
		synchronized (lock)
		{
			if (linesInfo == null) 
			{
				loadStructures();
			}
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String separ = "";
		GeneralMessageDeliveryStructure[] messageDeliveries = response.getGetGeneralMessageResponse().getAnswer().getGeneralMessageDeliveryArray();
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for(GeneralMessageDeliveryStructure messageDelivery : messageDeliveries)
		{
			if(messageDelivery.getStatus())
			{
				sb.append("\"generalMessages\": [ \n");
				InfoMessageStructure[] infoMessages = messageDelivery.getGeneralMessageArray();
				for (InfoMessageStructure infoMessage : infoMessages) 
				{
					sb.append(separ);
					sb.append("{\n");
					sb.append("\"messageClass\":\"").append(infoMessage.getInfoChannelRef().getStringValue().toLowerCase()).append("\",\n");
					sb.append("\"id\":\"").append(infoMessage.getInfoMessageIdentifier().getStringValue()).append("\",\n");
					sb.append("\"version\":\"").append(infoMessage.getInfoMessageVersion()).append("\",\n");
					sb.append("\"type\":\"").append(infoMessage.getInfoChannelRef().getStringValue()).append("\",\n");
					sb.append("\"startDate\":\"").append(dateFormat.format(infoMessage.getRecordedAtTime().getTime())).append("\",\n");
					sb.append("\"startTime\":\"").append(timeFormat.format(infoMessage.getRecordedAtTime().getTime())).append("\",\n");

					XmlObject anyContent = infoMessage.getContent();
					IDFGeneralMessageStructure content = (IDFGeneralMessageStructure) anyContent.changeType(IDFGeneralMessageStructure.type);
					IDFMessageStructure[] messages = content.getMessageArray();
					sb.append("\"message\":\"");
					for(IDFMessageStructure message : messages)
					{
						sb.append(message.getMessageText().getStringValue()).append(" ");
					}
					sb.append("\",\n");

					if (infoMessage.isSetValidUntilTime())
					{
						sb.append("\"endDate\":\"").append(dateFormat.format(infoMessage.getValidUntilTime().getTime())).append("\",\n");
						sb.append("\"endTime\":\"").append(timeFormat.format(infoMessage.getValidUntilTime().getTime())).append("\"\n");
					}
					else
					{
						sb.append("\"endDate\":\"\",\n");
						sb.append("\"endTime\":\"\"\n");
					}

					LineRefStructure[] lines = content.getLineRefArray();
					if (lines.length > 0)
					{
						sb.append(",\"lines\": [");
						String spsepar ="";
						for (LineRefStructure line : lines)
						{
							sb.append(spsepar);
							sb.append("{\"id\":\"").append(line.getStringValue()).append("\",");
							AnnotatedLineStructure lineInfo = linesInfo.get(line.getStringValue());
							if (lineInfo == null)
							{
								sb.append("\"name\":\"").append("").append("\"}");
							}
							else
							{
								sb.append("\"name\":\"").append(lineInfo.getLineName().getStringValue()).append("\"}");
							}
							spsepar = ",";
						}
						sb.append("]\n");
					}
					StopPointRefStructure[] stopPoints = content.getStopPointRefArray();
					if (stopPoints.length > 0)
					{
						sb.append(",\"stopPoints\": [");
						String spsepar ="";
						for (StopPointRefStructure stopPoint : stopPoints)
						{
							sb.append(spsepar);
							sb.append("{\"id\":\"").append(stopPoint.getStringValue()).append("\",");
							AnnotatedStopPointStructure stopInfo = stopPointsInfo.get(stopPoint.getStringValue());
							if (stopInfo == null)
							{
								sb.append("\"name\":\"").append("").append("\"}");
							}
							else
							{
								sb.append("\"name\":\"").append(stopInfo.getStopName().getStringValue()).append("\"}");
							}
							spsepar = ",";
						}
						sb.append("]\n");
					}
					sb.append("}\n");
					separ=",";
				}
				sb.append("]\n");
			}
			else
			{
				ServiceDeliveryErrorConditionStructure errorCondition = messageDelivery.getErrorCondition();
				sb.append(errorToJsonString(errorCondition));
			}  
		}
		sb.append("}\n");
		resp.getWriter().print(sb.toString());
		return ;

	}

	private void printJson(HttpServletResponse resp,
			LinesDiscoveryResponseDocument response) {
		// TODO Auto-generated method stub

	}



	private void printJson(HttpServletResponse resp, GetStopMonitoringResponseDocument response) throws IOException
	{
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		StopMonitoringDeliveryStructure[] stopMonitoringDeliveries = response.getGetStopMonitoringResponse().getAnswer().getStopMonitoringDeliveryArray();
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for (StopMonitoringDeliveryStructure stopMonitoringDelivery : stopMonitoringDeliveries) 
		{
			if(stopMonitoringDelivery.getStatus())
			{
				sb.append("\"monitoredStopVisits\": [ \n");
				MonitoredStopVisitStructure[] monitoredStopVisits = stopMonitoringDelivery.getMonitoredStopVisitArray();
				String separ = "";
				for (MonitoredStopVisitStructure monitoredStopVisit : monitoredStopVisits) 
				{
					sb.append(separ);
					sb.append("{\n");
					MonitoredCallStructure monitoredCall = monitoredStopVisit.getMonitoredVehicleJourney().getMonitoredCall();
					sb.append("\"id\":\"").append(monitoredCall.getStopPointRef().getStringValue()).append("\",\n");
					sb.append("\"name\":\"").append(monitoredCall.getStopPointName().getStringValue()).append("\",\n");

					MonitoredVehicleJourneyStructure monitoredVehicleJourney = monitoredStopVisit.getMonitoredVehicleJourney();
					sb.append("\"lineId\":\"").append(monitoredVehicleJourney.getLineRef().getStringValue()).append("\",\n");
					sb.append("\"lineName\":\"").append(monitoredVehicleJourney.getPublishedLineName().getStringValue()).append("\",\n");
					sb.append("\"directionName\":\"").append(monitoredVehicleJourney.getDirectionName().getStringValue()).append("\",\n");

					Enum[] vehicleModeArray = monitoredVehicleJourney.getVehicleModeArray();
					sb.append("\"transportModeName\":\"");
					for (Enum vehicleMode : vehicleModeArray)
					{
						sb.append(vehicleMode).append(" ");
					}
					sb.append("\",\n");

					sb.append("\"expectedDeparturTime\":\"");
					if (monitoredCall.getExpectedDepartureTime() != null)
					{
						sb.append(timeFormat.format(monitoredCall.getExpectedDepartureTime().getTime()));
					}
					sb.append("\",\n");
					sb.append("\"aimedDeparturTime\":\"");
					if (monitoredCall.getAimedDepartureTime() != null)
					{
						sb.append(timeFormat.format(monitoredCall.getAimedDepartureTime().getTime()));
					}
					sb.append("\",\n");

					sb.append("\"status\":\"");
					if (monitoredCall.getAimedDepartureTime() != null)
						sb.append(monitoredCall.getDepartureStatus());
					sb.append("\"");
					if (monitoredVehicleJourney.isSetOnwardCalls())
					{
						sb.append(",\n");
						sb.append("\"onwards\" :[");
						String separO = "";
						OnwardCallsStructure onwards = monitoredVehicleJourney.getOnwardCalls();
						for (OnwardCallStructure onward : onwards.getOnwardCallArray())
						{
							sb.append(separO);
							sb.append("{\n");
							sb.append("\"id\":\"").append(onward.getStopPointRef().getStringValue()).append("\",\n");
							sb.append("\"name\":\"").append(onward.getStopPointName().getStringValue()).append("\",\n");
							sb.append("\"expectedDeparturTime\":\"");
							if (onward.getExpectedDepartureTime() != null)
							{
								sb.append(timeFormat.format(onward.getExpectedDepartureTime().getTime()));
							}
							sb.append("\",\n");
							sb.append("\"aimedDeparturTime\":\"");
							if (onward.getAimedDepartureTime() != null)
							{
								sb.append(timeFormat.format(onward.getAimedDepartureTime().getTime()));
							}
							sb.append("\"}");
							separO=",\n";
						}
						sb.append("]\n");
					}


					sb.append("}\n");
					separ=",";
				}
				sb.append("]\n");
			}

			else
			{
				ServiceDeliveryErrorConditionStructure errorCondition = stopMonitoringDelivery.getErrorCondition();
				sb.append(errorToJsonString(errorCondition));
			}  
		}
		sb.append("}\n");
		resp.getWriter().print(sb.toString());
		return ;

	}


}
