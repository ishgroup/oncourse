package ish.oncourse.ui.components.internal;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.datalayer.ShoppingCartDataBuilder;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Collections;
import java.util.List;

public class DigitalData {

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
	private ITextileConverter textileConverter;

	@Inject
	private IPlainTextExtractor plainTextExtractor;

	@Property
	private ShoppingCartDataBuilder.Cart cart;

	@Property
	private ShoppingCartDataBuilder.Product product;

	private List<CourseClass> classes;
	private List<Product> products;

	@SetupRender
	void beforeRender() {
		initItems();

		if (classes.size() > 0)
		{
			ShoppingCartDataBuilder cartDataBuilder = new ShoppingCartDataBuilder();
			cartDataBuilder.setRequest(request);
			cartDataBuilder.setTagService(tagService);
			cartDataBuilder.setPlainTextExtractor(plainTextExtractor);
			cartDataBuilder.setTextileConverter(textileConverter);

			cartDataBuilder.setCourseClasses(classes);
			cartDataBuilder.build();
			cart = cartDataBuilder.getCart();
		}
		//todo export for vouchers
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
