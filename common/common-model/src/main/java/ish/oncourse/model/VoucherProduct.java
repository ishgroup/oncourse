package ish.oncourse.model;

import ish.oncourse.model.auto._VoucherProduct;
import ish.oncourse.utils.QueueableObjectUtils;

public class VoucherProduct extends _VoucherProduct implements Queueable {
	private static final long serialVersionUID = 4859536151566879248L;
	
	/**
	 * parameter for add/remove cookies actions
	 */
	public static final String VOUCHER_PRODUCT_ID_PARAMETER = "voucherProductId";
	
	/**
	 * ordered classes cookie name
	 */
	public static final String SHORTLIST_COOKIE_KEY = "voucherProductShortList";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
