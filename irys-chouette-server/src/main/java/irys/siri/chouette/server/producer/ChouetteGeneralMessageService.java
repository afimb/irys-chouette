/**
 *   Siri Product - Produit SIRI
 *  
 *   a set of tools for easy application building with 
 *   respect of the France Siri Local Agreement
 *
 *   un ensemble d'outils facilitant la realisation d'applications
 *   respectant le profil France de la norme SIRI
 * 
 *   Copyright DRYADE 2009-2010
 */
package irys.siri.chouette.server.producer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import irys.common.SiriException;
import irys.common.SiriTool;
import irys.siri.chouette.ChouetteTool;
import irys.siri.chouette.Referential;
import irys.siri.chouette.server.model.GeneralMessage;
import irys.siri.chouette.server.model.GeneralMessageType;
import irys.siri.chouette.server.model.RealTimeDao;
import irys.siri.chouette.server.model.GeneralMessage.Message;
import irys.siri.server.producer.service.AbstractGeneralMessageService;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlString;
import org.w3.xml.x1998.namespace.LangAttribute.Lang;

import irys.uk.org.siri.siri.ContextualisedRequestStructure;
import irys.uk.org.siri.siri.GeneralMessageDeliveriesStructure;
import irys.uk.org.siri.siri.GeneralMessageDeliveryStructure;
import irys.uk.org.siri.siri.GeneralMessageRequestStructure;
import irys.uk.org.siri.siri.IDFGeneralMessageRequestFilterDocument;
import irys.uk.org.siri.siri.IDFGeneralMessageRequestFilterStructure;
import irys.uk.org.siri.siri.IDFGeneralMessageStructure;
import irys.uk.org.siri.siri.IDFMessageStructure;
import irys.uk.org.siri.siri.IDFMessageTypeEnumeration;
import irys.uk.org.siri.siri.InfoChannelRefStructure;
import irys.uk.org.siri.siri.InfoMessageStructure;
import irys.uk.org.siri.siri.LineRefStructure;
import irys.uk.org.siri.siri.NaturalLanguageStringStructure;
import irys.uk.org.siri.siri.StopPointRefStructure;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;


