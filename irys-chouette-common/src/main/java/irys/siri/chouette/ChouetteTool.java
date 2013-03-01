package irys.siri.chouette;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import irys.common.SiriException;
import irys.common.SiriTool;

public class ChouetteTool extends SiriTool
{
    private static final Logger logger = Logger.getLogger(ChouetteTool.class);

	/**
	 * @param neptuneId
	 * @param type
	 * @return
	 * @throws SiriException 
	 */
	public String toSiriId(String neptuneId,String type) throws SiriException
	{
		String[] token = neptuneId.split(":");
		if (token.length != 3)
		{
			logger.error("Malformed Neptune id = "+neptuneId);
			throw new SiriException(SiriException.Code.INTERNAL_ERROR, "invalid data in referential : "+neptuneId);
		}
		String prefix = token[0];
		String technicalId = token[2];
		return buildId(technicalId, type, prefix);
	}

	/**
	 * @param neptuneId
	 * @param type
	 * @param areaType
	 * @return
	 * @throws SiriException 
	 */
	public String toSiriId(String neptuneId,String type,ChouetteAreaEnum areaType) throws SiriException
	{
		String[] token = neptuneId.split(":");
		if (token.length != 3)
		{
			logger.error("Malformed Neptune id = "+neptuneId);
			throw new SiriException(SiriException.Code.INTERNAL_ERROR, "invalid data in referential : "+neptuneId);
		}
		String prefix = token[0];
		String technicalId = token[2];
		String subType = toSiriSubtype(areaType);
		return buildId(technicalId, type, subType, prefix);
	}

	/**
	 * @param areaType
	 * @return
	 */
	private String toSiriSubtype(ChouetteAreaEnum areaType)
	{
		if (areaType == null) return SiriTool.ID_SPOR;
		switch (areaType) 
		{
		case BOARDINGPOSITION : return SiriTool.ID_BP;
		case QUAY : return SiriTool.ID_QUAY;
		default: return SiriTool.ID_SP;
		}
	}

	/**
	 * convert id from Siri to Neptune
	 * 
	 * @param siriId siri iidentifiaction 
	 * @param siriType siri object type (see constants in SiriTool)
	 * @param neptuneType neptune object (see constants in NeptuneIdentifiedObject)
	 * @return neptune id
	 * @throws SiriException malformed siri id
	 */
	public String toNeptuneId(String siriId,String siriType,String neptuneType) throws SiriException
	{
		String technicalId = extractId(siriId, siriType);
		String prefix = siriId.split(":")[0];
		return prefix+":"+neptuneType+":"+technicalId;
	}
	
}
