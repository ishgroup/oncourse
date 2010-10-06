package ish.oncourse.services.cookies;


public interface ICookiesService {
	String getCookieValue(String cookieKey);
	String[] getCookieCollectionValue(String cookieKey);
	void writeCookieValue(String cookieKey, String cookieValue);
	void appendValueToCookieCollection(String cookieKey, String cookieValue);
	void removeValueFromCookieCollection(String cookieKey, String cookieValue);
}
