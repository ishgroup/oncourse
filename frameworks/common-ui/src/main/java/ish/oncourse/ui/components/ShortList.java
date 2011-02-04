package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.CookieUtils;
import java.util.Collections;

import java.util.List;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;

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
	private Cookies cookies;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private List<CourseClass> items;

	@Property
	private CourseClass courseClass;

	@Inject
	private Request request;

	private static final Logger LOGGER = Logger.getLogger(ShortList.class);


	@SetupRender
	void beforeRender() {

		List<Long> classIds = CookieUtils.convertToIds(
				cookies, CourseClass.SHORTLIST_COOKIE_KEY, Long.class);

		if (request.getParameter("addClass") != null) {
			Long id = null;
			try {
				id = Long.parseLong(request.getParameter("addClass"));
				if ((id != null) && !(classIds.contains(id))) {
					classIds.add(id);
				}
			} catch (Exception e) {
				LOGGER.debug("Error converting ID", e);
			}
		} else if (request.getParameter("removeClass") != null) {
			Long id = null;
			try {
				id = Long.parseLong(request.getParameter("removeClass"));
				if ((id != null) && (classIds.contains(id))) {
					classIds.remove(id);
				}
			} catch (Exception e) {
				LOGGER.debug("Error converting ID", e);
			}
		}

		CookieUtils.convertToCookie(cookies, CourseClass.SHORTLIST_COOKIE_KEY, classIds);

		if ((classIds != null) && !(classIds.isEmpty())) {
			items = courseClassService.loadByIds(classIds);
		} else {
			items = Collections.emptyList();
		}
	}

	/**
	 * Obtain the count of items in the shortlist
	 *
	 * @return a count of items
	 */
	public Integer getItemCount() {
		return (items == null) ? 0 : items.size();
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
