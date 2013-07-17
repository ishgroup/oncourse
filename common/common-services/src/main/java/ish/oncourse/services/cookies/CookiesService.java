package ish.oncourse.services.cookies;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Product;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class CookiesService implements ICookiesService {
	public static final String CLIENT_TIMEZONE_OFFSET_IN_MINUTES = "clientOffset";
	public static final String CLIENT_TIMEZONE_NAME = "clientTimezoneName";
	public static final String COOKIES_DICTIONARY_REQUEST_ATTR = "cookiesDictionary";
	private static final String COOKIES_COLLECTION_SEPARATOR = "%";
	private static final String COOKIES_COLLECTION_SEPARATOR_REGEXP = "[" + COOKIES_COLLECTION_SEPARATOR + "]";
	private static final Logger LOGGER = Logger.getLogger(CookiesService.class);

	@Inject
	private Request request;

	@Inject
	private ICookiesOverride cookiesOverride;

	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private IVoucherService voucherService;

	@Inject
	private IDiscountService discountService;

	@Override
	public TimeZone getClientTimezone() {
		String value = StringUtils.trimToNull(getCookieValue(CLIENT_TIMEZONE_NAME));
		if (value != null) {
			return TimeZone.getTimeZone(value);
		}
		return null;
	}
	
	@Override
	public TimeZone getSimpleClientTimezone() {
		Integer offset = getClientTimezoneOffset();
		if (offset != null) {
			offset = offset * 60000;
			return new SimpleTimeZone(offset, "GMT");
		}
		return null;
	}

	Integer getClientTimezoneOffset() {
		String value = StringUtils.trimToNull(getCookieValue(CLIENT_TIMEZONE_OFFSET_IN_MINUTES));
		Integer offset = null;

		if (StringUtils.isNumeric(value))
		{
			offset = Integer.valueOf(value);
		}
		return offset;
	}

	/**
	 * {@inheritDoc} <br/>
	 * Splits the cookie value by '
	 * {@value CookiesService#COOKIES_COLLECTION_SEPARATOR}.'
	 * 
	 *  
	 * @see ish.oncourse.services.cookies.ICookiesService#getCookieValue(String)
	 */
	public <T> List<T> getCookieCollectionValue(String cookieKey, Class<T> clazz) {
		List<T> listResult = new ArrayList<>();

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
					T convertedInstance = convertToInstance(id, clazz);
					if (convertedInstance != null) {
						listResult.add(convertedInstance);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Exception while converting IDs", e);
			}
		}
		return listResult;

	}

	@SuppressWarnings("unchecked")
	protected <T> T convertToInstance(String value, Class<T> clazz) {
		if (clazz == null || clazz.equals(String.class)) {
			return (T) value;
		}
		if (StringUtils.isNumeric(value) || (StringUtils.isNumeric(value.replaceFirst("[.]", ""))&&clazz.equals(Float.class)||clazz.equals(Double.class))) {
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
		return null;
	}

	/**
	 * @param cookieKey
	 * @param result
	 */
	private void addCookieToDictionary(String cookieKey, Object result) {
		@SuppressWarnings("unchecked")
		Map<String, Object> cookiesDictionary = (Map<String, Object>) request
				.getAttribute(COOKIES_DICTIONARY_REQUEST_ATTR);
		if (cookiesDictionary == null) {
			cookiesDictionary = new HashMap<>();
		}
		cookiesDictionary.put(cookieKey, result);
		request.setAttribute(COOKIES_DICTIONARY_REQUEST_ATTR, cookiesDictionary);
	}

	/**
	 * @param cookieKey
	 */
	private Object getCookieFromDictionary(String cookieKey) {
		@SuppressWarnings("unchecked")
		Map<String, Object> cookiesDictionary = (Map<String, Object>) request
				.getAttribute(COOKIES_DICTIONARY_REQUEST_ATTR);
		if (cookiesDictionary != null && cookiesDictionary.containsKey(cookieKey)) {
			return cookiesDictionary.get(cookieKey);
		}
		return null;
	}

	public String getCookieValue(String cookieKey) {
		return cookiesOverride.readCookieValue(cookieKey);
	}

	public void appendValueToCookieCollection(String cookieKey, String cookieValue) {
		if (!checkParameters(cookieKey, cookieValue)) {
			return;
		}
		String existingValue = getCookieValue(cookieKey);
		// checks if this value already exists in this collection
		if (existingValue != null && !existingValue.equals(cookieValue)
				&& !existingValue.contains(cookieValue + COOKIES_COLLECTION_SEPARATOR)
				&& !existingValue.contains(COOKIES_COLLECTION_SEPARATOR + cookieValue)) {
			StringBuilder strBuff = new StringBuilder();
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
		cookiesOverride.writeCookieValue(cookieKey, cookieValue, "/", false);
	}

	public void removeValueFromCookieCollection(String cookieKey, String cookieValue) {
		if (!checkParameters(cookieKey, cookieValue)) {
			return;
		}
		String existingValue = getCookieValue(cookieKey);
		String result = null;
		if (existingValue != null && existingValue.lastIndexOf(COOKIES_COLLECTION_SEPARATOR) == -1) {
			result = existingValue.replaceAll(cookieValue, "");
		} else if (existingValue != null && existingValue.lastIndexOf(COOKIES_COLLECTION_SEPARATOR) > existingValue.indexOf(cookieValue)) {
			result = existingValue.replaceAll(cookieValue + COOKIES_COLLECTION_SEPARATOR_REGEXP, "");
		} else if (existingValue != null) {
			result = existingValue.replaceAll(COOKIES_COLLECTION_SEPARATOR_REGEXP + cookieValue, "");
		}
		if(result != null) {
			writeCookieValue(cookieKey, result);
		}
	}

	private boolean checkParameters(String cookieKey, String cookieValue) {
		if (cookieKey == null || cookieValue == null) {
			return false;
		}
		if (cookieKey.equalsIgnoreCase(CourseClass.SHORTLIST_COOKIE_KEY)
				&& (!cookieValue.matches("\\d+") || courseClassService.loadByIds(Long.parseLong(cookieValue)).isEmpty())) {
			return false;
		}
		
		if (cookieKey.equalsIgnoreCase(Product.SHORTLIST_COOKIE_KEY)
				&& (!cookieValue.matches("\\d+") || voucherService.loadByIds(cookieValue).isEmpty())) {
			return false;
		}
		if (cookieKey.equalsIgnoreCase(Discount.PROMOTIONS_KEY)
				&& (!cookieValue.matches("\\d+") || discountService.loadByIds(cookieValue).isEmpty())) {
			return false;
		}
		return true;
	}

	/**
	 * Implementation from @see CookiesImpl#removeCookieValue(java.lang.String),
	 * but the difference is that this method sets root path to the processing
	 * cookie.
	 */
	@Override
	public void removeCookieValue(String name) {
		cookiesOverride.writeCookieValue(name, null, "/", request.isSecure());
	}

	@Override
	public void pushPreviousPagePath(String path) {
		writeCookieValue("prevpage", new String(Base64.encodeBase64(path.getBytes())));
	}

	@Override
	public URL popPreviousPageURL() {

		String prevPage = getCookieValue("prevpage");

		if (prevPage != null && !"".equals(prevPage)) {
			removeCookieValue("prevpage");
			try {
				String path = new String(Base64.decodeBase64(prevPage.getBytes()));
				String schema = request.isSecure() ? "https://" : "http://";
				String url = schema + request.getServerName() + request.getContextPath() + path;
				return new URL(url);
			} catch (MalformedURLException e) {
				LOGGER.warn(e);
			}
		}

		return null;
	}
}
