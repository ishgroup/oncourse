package ish.oncourse.services.cookies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;

public class CookiesService implements ICookiesService {

	public static final String COOKIES_DICTIONARY_REQUEST_ATTR = "cookiesDictionary";

	private static final String COOKIES_COLLECTION_SEPARATOR = ",";

	@Inject
	private Request request;

	@Inject
	private Cookies cookies;

	public Collection<String> getCookieCollectionValue(String cookieKey) {
		Collection<String> result = (Collection<String>) getCookieFromDictionary(cookieKey);
		if (result == null) {
			String resultString = getCookieValue(cookieKey);
			if (resultString == null) {
				return null;
			}
			String[] resultArray = resultString
					.split(COOKIES_COLLECTION_SEPARATOR);
			result = new ArrayList<String>(resultArray.length);
			for (String value : resultArray) {
				if (!"".equals(value)) {
					result.add(value);
				}
			}
			addCookieToDictionary(cookieKey, result);
		}
		return result;
	}

	/**
	 * @param cookieKey
	 * @param result
	 */
	private void addCookieToDictionary(String cookieKey,
			Collection<String> result) {
		Map<String, Object> cookiesDictionary = (Map<String, Object>) request
				.getAttribute(COOKIES_DICTIONARY_REQUEST_ATTR);
		if (cookiesDictionary == null) {
			cookiesDictionary = new HashMap<String, Object>();
		}
		cookiesDictionary.put(cookieKey, result);
		request
				.setAttribute(COOKIES_DICTIONARY_REQUEST_ATTR,
						cookiesDictionary);
	}

	/**
	 * @param cookieKey
	 */
	private Object getCookieFromDictionary(String cookieKey) {
		Map<String, Object> cookiesDictionary = (Map<String, Object>) request
				.getAttribute(COOKIES_DICTIONARY_REQUEST_ATTR);
		if (cookiesDictionary != null
				&& cookiesDictionary.containsKey(cookieKey)) {
			return cookiesDictionary.get(cookieKey);
		}
		return null;
	}

	public String getCookieValue(String cookieKey) {
		return cookies.readCookieValue(cookieKey);
	}

	public void resetRequestedCookies() {
		request.setAttribute(COOKIES_DICTIONARY_REQUEST_ATTR, null);
	}

	public void writeCookieCollectionValue(String cookieKey,
			Collection<String> cookieValue) {
		resetRequestedCookies();
		StringBuffer strBuff = new StringBuffer();
		for (String val : cookieValue) {
			strBuff.append(val).append(COOKIES_COLLECTION_SEPARATOR);
		}
		writeCookieValue(cookieKey, strBuff.substring(0, strBuff.length() - 1));

	}

	public void writeCookieValue(String cookieKey, String cookieValue) {
		cookies.writeCookieValue(cookieKey, cookieValue);
	}

}
