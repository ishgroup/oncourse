package ish.oncourse.model;

import ish.oncourse.model.auto._ProductItem;
import ish.oncourse.utils.QueueableObjectUtils;

public class ProductItem extends _ProductItem implements Queueable {
	private static final long serialVersionUID = 7320137102951574972L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
