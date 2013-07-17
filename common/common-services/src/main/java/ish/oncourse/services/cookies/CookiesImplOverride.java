package ish.oncourse.services.cookies;

import org.apache.tapestry5.internal.services.CookieSink;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.util.TimeInterval;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;

import javax.servlet.http.Cookie;

/**
 * Override of default CookiesImpl implementation which will ignore the secure parameter for cookies.
 */
public class CookiesImplOverride implements ICookiesOverride {
	private final Cookies cookies;

	private final CookieSink cookieSink;

	private final Request request;

	private final int defaultMaxAge;

	public CookiesImplOverride(@Symbol("tapestry.default-cookie-max-age") @IntermediateType(TimeInterval.class)
		long defaultMaxAge, Request request, CookieSink cookieSink, Cookies cookies) {

		this.defaultMaxAge = (int) (defaultMaxAge / 1000l);
		this.request = request;
		this.cookieSink = cookieSink;
		this.cookies = cookies;
	}

	@Override
	public String readCookieValue(String name) {
		return cookies.readCookieValue(name);
	}

	@Override
	public void writeCookieValue(String name, String value, String path, boolean isSecure) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(defaultMaxAge);
		cookie.setSecure(isSecure);
		cookieSink.addCookie(cookie);
	}

	@Override
	public void writeCookieValue(String name, String value) {
		writeCookieValue(name, value, defaultMaxAge);
	}

	@Override
	public void writeCookieValue(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(request.getContextPath() + "/");
		cookie.setMaxAge(maxAge);
		cookie.setSecure(false);
		cookieSink.addCookie(cookie);
	}

	@Override
	public void writeCookieValue(String name, String value, String path) {
		writeCookieValue(name, value, path, false);
	}

	@Override
	public void writeDomainCookieValue(String name, String value, String domain) {
		writeDomainCookieValue(name, value, domain, defaultMaxAge);
	}

	@Override
	public void writeDomainCookieValue(String name, String value, String domain, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(request.getContextPath() + "/");
		cookie.setDomain(domain);
		cookie.setMaxAge(maxAge);
		cookie.setSecure(false);
		cookieSink.addCookie(cookie);
	}

	@Override
	public void writeCookieValue(String name, String value, String path, String domain) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(path);
		cookie.setDomain(domain);
		cookie.setMaxAge(defaultMaxAge);
		cookie.setSecure(false);
		cookieSink.addCookie(cookie);
	}

	@Override
	public void removeCookieValue(String name) {
		cookies.removeCookieValue(name);
	}
}
