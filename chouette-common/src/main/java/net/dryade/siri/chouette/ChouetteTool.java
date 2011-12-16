package net.dryade.siri.chouette;

import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import net.dryade.siri.common.SiriException;
import net.dryade.siri.common.SiriTool;

public class ChouetteTool extends SiriTool
{

	/**
	 * @param neptuneId
	 * @param type
	 * @return
	 */
	public String toSiriId(String neptuneId,String type)
	{
		String[] token = neptuneId.split(":");
		String prefix = token[0];
		String technicalId = token[2];
		return buildId(technicalId, type, prefix);
	}

	/**
	 * @param neptuneId
	 * @param type
	 * @param areaType
	 * @return
	 */
	public String toSiriId(String neptuneId,String type,ChouetteAreaEnum areaType)
	{
		String[] token = neptuneId.split(":");
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
