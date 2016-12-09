package ish.oncourse.function;

import org.apache.tapestry5.services.Request;

public class GetServerName implements IGet<String> {
	
	private static final String TECHNICAL_DOMAIN = "live.oncourse.net.au";
	private static final String ACTUAL_DOMAIN = "oncourse.cc";
	private Request request;
	
	private GetServerName() {
		
	}
	
	public static GetServerName valueOf(Request request) {
		GetServerName getServerName = new GetServerName();
		getServerName.request = request;
		return getServerName;
	}

	@Override
	public String get() {
		String serverName = request.getServerName();
		if (serverName.contains(TECHNICAL_DOMAIN)) {
			serverName = serverName.replace(TECHNICAL_DOMAIN, ACTUAL_DOMAIN);
		}
		
		return serverName;
	}
}
