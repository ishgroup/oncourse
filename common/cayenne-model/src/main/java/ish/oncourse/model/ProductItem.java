package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.common.types.ProductStatus;
import ish.oncourse.cayenne.ICustomField;
import ish.oncourse.model.auto._ProductItem;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;
import java.util.List;

public class ProductItem extends _ProductItem implements Queueable {
	private static final long serialVersionUID = 7320137102951574972L;

	@Override
	public List<? extends ICustomField> getCustomFields() {
		return null;
	}

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public boolean isAsyncReplicationAllowed() {
		return getStatus() != null && getStatus() != ProductStatus.NEW;
	}

    @Override
    protected void onPostAdd() {
		if (getCreated() == null) {
			setCreated(new Date());
		}
		if (getModified() == null) {
			setModified(getCreated());
		}
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
		}
    }

	@Override
	public void setCustomFieldValue(String key, String value) {

	}
}
