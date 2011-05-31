package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentTransaction;

import java.util.UUID;

public class PaymentTransaction extends _PaymentTransaction {
	
	public static final String REFERENCE_ID_PARAM = "ref";

	@Override
	protected void onPrePersist() {
		UUID idOne = UUID.randomUUID();
		String referenceId = idOne.toString().replaceAll("-", "");
		setSessionId(referenceId);
	}
}
