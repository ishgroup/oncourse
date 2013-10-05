package ish.oncourse.ui.components.internal;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.datalayer.DataLayerFactory;
import ish.oncourse.services.datalayer.IDataLayerFactory;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class is tapestry component class for DataLayer.tml template which creates dataLayer javascript object
 * for Google TagManager functionality
 */
public class DataLayer {

	@Inject
	private Request request;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IVoucherService voucherService;

	@Inject
	private ITagService tagService;

	@Inject
	private IDataLayerFactory dataLayerFactory;

	@Property
	@Parameter
	private DataLayerFactory.Cart cart;

	@Property
	private DataLayerFactory.Product product;

	private List<CourseClass> classes;
	private List<Product> products;

	@Property
	private NumberFormat moneyFormat;

	/**
	 * Google tag mananger event name.
	 */
	@Parameter
	@Property
	private String eventName;

	@SetupRender
	void beforeRender() {

		moneyFormat = NumberFormat.getInstance();
		moneyFormat.setMinimumIntegerDigits(1);
		moneyFormat.setMinimumFractionDigits(2);
		moneyFormat.setMaximumFractionDigits(2);
		moneyFormat.setGroupingUsed(false);

		initItems();

		if (cart == null)
		{
			if (classes.size() > 0 || products.size() > 0)
			{
				ArrayList values = new ArrayList(classes.size() + products.size());
				values.addAll(classes);
				values.addAll(products);
				cart = dataLayerFactory.build(values);
			}
		}
	}

	private void initItems() {
		List<Long> classIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);

		if ((classIds != null) && !(classIds.isEmpty())) {
			classes = courseClassService.loadByIds(classIds);
		} else {
			classes = Collections.emptyList();
		}


		if ((productIds != null) && !(productIds.isEmpty())) {
			products = voucherService.loadByIds(productIds);
		} else {
			products = Collections.emptyList();
		}
	}
}
