package ish.oncourse.model;

import ish.oncourse.model.auto._Product;
import ish.oncourse.utils.QueueableObjectUtils;

public class Product extends _Product implements Queueable {
	private static final long serialVersionUID = 8422903473669633877L;
	
	/**
	 * ordered classes cookie name
	 */
	public static final String SHORTLIST_COOKIE_KEY = "productShortList";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
