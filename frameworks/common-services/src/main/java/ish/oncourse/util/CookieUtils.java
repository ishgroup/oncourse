/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.services.Cookies;

/**
 * 
 * @author marek
 */
public class CookieUtils {

	private static final Logger LOGGER = Logger.getLogger(CookieUtils.class);

	public static <T extends Number> List<T> convertToIds(Cookies cookies, String name,
			Class<T> clazz) {
		List<T> ids = new ArrayList<T>();
		String cookieValue = cookies.readCookieValue(name);
		if (cookieValue == null) {
			return ids;
		}
		String[] stringIds = cookieValue.split("[%]");

		if ((stringIds != null) && (stringIds.length > 0)) {
			try {
				for (String id : stringIds) {
					ids.add(convertToNumber(id, clazz));
				}
			} catch (Exception e) {
				LOGGER.error("Exception while converting IDs", e);
			}
		}
		return ids;
	}

	public static <T extends Number> void convertToCookie(Cookies cookies, String name, List<T> ids) {
		String cookieValue = StringUtils.join(ids, "%");

		if ((cookieValue != null) && (cookieValue.length() > 0)) {
			cookies.writeCookieValue(name, cookieValue);
		} else {
			cookies.removeCookieValue(name);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends Number> T convertToNumber(String value, Class<T> clazz) {
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
}
