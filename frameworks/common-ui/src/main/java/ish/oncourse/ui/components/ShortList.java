package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Based on DynamicCookieView
 * 
 * @author ksenia
 * 
 */
public class ShortList {

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private List<CourseClass> items;

	@SetupRender
	void beforeRender() {
		String[] shortlistedClassIds = cookiesService.getCookieCollectionValue(
				CourseClass.SHORTLIST_COOKIE_KEY);
		if (shortlistedClassIds != null) {
			items = courseClassService.loadByIds(shortlistedClassIds);
		}
	}

	/**
	 * Obtain the count of items in the shortlist
	 *
	 * @return a count of items
	 */
	public Integer getItemCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	public String getSelectedMessage() {
		return "course" + (items == null || items.size() != 1 ? "s" : "") + " selected";
	}

	/**
	 * Test to see if there are any items in shortlist
	 * @return true if shortlist has ANY items
	 */
	public boolean isHasItems() {
		return getItemCount() > 0;
	}

	/**
	 * Test to see if there are multiple items in the shortlist
	 * @return true if the shortlist contains more than one item
	 */
	public boolean isHasMultipleItems() {
		return getItemCount() > 1;
	}

	/**
	 * Checks if the payment gateway processing is enabled for the current
	 * college.
	 *
	 * @return true if payment gateway is enabled.
	 */
	public boolean isPaymentGatewayEnabled() {
		return webSiteService.getCurrentCollege().isPaymentGatewayEnabled();
	}

}
