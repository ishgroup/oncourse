package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentTransaction;

import java.util.Date;

public class PaymentTransaction extends _PaymentTransaction {
	private static final long serialVersionUID = -3879382348217850073L;
	public static final String REFERENCE_ID_PARAM = "ref";
	public static final String APPROVED_RESPONSE = "APPROVED";

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
