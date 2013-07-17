package ish.oncourse.services.cookies;

import org.apache.tapestry5.services.Cookies;

public interface ICookiesOverride extends Cookies {
	void writeCookieValue(String name, String value, String path, boolean isSecure);
}
