package ish.oncourse.services.cookies;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;

public class CookiesService implements ICookiesService {

	public static final String COOKIES_DICTIONARY_REQUEST_ATTR = "cookiesDictionary";
	//at the old site the view of shortlist cookie is "shortlist=CourseClass%3A123234%2C456785"
	//our view will be "shortlist=123234%456785"
	//sort out if we need the entityname and what does the letters mean
	private static final String COOKIES_COLLECTION_SEPARATOR = "%";
	private static final String COOKIES_COLLECTION_SEPARATOR_REGEXP = "["+COOKIES_COLLECTION_SEPARATOR+"]";
	
	@Inject
	private Request request;

	@Inject
	private Cookies cookies;

	public String[] getCookieCollectionValue(String cookieKey) {
		String[] result = (String[]) getCookieFromDictionary(cookieKey);
		if (result == null) {
			String resultString = getCookieValue(cookieKey);
			if (resultString == null) {
				return null;
			}
			result = resultString
					.split(COOKIES_COLLECTION_SEPARATOR_REGEXP);
			addCookieToDictionary(cookieKey, result);
		}
		return result;
	}

	/**
	 * @param cookieKey
	 * @param result
	 */
	private void addCookieToDictionary(String cookieKey,
			Object result) {
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

	public void appendValueToCookieCollection(String cookieKey,
			String cookieValue) {
		String existingValue=getCookieValue(cookieKey);
		StringBuffer strBuff = new StringBuffer();
		if(existingValue!=null&&!"".equals(existingValue)){
			strBuff.append(existingValue);
			strBuff.append(COOKIES_COLLECTION_SEPARATOR);
		}
		strBuff.append(cookieValue);
		writeCookieValue(cookieKey,strBuff.toString());

	}

	public void writeCookieValue(String cookieKey, String cookieValue) {
		cookies.writeCookieValue(cookieKey, cookieValue);
	}

	public void removeValueFromCookieCollection(String cookieKey,
			String cookieValue) {
		String existingValue=getCookieValue(cookieKey);
		String result;
		if(existingValue.lastIndexOf(COOKIES_COLLECTION_SEPARATOR)==-1){
			result=existingValue.replaceAll(cookieValue, "");
		}else
		if(existingValue.lastIndexOf(COOKIES_COLLECTION_SEPARATOR)>existingValue.indexOf(cookieValue)){
			result=existingValue.replaceAll(cookieValue+COOKIES_COLLECTION_SEPARATOR_REGEXP, "");
		}else{
			result=existingValue.replaceAll(COOKIES_COLLECTION_SEPARATOR_REGEXP+cookieValue, "");
		}
		writeCookieValue(cookieKey,result);
	}

}
