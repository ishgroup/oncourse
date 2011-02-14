package ish.oncourse.services.cookies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

public class CookiesService implements ICookiesService {

	public static final String COOKIES_DICTIONARY_REQUEST_ATTR = "cookiesDictionary";
	private static final String COOKIES_COLLECTION_SEPARATOR = "%";
	private static final String COOKIES_COLLECTION_SEPARATOR_REGEXP = "["
			+ COOKIES_COLLECTION_SEPARATOR + "]";
	private static final Logger LOGGER = Logger.getLogger(CookiesService.class);

	@Inject
	private Request request;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Cookies cookies;

	/**
	 * {@inheritDoc} <br/>
	 * Splits the cookie value by '
	 * {@value CookiesService#COOKIES_COLLECTION_SEPARATOR}.'
	 * 
	 * 
	 * @see ish.oncourse.services.cookies.ICookiesService#getCookieCollectionValue(java.lang.String)
	 */
	public <T> List<T> getCookieCollectionValue(String cookieKey, Class<T> clazz) {
		List<T> listResult = new ArrayList<T>();

		String[] result = (String[]) getCookieFromDictionary(cookieKey);
		if (result == null) {
			String resultString = getCookieValue(cookieKey);
			if (resultString == null || resultString.equals("")) {
				return listResult;
			}
			result = resultString.split(COOKIES_COLLECTION_SEPARATOR_REGEXP);
			addCookieToDictionary(cookieKey, result);

		}
		if (result.length > 0) {
			try {
				for (String id : result) {
					listResult.add(convertToInstance(id, clazz));
				}
			} catch (Exception e) {
				LOGGER.error("Exception while converting IDs", e);
			}
		}
		return listResult;

	}

	@SuppressWarnings("unchecked")
	private <T> T convertToInstance(String value, Class<T> clazz) {
		if (clazz == null || clazz.equals(String.class)) {
			return (T) value;
		}
		T number = null;

		if (clazz.equals(Byte.class)) {
			number = (T) new Byte(Byte.parseByte(value));
		} else if (clazz.equals(Double.class)) {
			number = (T) new Double(Double.parseDouble(value));
		} else if (clazz.equals(Float.class)) {
			number = (T) new Float(Float.parseFloat(value));
		} else if (clazz.equals(Integer.class)) {
			number = (T) new Integer(Integer.parseInt(value));
		} else if (clazz.equals(Long.class)) {
			number = (T) new Long(Long.parseLong(value));
		} else if (clazz.equals(Short.class)) {
			number = (T) new Short(Short.parseShort(value));
		}

		return number;
	}

	/**
	 * @param cookieKey
	 * @param result
	 */
	private void addCookieToDictionary(String cookieKey, Object result) {
		Map<String, Object> cookiesDictionary = (Map<String, Object>) request
				.getAttribute(COOKIES_DICTIONARY_REQUEST_ATTR);
		if (cookiesDictionary == null) {
			cookiesDictionary = new HashMap<String, Object>();
		}
		cookiesDictionary.put(cookieKey, result);
		request.setAttribute(COOKIES_DICTIONARY_REQUEST_ATTR, cookiesDictionary);
	}

	/**
	 * @param cookieKey
	 */
	private Object getCookieFromDictionary(String cookieKey) {
		Map<String, Object> cookiesDictionary = (Map<String, Object>) request
				.getAttribute(COOKIES_DICTIONARY_REQUEST_ATTR);
		if (cookiesDictionary != null && cookiesDictionary.containsKey(cookieKey)) {
			return cookiesDictionary.get(cookieKey);
		}
		return null;
	}

	public String getCookieValue(String cookieKey) {
		return cookies.readCookieValue(cookieKey);
	}

	public void appendValueToCookieCollection(String cookieKey, String cookieValue) {
		String existingValue = getCookieValue(cookieKey);
		// checks if this value already exists in this collection
		if (existingValue != null && !existingValue.equals(cookieValue)
				&& !existingValue.contains(cookieValue + COOKIES_COLLECTION_SEPARATOR)
				&& !existingValue.contains(COOKIES_COLLECTION_SEPARATOR + cookieValue)) {
			StringBuffer strBuff = new StringBuffer();
			if (!"".equals(existingValue)) {
				strBuff.append(existingValue);
				strBuff.append(COOKIES_COLLECTION_SEPARATOR);
			}
			strBuff.append(cookieValue);
			writeCookieValue(cookieKey, strBuff.toString());
		} else {
			writeCookieValue(cookieKey, cookieValue);
		}
	}

	public void writeCookieValue(String cookieKey, String cookieValue) {
		cookies.writeCookieValue(cookieKey, cookieValue, "/");
	}

	public void removeValueFromCookieCollection(String cookieKey, String cookieValue) {
		String existingValue = getCookieValue(cookieKey);
		String result;
		if (existingValue.lastIndexOf(COOKIES_COLLECTION_SEPARATOR) == -1) {
			result = existingValue.replaceAll(cookieValue, "");
		} else if (existingValue.lastIndexOf(COOKIES_COLLECTION_SEPARATOR) > existingValue
				.indexOf(cookieValue)) {
			result = existingValue
					.replaceAll(cookieValue + COOKIES_COLLECTION_SEPARATOR_REGEXP, "");
		} else {
			result = existingValue
					.replaceAll(COOKIES_COLLECTION_SEPARATOR_REGEXP + cookieValue, "");
		}
		writeCookieValue(cookieKey, result);
	}

}
