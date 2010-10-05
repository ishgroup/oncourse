package ish.oncourse.services.cookies;

import java.util.Collection;

public interface ICookiesService {
	String getCookieValue(String cookieKey);
	Collection<String> getCookieCollectionValue(String cookieKey);
	void resetRequestedCookies();
	void writeCookieValue(String cookieKey, String cookieValue);
	void writeCookieCollectionValue(String cookieKey, Collection<String> cookieValue);
}
