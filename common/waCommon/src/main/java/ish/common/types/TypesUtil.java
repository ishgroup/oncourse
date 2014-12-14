package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import org.apache.cayenne.ExtendedEnumeration;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * utilities to use with DisplayableExtendedEnumeration
 */
public final class TypesUtil {

	private TypesUtil() {}

	/**
	 * assembles a map of displayString/databaseValue pairs for any given DisplayableExtendedEnumeration
	 * 
	 * @param e enum class
	 * @return Map of map of displayString/databaseValue pairs
	 */
	public static <T extends DisplayableExtendedEnumeration> Map<String, T> getValuesAsMap(Class<T> e) {
		if (e == null) {
			throw new IllegalArgumentException("enum parameter cannot be null");
		} else if (!e.isEnum()) {
			throw new IllegalArgumentException(e.getSimpleName() + " must be an enum");
		}

		Map<String, T> map = new LinkedHashMap<>();
		for (T t : e.getEnumConstants()) {
			map.put(t.getDisplayName(), t);
		}
		return map;
	}

	/**
	 * returns the ExtendedEnumeration for a given database value
	 * 
	 * @param aValue database value too look up
	 * @param e enum class
	 * @return enum related to the database value
	 */
	public static <T extends ExtendedEnumeration> T getEnumForDatabaseValue(Object aValue, Class<T> e) {
		if (e == null) {
			throw new IllegalArgumentException("enum parameter cannot be null");
		} else if (!e.isEnum()) {
			throw new IllegalArgumentException(e.getSimpleName() + " must be an enum");
		} else if (aValue == null) {
			return null;
		}

		Class<? extends Object> c = e.getEnumConstants()[0].getDatabaseValue().getClass();
		Object val = null;

		// database value can be String or Integer
		// but to allow lookup on different types of fields a conversion might be required
		if (c == String.class) {
			if (Number.class.isAssignableFrom(aValue.getClass())) {
				val = aValue.toString();
			} else if (String.class.isAssignableFrom(aValue.getClass())) {
				// no conversion needed
				val = aValue;
			} else {
				throw new IllegalArgumentException("cannot lookup enum with db value type " + c.getSimpleName() + " for argument " +
						aValue.getClass().getSimpleName());
			}
		} else if (c == Integer.class) {
			if (String.class.isAssignableFrom(aValue.getClass())) {
				val = Integer.valueOf((String) aValue);
			} else if (BigInteger.class.isAssignableFrom(aValue.getClass())) {
				val = ((BigInteger) aValue).intValue();
			} else if (Long.class.isAssignableFrom(aValue.getClass())) {
				val = ((Long) aValue).intValue();
			} else if (Integer.class.isAssignableFrom(aValue.getClass())) {
				// no conversion needed
				val = aValue;
			} else {
				throw new IllegalArgumentException("cannot lookup enum with db value type " + c.getSimpleName() + " for argument " +
						aValue.getClass().getSimpleName());
			}
		}

		for (T t : e.getEnumConstants()) {
			if (t.getDatabaseValue().equals(val)) {
				return t;
			}
		}

		return null;
	}

	/**
	 * returns the DisplayableExtendedEnumeration for a given display value
	 * 
	 * @param aValue display value too look up
	 * @param e enum class
	 * @return enum related to the display value
	 */
	public static <T extends DisplayableExtendedEnumeration> T getEnumForDisplayName(String displayName, Class<T> e) {
		if (e == null) {
			throw new IllegalArgumentException("enum parameter cannot be null");
		} else if (!e.isEnum()) {
			throw new IllegalArgumentException(e.getSimpleName() + " must be an enum");
		} else if (displayName == null) {
			return null;
		}

		for (T t : e.getEnumConstants()) {
			if (t.getDisplayName().equals(displayName)) {
				return t;
			}
		}
		return null;
	}
}
