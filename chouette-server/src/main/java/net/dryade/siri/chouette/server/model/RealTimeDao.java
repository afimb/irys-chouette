package net.dryade.siri.chouette.server.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

public class RealTimeDao extends NamedParameterJdbcDaoSupport
{
	@Setter private String datedCallRequest;
	@Setter private String datedCallMonitoringRefFilter;
	@Setter private String datedCallDepartureStartHourFilterRequest;
	@Setter private String datedCallArrivalStartHourFilterRequest;
	@Setter private String datedCallDeparturePreviewIntervalFilterRequest;
	@Setter private String datedCallArrivalPreviewIntervalFilterRequest;
	@Setter private String datedCallLineFilterRequest;
	@Setter private String datedCallDestinationFilterRequest;
	@Setter private String datedCallOperatorFilterRequest;
	@Setter private String datedCallDepartureOrderByRequest;
	@Setter private String datedCallArrivalOrderByRequest;

	@Setter private String singleDatedCallRequest;

	@Setter private String onwardDatedCallRequest;

	@Setter private String generalMessageRequest;
	@Setter private String generalMessageChannelFilterRequest;
	@Setter private String generalMessageLangFilterRequest;
	@Setter private String generalMessageLineFilterRequest;
	@Setter private String generalMessageStopAreaFilterRequest;

	public List<DatedCall> getCalls(Date activeDate, List<String> stoppointids, Calendar startCal,Calendar endCal,boolean filterOnDeparture,String lineId, String operatorId, List<String> destinationIds, int limit )
	{
		// Build SQL request 
		String datedCallStartHourFilterRequest = datedCallDepartureStartHourFilterRequest;
		String datedCallPreviewIntervalFilterRequest = datedCallDeparturePreviewIntervalFilterRequest;
		String datedCallOrderByRequest = datedCallDepartureOrderByRequest;
		if (!filterOnDeparture)
		{
			datedCallStartHourFilterRequest = datedCallArrivalStartHourFilterRequest;
			datedCallPreviewIntervalFilterRequest = datedCallArrivalPreviewIntervalFilterRequest;
			datedCallOrderByRequest = datedCallArrivalOrderByRequest;
		}

		Map<String,Object> args = new HashMap<String, Object>();
		List<Object> argsForLog = new ArrayList<Object>();
		String preparedRequest = datedCallRequest+" ";
		args.put("date",activeDate);
		argsForLog.add(activeDate);

		// add stoppoint ids (spring jdbc take care of lists for in clause) 
		preparedRequest+=datedCallMonitoringRefFilter+" ";
		args.put("stopPoints",stoppointids);
		argsForLog.add(stoppointids);

		preparedRequest+=datedCallStartHourFilterRequest;
		Timestamp startTime = new Timestamp(startCal.getTimeInMillis());
		args.put("startTime",startTime);
		argsForLog.add(startTime);
		if (endCal != null)
		{
			Timestamp endTime = new Timestamp(endCal.getTimeInMillis());
			preparedRequest += datedCallPreviewIntervalFilterRequest;
			args.put("endTime",endTime);
			argsForLog.add(endTime);
		}
		if (isNotEmpty(lineId))
		{
			preparedRequest += datedCallLineFilterRequest+" ";
			args.put("line",lineId);
			argsForLog.add(lineId);
		}
		if (isNotEmpty(operatorId))
		{
			preparedRequest += datedCallOperatorFilterRequest+" ";
			args.put("operator",operatorId);
			argsForLog.add(operatorId);
		}
		if (isNotEmpty(destinationIds))
		{			
			// add destination ids (1 or multiple) (spring jdbc take care of lists for in clause)
			preparedRequest+=datedCallDestinationFilterRequest+" ";
			args.put("destinations",destinationIds);
			argsForLog.add(destinationIds);

		}
		preparedRequest += datedCallOrderByRequest;

		if (limit > 0)
		{
			preparedRequest += " LIMIT "+limit;
		}
		logger.debug("prepared request = "+preparedRequest);
		logger.debug("args = "+Arrays.toString(argsForLog.toArray()));

		// prepare statement 
		RowMapper<DatedCall> mapper = new CompleteDatedCallRowMapper();
		List<DatedCall> calls = getNamedParameterJdbcTemplate().query(preparedRequest, args, mapper );

		logger.debug("returns "+calls.size()+" datedCalls");
		return calls;
	}

	private boolean isNotEmpty(String string) 
	{
		return (string != null && !string.isEmpty());
	}

