package ish.oncourse.services.cookies;

import java.util.List;

/**
 * Service for manipulating with cookies.
 * 
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
	 * @param clazz
	 *            the class for cookie representation(if null return String)
	 * @return array of values
	 */
	<T> List<T> getCookieCollectionValue(String cookieKey, Class<T> clazz);

	void writeCookieValue(String cookieKey, String cookieValue);

	void appendValueToCookieCollection(String cookieKey, String cookieValue);

	void removeValueFromCookieCollection(String cookieKey, String cookieValue);
}
