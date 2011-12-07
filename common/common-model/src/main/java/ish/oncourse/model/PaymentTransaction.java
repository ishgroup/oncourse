package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._PaymentTransaction;

public class PaymentTransaction extends _PaymentTransaction {
	private static final long serialVersionUID = -3879382348217850073L;
	public static final String REFERENCE_ID_PARAM = "ref";

	@Override
	protected void onPreUpdate() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
	}

	@Override
	protected void onPrePersist() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
	}
}
