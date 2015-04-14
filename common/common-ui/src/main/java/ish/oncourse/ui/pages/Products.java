package ish.oncourse.ui.pages;

import ish.oncourse.linktransform.PageIdentifier;
import ish.oncourse.model.Product;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

public class Products {
	@Inject
	@Property
	private Request request;
	
	static final Logger logger = LogManager.getLogger();
	private static final int START_DEFAULT = 0;
	private static final int ROWS_DEFAULT = 10;
	
	@Inject
	private IVoucherService voucherService;
	
	@Property
	private List<Product> products;
	
	@Property
	private Integer itemIndex;
	
	@SuppressWarnings("all")
	@Property
	private Integer productsCount;
	
	@SuppressWarnings("all")
	@Property
	private Boolean isException;
	
	@Persist("client")
	private List<Long> productsIds;
	
	public boolean isXHR() {
		return request.isXHR();
	}
	
	private static int getIntParam(String s, int def) {
		return (s != null && s.matches("\\d+")) ? Integer.parseInt(s) : def;
	}
	
	Object onActivate() {
		return voucherService.isAbleToPurchaseProductsOnline() ? null : PageIdentifier.PageNotFound.getPageName();
	}
	
	@SetupRender
	public void beforeRender() {
		int start = getIntParam(request.getParameter("start"), START_DEFAULT);
		int rows = getIntParam(request.getParameter("rows"), ROWS_DEFAULT);

		this.itemIndex = start;
		this.isException = false;
		this.products = voucherService.getAvailableProducts(start, rows);
		this.productsCount = voucherService.getProductCount();

		productsIds = new ArrayList<>();
		updateIdsAndIndexes();
	}
	
	private void updateIdsAndIndexes() {
		itemIndex = itemIndex + products.size();
		for (Product product : products) {
			if (!productsIds.contains(product.getId()))
				productsIds.add(product.getId());
		}
	}
	
	public boolean isHasAnyItems() {
		return !products.isEmpty();
	}
}