public class ChouetteGeneralMessageService extends AbstractGeneralMessageService
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ChouetteGeneralMessageService.class);

	@Getter @Setter private boolean gmEncoded = false;

	@Setter private Referential referential;

	@Setter private ChouetteTool chouetteTool;
	@Setter private RealTimeDao realTimeDao; 

	@Setter private boolean indirectRelations = true;

	private GeneralMessageType messageType = GeneralMessageType.stif_idf;

	public ChouetteGeneralMessageService() 
	{
	}

	public void init()
	{
		super.init();
	}

	public void setFormatRef(String type)
	{
		super.setFormatRef(type);
		messageType = GeneralMessageType.valueOf(type.toLowerCase().replaceAll("-", "_"));
	}

	/* (non-Javadoc)
	 * @see irys.siri.server.producer.GeneralMessageInterface#getGeneralMessage(irys.uk.org.siri.siri.ContextualisedRequestStructure, irys.uk.org.siri.siri.GeneralMessageRequestStructure, java.util.Calendar)
	 */
	public GeneralMessageDeliveriesStructure getGeneralMessage(ContextualisedRequestStructure serviceRequestInfo,GeneralMessageRequestStructure request,Calendar responseTimestamp) throws SiriException 
	{

		String lang = request.getLanguage();
		// lang si non renseigne
		if (lang == null || lang.trim().length() == 0) 
		{
			lang = getDefaultLang();
		}
		if(!getLanguages().contains(lang))
		{
			logger.error("langage inconnu = "+lang);
			throw new SiriException(SiriException.Code.BAD_PARAMETER,"bad language : "+lang);
		}

		GeneralMessageDeliveriesStructure structure = GeneralMessageDeliveriesStructure.Factory.newInstance();
		GeneralMessageDeliveryStructure messageDelivery = structure.addNewGeneralMessageDelivery();
		messageDelivery.setStatus(true);
		messageDelivery.setResponseTimestamp(responseTimestamp);
		messageDelivery.setVersion(request.getVersion());

		messageDelivery.setGeneralMessageArray(getGeneralMessages(request).toArray(new InfoMessageStructure[0]));
		return structure;
	}


	/* (non-Javadoc)
	 * @see irys.siri.server.producer.service.AbstractGeneralMessageService#getGeneralMessages(irys.uk.org.siri.siri.GeneralMessageRequestStructure)
	 */
	public List<InfoMessageStructure> getGeneralMessages(GeneralMessageRequestStructure request) throws SiriException
	{
		String lang = request.getLanguage();
		boolean encoded = isGmEncoded();

		// lang si non renseigne
		if (lang == null || lang.trim().length() == 0) 
		{
			lang = getDefaultLang();
		}
		if(!getLanguages().contains(lang))
		{
			logger.error("langage inconnu = "+lang);
			throw new SiriException(SiriException.Code.BAD_PARAMETER,"bad language : "+lang);
		}

		InfoChannelRefStructure[] infos = request.getInfoChannelRefArray();
		Set<Long> lineFilter = new HashSet<Long>();
		Set<Long> stopFilter = new HashSet<Long>();

		switch (messageType)
		{
		case simple_text: 
			break; // no filter available
		case stif_idf: getStifIdfFilter(request, lineFilter, stopFilter); 
		break;
		}


		List<String> info = new ArrayList<String>();
		if (infos.length != 0)
		{
			for (InfoChannelRefStructure infoChannelRefStructure : infos)
			{
				String infoChannel = infoChannelRefStructure.getStringValue();
				if (encoded) infoChannel = decode(infoChannel);

				if (!getInfoChannelList().contains(infoChannel))
				{
					throw new SiriException(SiriException.Code.BAD_PARAMETER,"Invalid InfoChannel "+infoChannelRefStructure.getStringValue());
				}
				info.add(infoChannel);
			}
		}

		List<GeneralMessage> messages = realTimeDao.getGeneralMessages(info, lang, new ArrayList<Long>(lineFilter), new ArrayList<Long>(stopFilter));

		List<InfoMessageStructure> infoMessageList = new ArrayList<InfoMessageStructure>();
		for (GeneralMessage generalMessage : messages)
		{
			Long idMessage = Long.valueOf(generalMessage.getId());
			InfoMessageStructure message = InfoMessageStructure.Factory.newInstance();
			infoMessageList.add(message);

			message.addNewFormatRef().setStringValue(getFormatRef());
			message.setInfoMessageVersion(new BigInteger(Long.toString(generalMessage.getObjectVersion())));

			String infoChannel = generalMessage.getInfoChannel();
			if (encoded) infoChannel = encode(infoChannel);
			message.addNewInfoChannelRef().setStringValue(infoChannel);

			Calendar recorded = Calendar.getInstance();
			recorded.setTime(generalMessage.getLastModificationDate());
			message.setRecordedAtTime(recorded);

			if (generalMessage.getValidUntilDate() != null)
			{
				Calendar valid = Calendar.getInstance();
				valid.setTime(generalMessage.getValidUntilDate());
				message.setValidUntilTime(valid);
			}

			message.addNewInfoMessageIdentifier().setStringValue(generalMessage.getObjectId());

			message.setItemIdentifier(idMessage.toString());

			switch (messageType) 
			{
			case simple_text: populateSimpleTextMessage(generalMessage, message);
			break;

			case stif_idf: populateStifIdfMessage(generalMessage, message);
			break;
			}

		}
		return infoMessageList;
	}

	/**
	 * @param generalMessage
	 * @param message
	 * @throws SiriException
	 */
	private void populateSimpleTextMessage(GeneralMessage generalMessage,
			InfoMessageStructure message) throws SiriException {
		XmlString gMessage = XmlString.Factory.newInstance();
		// forward only first message
		for (Message msg : generalMessage.getMessages()) 
		{
			gMessage.setStringValue(msg.getText());
			break;
		}
		message.setContent(gMessage);
	}

	/**
	 * @param generalMessage
	 * @param message
	 * @throws SiriException
	 */
	private void populateStifIdfMessage(GeneralMessage generalMessage,
			InfoMessageStructure message) throws SiriException {
		IDFGeneralMessageStructure gMessage = IDFGeneralMessageStructure.Factory.newInstance();

		for (Message msg : generalMessage.getMessages()) 
		{
			IDFMessageStructure messageIDF = gMessage.addNewMessage();
			messageIDF.setMessageType(IDFMessageTypeEnumeration.Enum.forString(msg.getType()));
			if (messageIDF.getMessageType() == null)
				throw new SiriException(SiriException.Code.INTERNAL_ERROR,"unknown message type "+msg.getType());

			NaturalLanguageStringStructure messageText = messageIDF.addNewMessageText();
			messageText.setStringValue(msg.getText());
			messageText.setLang(Lang.Enum.forString(msg.getLang()));
		}


		// lineref 
		for (Long lineId : generalMessage.getLineIds())
		{
			Line line = referential.getLine(lineId);
			if (line != null)
			{
				String lineRef = chouetteTool.toSiriId(line.getObjectId(), SiriTool.ID_LINE);
				LineRefStructure lineStr = gMessage.addNewLineRef();
				lineStr.setStringValue(lineRef); 
			}
			else
			{
				logger.warn("missing line : id = "+lineId);
			}
		}

		// stoppointref 
		for (Long stopId : generalMessage.getStopAreaIds())
		{           
			StopArea area = referential.getStopArea(stopId);
			if (area != null)
			{
				String stopPointRef = chouetteTool.toSiriId(area.getObjectId(), SiriTool.ID_STOPPOINT, area.getAreaType());
				StopPointRefStructure stopPoint = gMessage.addNewStopPointRef();
				stopPoint.setStringValue(stopPointRef);
			}
			else
			{
				logger.warn("missing stop area : id = "+stopId);
			}
		}

		// a faire en dernier sinon les donnees d'habillage ne sont pas prises en compte
		message.setContent(gMessage);
	}

	/**
	 * @param request
	 * @param lineFilter
	 * @param stopFilter
	 * @throws SiriException
	 */
	private void getStifIdfFilter(GeneralMessageRequestStructure request,
			Set<Long> lineFilter, Set<Long> stopFilter)
					throws SiriException {
		IDFGeneralMessageRequestFilterDocument idfextDoc = extractIDFFilterExtension(request,logger);
		if (idfextDoc != null)
		{
			IDFGeneralMessageRequestFilterStructure idfFilter = idfextDoc.getIDFGeneralMessageRequestFilter();
			if (idfFilter.sizeOfLineRefArray() > 0)
			{
				LineRefStructure[] lineRefs = idfFilter.getLineRefArray();
				for (LineRefStructure lineRef : lineRefs)
				{
					String id = lineRef.getStringValue();
					// check syntax
					Line line = referential.getLineFromSiri(id);
					if (line != null)
					{
						lineFilter.add(line.getId());
						if (indirectRelations)
						{
							List<Long> areaIds = referential.getAreaIdsForLine(line.getId());
							if (areaIds != null)
							{
								stopFilter.addAll(areaIds);
							}
						}
					}
					else
					{
						throw new SiriException(SiriException.Code.BAD_ID,"unknown Line id : "+id);
					}
				}
			}
			else if (idfFilter.sizeOfStopPointRefArray() > 0)
			{
				StopPointRefStructure[] stopRefs = idfFilter.getStopPointRefArray();
				for (StopPointRefStructure stopRef : stopRefs)
				{
					if (stopRef.getStringValue().contains(SiriTool.ID_SPOR)) continue;
					String id = stopRef.getStringValue();
					StopArea area = referential.getStopAreaFromSiri(id);
					if (area != null)
					{
						stopFilter.add(area.getId());
						if (indirectRelations)
						{
							List<Long> lineIds = referential.getLineIdsForArea(area.getId());
							if (lineIds != null)
							{
								lineFilter.addAll(lineIds);
							}
						}
					}
					else
					{
						throw new SiriException(SiriException.Code.BAD_ID,"unknown stopPoint id : "+stopRef.getStringValue());
					}

				}
			}
		}
	}

	private String encode(String infoChannel) throws SiriException
	{

		if (infoChannel.equals("Perturbation")) return "1";
		if (infoChannel.equals("Information")) return "2";
		return "3";
		// throw new SiriException(SiriException.Code.BAD_PARAMETER,"unknown infochannel :"+infoChannel);

	}

	private String decode(String infoChannel) throws SiriException 
	{
		if (infoChannel == null) 
		{
			logger.warn("infoChannel code null");
			return "";
		}
		if (infoChannel.equals("1")) return "Perturbation";
		if (infoChannel.equals("2")) return "Information";
		if (infoChannel.equals("3")) return "Commercial";
		throw new SiriException(SiriException.Code.BAD_PARAMETER,"unknown infochannel :"+infoChannel);
	}


}