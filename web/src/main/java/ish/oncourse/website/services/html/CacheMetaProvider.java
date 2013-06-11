package ish.oncourse.website.services.html;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.html.ICacheMetaProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CacheMetaProvider implements ICacheMetaProvider {

	@Inject
	private ICookiesService cookiesService;

	@Override
	public String getMetaContent() {

		String value = StringUtils.trimToNull(cookiesService.getCookieValue(Discount.PROMOTIONS_KEY));
		if (value != null)
			return "no-cache";
		value = StringUtils.trimToNull(cookiesService.getCookieValue(CourseClass.SHORTLIST_COOKIE_KEY));
		if (value != null)
			return "no-cache";
		return "public";
	}
}
