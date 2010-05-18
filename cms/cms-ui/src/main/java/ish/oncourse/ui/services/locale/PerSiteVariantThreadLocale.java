package ish.oncourse.ui.services.locale;

import static org.apache.tapestry5.ioc.internal.util.Defense.notNull;

import java.util.Locale;

import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.services.ThreadLocale;

import ish.oncourse.services.site.IWebSiteService;

/**
 * A {@link ThreadLocale} implementation that enforces locale variant that is
 * equal to the current web site code. The ensures that per-site templates are
 * correctly chosen for page rendering
 */
@Scope(ScopeConstants.PERTHREAD)
public class PerSiteVariantThreadLocale implements ThreadLocale {

	private Locale locale = Locale.getDefault();

	@Inject
	private IWebSiteService webSiteService;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {

		notNull(locale, "locale");

		if (this.locale != locale) {
			String code = webSiteService.getCurrentSite().getCode();
			this.locale = new Locale(locale.getLanguage(), locale.getCountry(),
					code);
		}
	}
}
