package irys.siri.chouette.server.model;

import irys.uk.org.siri.siri.StopMonitoringDetailEnumeration;

public enum DetailLevelEnum 
{
	minimum,basic,normal,calls,full;

	public static DetailLevelEnum forStopMonitoringDetailLevel(StopMonitoringDetailEnumeration.Enum level)
	{
		return valueOf(level.toString().toLowerCase());
	}

	public static DetailLevelEnum forString(String level)
	{
		return valueOf(level.toLowerCase());
	}

	public boolean isValidFor(DetailLevelEnum other) 
	{
		return this.ordinal() >= other.ordinal();
	}
}
