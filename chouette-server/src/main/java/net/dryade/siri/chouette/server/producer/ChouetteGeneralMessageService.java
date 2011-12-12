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
package net.dryade.siri.chouette.server.producer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.dryade.siri.chouette.server.model.GeneralMessage;
import net.dryade.siri.chouette.server.model.GeneralMessage.Message;
import net.dryade.siri.chouette.server.model.RealTimeDao;
import net.dryade.siri.chouette.server.model.Referential;
import net.dryade.siri.server.common.SiriException;
import net.dryade.siri.server.common.SiriTool;
import net.dryade.siri.server.producer.service.AbstractGeneralMessageService;

import org.apache.log4j.Logger;
import org.w3.xml.x1998.namespace.LangAttribute.Lang;

import uk.org.siri.siri.ContextualisedRequestStructure;
import uk.org.siri.siri.GeneralMessageDeliveriesStructure;
import uk.org.siri.siri.GeneralMessageDeliveryStructure;
import uk.org.siri.siri.GeneralMessageRequestStructure;
import uk.org.siri.siri.IDFGeneralMessageRequestFilterDocument;
import uk.org.siri.siri.IDFGeneralMessageRequestFilterStructure;
import uk.org.siri.siri.IDFGeneralMessageStructure;
import uk.org.siri.siri.IDFMessageStructure;
import uk.org.siri.siri.IDFMessageTypeEnumeration;
import uk.org.siri.siri.InfoChannelRefStructure;
import uk.org.siri.siri.InfoMessageStructure;
import uk.org.siri.siri.LineRefStructure;
import uk.org.siri.siri.NaturalLanguageStringStructure;
import uk.org.siri.siri.StopPointRefStructure;
import uk.org.siri.siri.impl.IDFMessageTypeEnumerationImpl;
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


	public ChouetteGeneralMessageService() 
	{
	}

	public void init()
	{
		super.init();
	}

	/* (non-Javadoc)
	 * @see net.dryade.siri.server.producer.GeneralMessageInterface#getGeneralMessage(uk.org.siri.siri.ContextualisedRequestStructure, uk.org.siri.siri.GeneralMessageRequestStructure, java.util.Calendar)
	 */
	public GeneralMessageDeliveriesStructure getGeneralMessage(ContextualisedRequestStructure serviceRequestInfo,GeneralMessageRequestStructure request,Calendar responseTimestamp) throws SiriException 
	{

		String lang = request.getLanguage();
		// lang si non renseigné
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
	 * @see net.dryade.siri.server.producer.service.AbstractGeneralMessageService#getGeneralMessages(uk.org.siri.siri.GeneralMessageRequestStructure)
	 */
	public List<InfoMessageStructure> getGeneralMessages(GeneralMessageRequestStructure request) throws SiriException
	{
		String lang = request.getLanguage();
		boolean encoded = isGmEncoded();

		// lang si non renseigné
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
		Set<String> lineFilter = new HashSet<String>();
		Set<String> stopFilter = new HashSet<String>();

		IDFGeneralMessageRequestFilterDocument idfextDoc = extractIDFFilterExtension(request,logger);
		if (idfextDoc != null)
		{
			// TODO : tester l'existance des lignes et arrêts dans la base !!
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
						lineFilter.add(line.getObjectId());
						List<String> areaIds = referential.getAreaIdsForLine(line.getObjectId());
                        if (areaIds != null)
                        {
                        	stopFilter.addAll(areaIds);
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
						stopFilter.add(area.getObjectId());
						List<String> lineIds = referential.getLineIdsForArea(area.getObjectId());
                        if (lineIds != null)
                        {
                        	lineFilter.addAll(lineIds);
                        }
					}
					else
					{
						throw new SiriException(SiriException.Code.BAD_ID,"unknown stopPoint id : "+stopRef.getStringValue());
					}

				}
			}
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

		List<GeneralMessage> messages = realTimeDao.getGeneralMessages(info, lang, new ArrayList<String>(lineFilter), new ArrayList<String>(stopFilter));

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
			for (String lineId : generalMessage.getLineIds())
			{
				Line line = referential.getLine(lineId);
				if (line != null)
				{
					String lineRef = chouetteTool.toSiriId(lineId, SiriTool.ID_LINE);
					LineRefStructure lineStr = gMessage.addNewLineRef();
					lineStr.setStringValue(lineRef); 
				}
			}

			// stoppointref 
			for (String stopId : generalMessage.getStopAreaIds())
			{           
				StopArea area = referential.getStopArea(stopId);
				if (area != null)
				{
					String stopPointRef = chouetteTool.toSiriId(stopId, SiriTool.ID_STOPPOINT, area.getAreaType());
					StopPointRefStructure stopPoint = gMessage.addNewStopPointRef();
					stopPoint.setStringValue(stopPointRef);
				}
			}

			// à faire en dernier sinon les données d'habillage ne sont pas prises en compte
			message.setContent(gMessage);

		}
		return infoMessageList;
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