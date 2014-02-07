package ish.oncourse.portal.services.pageload;

import org.apache.log4j.Logger;
@Deprecated
public enum UserAgent {


	IPHONE(true, "iphone"), IPOD(true, "ipod"), IPAD(true, "ipad"), ANDROID(true, "android"), BLACKBERRY(true, "blackberry"), DESKTOP (false, null);

	private static final Logger LOGGER = Logger.getLogger(UserAgent.class);
	private boolean mobile;
	private String agentId;
	
	private UserAgent(boolean isMobile, String agentId) {
		this.mobile = isMobile;
		this.agentId = agentId;
	}
	
	public boolean isMobile() {
		return mobile;
	}

	public String getAgentId() {
		return agentId;
	}

	public static UserAgent valueByAgentId(String userAgentId)
	{
		if (userAgentId == null)
		{
			LOGGER.warn("userAgentId is null");
			return DESKTOP;
		}


		for (UserAgent userAgent: UserAgent.values())
		{
			if (userAgent.getAgentId() != null)
			{
				if (userAgentId.toLowerCase().contains(userAgent.getAgentId()))
					return userAgent;
			}
		}
		LOGGER.info(String.format("UserAgent is not defined for id=%s. Use DESKTOP",userAgentId));
		return DESKTOP;
	}
}
