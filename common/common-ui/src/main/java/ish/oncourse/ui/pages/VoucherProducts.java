package ish.oncourse.ui.pages;

import java.util.ArrayList;
import java.util.List;

import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.voucher.IVoucherService;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class VoucherProducts {
	@Inject
	@Property
	private Request request;
	
	static final Logger LOGGER = Logger.getLogger(VoucherProducts.class);
	private static final int START_DEFAULT = 0;
	private static final int ROWS_DEFAULT = 10;
	
	@Inject
	private IVoucherService voucherService;
	
	@Property
	private List<VoucherProduct> voucherProducts;
	
	@Property
	private Integer itemIndex;
	
	@SuppressWarnings("all")
	@Property
	private Integer voucherProductsCount;
	
	@SuppressWarnings("all")
	@Property
	private Boolean isException;
	
	@Persist("client")
	private List<Long> voucherProductsIds;
	
	public boolean isXHR() {
		return request.isXHR();
	}
	
	private static int getIntParam(String s, int def) {
		return (s != null && s.matches("\\d+")) ? Integer.parseInt(s) : def;
	}
	
	@SetupRender
	public void beforeRender() {
		int start = getIntParam(request.getParameter("start"), START_DEFAULT);
		int rows = getIntParam(request.getParameter("rows"), ROWS_DEFAULT);

		this.itemIndex = start;
		this.isException = false;
		this.voucherProducts = voucherService.getAvailableVoucherProducts(start, rows);
		this.voucherProductsCount = voucherService.getAvailableVoucherProducts().size();

		voucherProductsIds = new ArrayList<Long>();
		updateIdsAndIndexes();
	}
	
	private void updateIdsAndIndexes() {
		itemIndex = itemIndex + voucherProducts.size();
		for (VoucherProduct product : voucherProducts) {
			if (!voucherProductsIds.contains(product.getId()))
				voucherProductsIds.add(product.getId());
		}
	}
	
	public boolean isHasAnyItems() {
		return !voucherProducts.isEmpty();
	}
}
