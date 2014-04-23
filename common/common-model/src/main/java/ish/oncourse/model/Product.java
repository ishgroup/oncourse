package ish.oncourse.model;

import ish.math.Money;
import ish.oncourse.model.auto._Product;
import ish.oncourse.utils.QueueableObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

public class Product extends _Product implements Queueable {
	private static final long serialVersionUID = 8422903473669633877L;
	
	/**
	 * ordered classes cookie name
	 */
	public static final String SHORTLIST_COOKIE_KEY = "productShortList";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public Money getPriceIncTax() {
		if (getPriceExTax() != null)
			return getPriceExTax().add(getTaxAmount());
		else
			return Money.ZERO;
	}

	public BigDecimal getTaxRate() {

		if (getPriceExTax() == null || getPriceExTax().isZero())
			return BigDecimal.ZERO;
		else
			return getTaxAmount().divide(getPriceExTax()).toBigDecimal();
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}

    @Override
    protected void onPostAdd() {
		if (getCreated() == null) {
			setCreated(new Date());
		}
		if (getModified() == null) {
			setModified(getCreated());
		}
    }
}
