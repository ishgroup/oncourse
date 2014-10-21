package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
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
	private List<Product> productItems;

	@Property
	private CourseClass courseClass;
	
	@Property
	private Product product;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	static final Logger LOGGER = Logger.getLogger(ShortList.class);

	@SetupRender
	void beforeRender() {
		List<Long> classIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		//also load the products
		List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);

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
		
		if (Product.SHORTLIST_COOKIE_KEY.equalsIgnoreCase(key)) {
			String addItem = request.getParameter("addItemId");
			String removeItem = request.getParameter("removeItemId");
			if (addItem != null && addItem.matches("\\d+")) {

				if (!productIds.contains(Long.valueOf(addItem))) {
					productIds.add(Long.valueOf(addItem));
				}

			}
			if (removeItem != null && removeItem.matches("\\d+")) {
				if (productIds.contains(Long.valueOf(removeItem))) {
					productIds.remove(Long.valueOf(removeItem));
				}

			}
		}
		
		if ((classIds != null) && !(classIds.isEmpty())) {
			items = courseClassService.loadByIds(classIds);
		} else {
			items = Collections.emptyList();
		}

		if ((productIds != null) && !(productIds.isEmpty())) {
			productItems = voucherService.loadByIds(productIds);
		} else {
			productItems = Collections.emptyList();
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
	
	public boolean isHasAnyItems() {
		return isHasItems() || isHasProductItems();
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
	public Integer getProductItemCount() {
		return (productItems == null) ? 0 : productItems.size();
	}

	/**
	 * Test to see if there are any items in shortlist
	 * 
	 * @return true if shortlist has ANY items
	 */
	public boolean isHasProductItems() {
		return getProductItemCount() > 0;
	}

	/**
	 * Test to see if there are multiple items in the shortlist
	 * 
	 * @return true if the shortlist contains more than one item
	 */
	public boolean isHasMultipleProductItems() {
		return getProductItemCount() > 1;
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
	
	public Format getProductDateFormat() {
		return FormatUtils.getShortDateFormat(product.getCollege().getTimeZone());
	}

	public Integer getTotatItemsCount(){
		return getItemCount() + getProductItemCount();
	}
}
