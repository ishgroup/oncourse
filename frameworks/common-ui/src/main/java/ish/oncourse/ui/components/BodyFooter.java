package ish.oncourse.ui.components;

import java.util.Date;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.format.FormatName;
import ish.oncourse.services.format.IFormatService;


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
