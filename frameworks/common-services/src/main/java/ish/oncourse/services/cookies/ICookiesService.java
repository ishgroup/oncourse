package ish.oncourse.services.cookies;
/**
 * Service for manipulating with cookies.
 * @author ksenia
 *
 */
public interface ICookiesService {
	String getCookieValue(String cookieKey);

	/**
	 * Retrieves the collection of values stored in cookies. Searches the cookie
	 * item with given cookieKey and splits its value to get the needed array.
	 * 
	 * @param cookieKey
	 * @return array of values
	 */
	String[] getCookieCollectionValue(String cookieKey);

	void writeCookieValue(String cookieKey, String cookieValue);

	void appendValueToCookieCollection(String cookieKey, String cookieValue);

	void removeValueFromCookieCollection(String cookieKey, String cookieValue);
}
