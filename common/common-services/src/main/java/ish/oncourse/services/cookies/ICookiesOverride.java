package ish.oncourse.services.cookies;

public interface ICookiesOverride {
	void writeCookieValue(String name, String value, String path, boolean isSecure);

	/**
	 * Returns the value of the first cookie whose name matches. Returns null if no such cookie exists. This method is
	 * only aware of cookies that are part of the incoming request; it does not know about additional cookies added
	 * since then (via {@link #writeCookieValue(String, String)}).
	 */
	String readCookieValue(String name);

	/**
	 * Creates or updates a cookie value. The value is stored using a max age (in seconds) defined by the symbol
	 * <code>org.apache.tapestry5.default-cookie-max-age</code>. The factory default for this value is the equivalent of
	 * one week.
	 */

	void writeCookieValue(String name, String value);

	/**
	 * As with {@link #writeCookieValue(String, String)} but an explicit maximum age may be set.
	 *
	 * @param name   the name of the cookie
	 * @param value  the value to be stored in the cookie
	 * @param maxAge the maximum age, in seconds, to store the cookie
	 */

	void writeCookieValue(String name, String value, int maxAge);

	/**
	 * As with {@link #writeCookieValue(String, String)} but an explicit path may be set.
	 */
	void writeCookieValue(String name, String value, String path);

	/**
	 * As with {@link #writeCookieValue(String, String)} but an explicit domain may be set.
	 */
	void writeDomainCookieValue(String name, String value, String domain);

	/**
	 * As with {@link #writeCookieValue(String, String)} but an explicit domain and maximum age may be set.
	 */
	void writeDomainCookieValue(String name, String value, String domain, int maxAge);

	/**
	 * As with {@link #writeCookieValue(String, String, String)} but an explicit domain and path may be set.
	 */
	void writeCookieValue(String name, String value, String path, String domain);

	/**
	 * Removes a previously written cookie, by writing a new cookie with a maxAge of 0.
	 */
	void removeCookieValue(String name);
}