	public boolean checkDatabase()
	{
		try
		{
			getJdbcTemplate().execute("SELECT 1 AS test");
			return true;
		}
		catch (Exception ex)
		{
			logger.error("database access failed ",ex);
		}
		return false;
		
	}
	
	public DatedCall getDatedCall(Long vehicleJourneyId,String stopPointId)
	{
		Map<String,Object> args = new HashMap<String, Object>();
		String preparedRequest = singleDatedCallRequest;
		args.put("stopPoint",stopPointId);
		args.put("vehicleJourney",vehicleJourneyId);
		logger.debug("prepared request = "+preparedRequest);
		// logger.debug("args = "+Arrays.toString(args.toArray()));
		RowMapper<DatedCall> mapper = new DatedCallFiller();
		List<DatedCall> calls = getNamedParameterJdbcTemplate().query(preparedRequest, args, mapper );
		if (calls.isEmpty()) return null;
		return calls.get(0);
	}

	public List<DatedCall> getOnwardCalls(Long vehicleJourneyId,int position,int maxOnwards)
	{
		Map<String,Object> args = new HashMap<String, Object>();
		String preparedRequest = onwardDatedCallRequest;
		if (maxOnwards > 0)
		{
			preparedRequest += " LIMIT "+maxOnwards;
		}

		args.put("position",Integer.valueOf(position));
		args.put("vehicleJourney",vehicleJourneyId);
		logger.debug("prepared request = "+preparedRequest);
		// logger.debug("args = "+Arrays.toString(args.toArray()));
		RowMapper<DatedCall> mapper = new DatedCallFiller();
		return getNamedParameterJdbcTemplate().query(preparedRequest, args, mapper );
	}

	public List<GeneralMessage> getGeneralMessages(List<String> infoChannels,String lang, List<String> lineIds, List<String> stopAreaIds)
	{
		Map<String,Object> args = new HashMap<String, Object>();
		List<Object> argsForLog = new ArrayList<Object>();
		String preparedRequest = generalMessageRequest;
		boolean hasRefClause = (isNotEmpty(lineIds) || isNotEmpty(stopAreaIds));
		boolean hasInfoChannelClause = isNotEmpty(infoChannels);
		String separ = " ";


		args.put("date",Calendar.getInstance().getTime());
		argsForLog.add(Calendar.getInstance().getTime());
		if (hasInfoChannelClause)
		{
			preparedRequest += " "+generalMessageChannelFilterRequest;
			args.put("channels",infoChannels);
			argsForLog.add(infoChannels);
		}

		if (!isNotEmpty(lang)) lang = "FR" ;

		preparedRequest += " "+generalMessageLangFilterRequest;
		args.put("lang",lang);
		argsForLog.add(lang);
		if (hasRefClause)
		{
			preparedRequest += " AND ( ";
			if (isNotEmpty(lineIds))
			{
				preparedRequest += separ+generalMessageLineFilterRequest;
				args.put("lines",lineIds);
				argsForLog.add(lineIds);
				separ = " OR ";
			}
			if (isNotEmpty(stopAreaIds))
			{
				preparedRequest += separ+generalMessageStopAreaFilterRequest;
				args.put("stops",stopAreaIds);
				argsForLog.add(stopAreaIds);
				separ = " OR ";
			}

			// close parenthesis
			preparedRequest += ")";
		}

		logger.debug("prepared request = "+preparedRequest);
		logger.debug("args = "+Arrays.toString(argsForLog.toArray()));
		GeneralMessageCallBack callBack = new GeneralMessageCallBack();
		getNamedParameterJdbcTemplate().query(preparedRequest, args, callBack );

		return callBack.getMessages();
	}


	/**
	 * @param values
	 * @return
	 */
	//	public static String getArrayValues(List<? extends Object> values)
	//	{
	//		String val = "(";
	//		String separ = "";
	//		for (Object item : values)
	//		{
	//			val+=separ+getValue(item);
	//			if (separ.length() == 0) separ = ",";
	//		}
	//		val += ")";
	//		return val;
	//	}



	private boolean isNotEmpty(List<?> collection)
	{
		return (collection != null && !collection.isEmpty());
	}

	/**
	 *
	 */
	class DatedCallFiller implements RowMapper<DatedCall>,ResultSetExtractor<DatedCall>
	{

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		public DatedCall mapRow(ResultSet rst, int rowNum) throws SQLException 
		{
			return fill(rst);
		}


		public DatedCall extractData(ResultSet rst) 
		throws SQLException,DataAccessException 
		{
			rst.next();
			return fill(rst);
		}

