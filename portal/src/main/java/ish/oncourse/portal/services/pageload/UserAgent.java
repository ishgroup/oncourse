package ish.oncourse.portal.services.pageload;

public enum UserAgent {
	
	IPHONE(true), IPOD(true), IPAD(true), ANDROID(true), BLACKBERRY(true), DESKTOP (false);
	
	private boolean mobile;
	
	private UserAgent(boolean isMobile) {
		this.mobile = isMobile;
	}
	
	public boolean isMobile() {
		return mobile;
	}
}
