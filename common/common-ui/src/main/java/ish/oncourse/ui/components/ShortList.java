package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;
import java.util.Collections;
import java.util.List;

/**
 * Based on DynamicCookieView
 * 
 * @author ksenia
 * 
 */
public class ShortList {

	@Parameter
	@Property
	private boolean expandComponent;

	@SuppressWarnings("unused")
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private PreferenceController preferenceController;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private IVoucherService voucherService;

	@Property
	private List<CourseClass> items;
	
	@Property
	private List<VoucherProduct> voucherProductItems;

	@Property
	private CourseClass courseClass;
	
	@Property
	private VoucherProduct voucherProduct;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	static final Logger LOGGER = Logger.getLogger(ShortList.class);

	@SetupRender
	void beforeRender() {
		List<Long> classIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		//also load the vouchers
		List<Long> voucherProductIds = cookiesService.getCookieCollectionValue(VoucherProduct.SHORTLIST_COOKIE_KEY, Long.class);

		String key = request.getParameter("key");

		if (CourseClass.SHORTLIST_COOKIE_KEY.equalsIgnoreCase(key)) {
			String addItem = request.getParameter("addItemId");
			String removeItem = request.getParameter("removeItemId");
			if (addItem != null && addItem.matches("\\d+")) {

				if (!classIds.contains(Long.valueOf(addItem))) {
					classIds.add(Long.valueOf(addItem));
				}

			}
			if (removeItem != null && removeItem.matches("\\d+")) {
				if (classIds.contains(Long.valueOf(removeItem))) {
					classIds.remove(Long.valueOf(removeItem));
				}

			}
		}
		
		if (VoucherProduct.SHORTLIST_COOKIE_KEY.equalsIgnoreCase(key)) {
			String addItem = request.getParameter("addItemId");
			String removeItem = request.getParameter("removeItemId");
			if (addItem != null && addItem.matches("\\d+")) {

				if (!voucherProductIds.contains(Long.valueOf(addItem))) {
					voucherProductIds.add(Long.valueOf(addItem));
				}

			}
			if (removeItem != null && removeItem.matches("\\d+")) {
				if (voucherProductIds.contains(Long.valueOf(removeItem))) {
					voucherProductIds.remove(Long.valueOf(removeItem));
				}

			}
		}
		
		if ((classIds != null) && !(classIds.isEmpty())) {
			items = courseClassService.loadByIds(classIds);
		} else {
			items = Collections.emptyList();
		}

		if ((voucherProductIds != null) && !(voucherProductIds.isEmpty())) {
			voucherProductItems = voucherService.loadByIds(voucherProductIds.toArray());
		} else {
			voucherProductItems = Collections.emptyList();
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
	 * 
	 * @return true if shortlist has ANY items
	 */
	public boolean isHasItems() {
		return getItemCount() > 0;
	}

	/**
	 * Test to see if there are multiple items in the shortlist
	 * 
	 * @return true if the shortlist contains more than one item
	 */
	public boolean isHasMultipleItems() {
		return getItemCount() > 1;
	}
	
	/**
	 * Obtain the count of items in the shortlist
	 * 
	 * @return a count of items
	 */
	public Integer getVoucherProductItemCount() {
		return (voucherProductItems == null) ? 0 : voucherProductItems.size();
	}

	/**
	 * Test to see if there are any items in shortlist
	 * 
	 * @return true if shortlist has ANY items
	 */
	public boolean isHasVoucherProductItems() {
		return getVoucherProductItemCount() > 0;
	}

	/**
	 * Test to see if there are multiple items in the shortlist
	 * 
	 * @return true if the shortlist contains more than one item
	 */
	public boolean isHasMultipleVoucherProductItems() {
		return getVoucherProductItemCount() > 1;
	}

	/**
	 * Checks if the payment gateway processing is enabled for the current
	 * college.
	 * 
	 * @return true if payment gateway is enabled.
	 */
	public boolean isPaymentGatewayEnabled() {
		return preferenceController.isPaymentGatewayEnabled();
	}

	public String getClassForList() {
		if (expandComponent) {
			return "";
		}
		return messages.get("css.class.list");
	}

	public Format getDateFormat() {
		return FormatUtils.getShortDateFormat(courseClass.getCollege().getTimeZone());
	}
	
	public Format getVoucherProductDateFormat() {
		return FormatUtils.getShortDateFormat(voucherProduct.getCollege().getTimeZone());
	}

}