		protected DatedCall fill(ResultSet rst) 
		throws SQLException,DataAccessException 
		{
			DatedCall dc = new DatedCall();
			dc.setStopPointId(rst.getString("stoppointid"));
			dc.setStatus(rst.getString("status"));
			dc.setPosition(rst.getInt("position"));
			dc.setExpectedDepartureTime(rst.getTimestamp("expecteddeparturetime"));
			dc.setExpectedArrivalTime(rst.getTimestamp("expectedarrivaltime"));
			dc.setAimedDepartureTime(rst.getTimestamp("aimeddeparturetime"));
			dc.setAimedArrivalTime(rst.getTimestamp("aimedarrivaltime"));

			return dc;
		}

	}

	/**
	 *
	 */
	class CompleteDatedCallRowMapper implements RowMapper<DatedCall>
	{

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		public DatedCall mapRow(ResultSet rst, int rowNum) throws SQLException 
		{
			DatedCallFiller filler = new DatedCallFiller();
			DatedCall dc = filler.fill(rst);
			DatedVehicleJourney dvj = new DatedVehicleJourney();
			dc.setVehicleJourney(dvj);
			dvj.setId(rst.getLong("dvjid"));
			dvj.setObjectId(rst.getString("objectid"));
			dvj.setLineId(rst.getString("lineid"));
			dvj.setRouteId(rst.getString("routeid"));
			dvj.setJourneyPatternId(rst.getString("journeypatternid"));
			dvj.setVehicleJourneyId(rst.getString("vehiclejourneyid"));
			dvj.setCompanyId(rst.getString("companyid"));
			dvj.setStatus(rst.getString("statusvalue"));
			dvj.setNumber(rst.getInt("number"));
			Long vehicleid = rst.getLong("vehicleid");
			if (!rst.wasNull())
			{
				Vehicle vehicle = new Vehicle();
				vehicle.setId(vehicleid);
				vehicle.setVehicleTypeIdentifier(rst.getString("vehicletypeidentifier"));
				vehicle.setStatus(rst.getString("vehiclestatus"));
				vehicle.setInCongestion(rst.getBoolean("incongestion"));
				vehicle.setInPanic(rst.getBoolean("inpanic"));
				vehicle.setLongitude(toBigDecimal(rst,"longitude"));
				vehicle.setLatitude(toBigDecimal(rst,"latitude"));
				vehicle.setX(toBigDecimal(rst,"x"));
				vehicle.setY(toBigDecimal(rst,"y"));
				vehicle.setProjectionType(rst.getString("projectiontype"));
				vehicle.setMonitored(rst.getBoolean("ismonitored"));
				vehicle.setMonitoringError(rst.getString("monitoringerror"));
				vehicle.setBearing(toBigDecimal(rst,"bearing"));
				vehicle.setDelay(rst.getTime("delay"));
				VehicleService service = new VehicleService();
				service.setVehicle(vehicle);
				dvj.setService(service);
			}
			return dc;
		}

		private BigDecimal toBigDecimal(ResultSet rst, String value) throws SQLException
		{
			double d = rst.getDouble(value);
			if (rst.wasNull()) return null;
			return BigDecimal.valueOf(d);
		}

	}

	private class GeneralMessageCallBack implements RowCallbackHandler
	{

		@Getter private List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

		private Map<Long,GeneralMessage> messageMap = new HashMap<Long, GeneralMessage>();

		public void processRow(ResultSet rst) throws SQLException 
		{
			Long id = rst.getLong("id");
			GeneralMessage gm = messageMap.get(id);
			if (gm == null)
			{
				gm = new GeneralMessage();
				gm.setId(id);
				messageMap.put(id, gm);
				messages.add(gm);
				gm.setInfoChannel(rst.getString("infochannel"));
				gm.setCreationTime(rst.getTimestamp("creation_date"));
				gm.setLastModificationDate(rst.getTimestamp("last_modification_date"));
				gm.setValidUntilDate(rst.getTimestamp("valid_until_date"));
				gm.setStatus(rst.getString("status"));
				gm.setObjectId(rst.getString("objectid"));
			}
			String refId = rst.getString("line_id");
			if (refId != null)
			{
				gm.addLineId(refId);
			}
			refId = rst.getString("stoparea_id");
			if (refId != null)
			{
				gm.addStopAreaId(refId);
			}
			String text = rst.getString("text");
			if (text != null)
			{
				String lang = rst.getString("language");
				if (lang == null) lang = "FR";
				String type = rst.getString("type");
				if (type != null)
				{
					GeneralMessage.Message msg = gm.new Message(type, lang, text);
					gm.addMessage(msg);
				}
			}

		}

	}

}
