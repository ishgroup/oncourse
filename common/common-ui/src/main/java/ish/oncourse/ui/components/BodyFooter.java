package ish.oncourse.ui.components;

import ish.oncourse.services.format.FormatName;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;


public class BodyFooter {

	@Inject
	private IWebSiteService siteService;

	@Inject
	private IFormatService formatService;

	public String getCollegeName() {
		return siteService.getCurrentCollege().getName();
	}

	public String getYear() {
		return formatService.format(new Date(), FormatName.YEAR_4DIGIT);
	}
}
