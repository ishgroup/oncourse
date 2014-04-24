package ish.oncourse.model;

import ish.oncourse.model.auto._VoucherPaymentIn;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.PersistenceState;

import java.util.Date;

public class VoucherPaymentIn extends _VoucherPaymentIn implements Queueable {
	private static final long serialVersionUID = -8979854047939163675L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return getPersistenceState() ==  PersistenceState.DELETED || getPayment().isAsyncReplicationAllowed();
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
