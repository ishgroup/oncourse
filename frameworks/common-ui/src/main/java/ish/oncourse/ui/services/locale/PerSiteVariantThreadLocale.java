package ish.oncourse.ui.services.locale;

import ish.oncourse.services.site.IWebSiteService;

import java.util.Locale;

import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.services.ThreadLocale;

/**
 * A {@link ThreadLocale} implementation that enforces locale variant that is
 * equal to the current web site code. The ensures that per-site templates are
 * correctly chosen for page rendering
 */
@Scope(ScopeConstants.PERTHREAD)
public class PerSiteVariantThreadLocale implements ThreadLocale {

	private Locale locale = Locale.getDefault();

	private IWebSiteService webSiteService;
	
	public PerSiteVariantThreadLocale(IWebSiteService webSiteService) {
		super();
		this.webSiteService = webSiteService;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {

		if (locale == null) {
			throw new IllegalArgumentException("Locale is null");
		}

		if (this.locale != locale) {
			String code = webSiteService.getCurrentWebSite().getSiteKey();
			this.locale = new Locale(locale.getLanguage(), locale.getCountry(),
					code);
		}
	}
}
