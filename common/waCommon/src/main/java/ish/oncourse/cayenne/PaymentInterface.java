/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.cayenne;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.print.PrintableObject;
import ish.util.Maps;

import java.util.Date;
import java.util.List;
import java.util.Map;

// FIXME: note there's another status not mentioned here: null. If I (lachlan) understand correctly it is the same as NEW. It will make things simpler if it can
// be eliminated and the status attribute in the entity could have a non-null constraint.

// TODO: I (lachlan) suggest converting the varying groups of constants into individual enums classes.

/**
 * Interface to host both payment in and out, for both client and server side.
 *
 * @author marcin, ldeck
 */
public interface PaymentInterface extends PrintableObject, PersistentObjectI {

	Map<String, PaymentSource> SOURCES = Maps.asLinkedMap(new String[] {
			PaymentSource.SOURCE_WEB.getDisplayName(),
			PaymentSource.SOURCE_ONCOURSE.getDisplayName() }, new PaymentSource[] { PaymentSource.SOURCE_WEB, PaymentSource.SOURCE_ONCOURSE });

	/**
	 * property mapped to the payer/payee
	 */
	String PERSON_PROPERTY = "contact";
	/**
	 * property mapped to payment type
	 */
	String PAYMENT_TYPE_PROPERTY = "paymentType";

	/**
	 * property mapped to payment type
	 */
	String STATUS_PROPERTY = "status";

	/**
	 * payment in type
	 */
	String TYPE_IN = "payment in";
	/**
	 * payment out type
	 */
	String TYPE_OUT = "payment out";

	/**
	 * handy map of payment types
	 */
	Map<String, String> paymentTypes = Maps.asLinkedMap(new String[] { "In", "Out" }, new String[] { TYPE_IN, TYPE_OUT });
	String CREATED_ON_PROPERTY = "createdOn";
	String SOURCE_PROPERTY = "source";
	String METHOD_PROPERTY = "method";
	String AMOUNT_PROPERTY = "amount";
	String DISPLAYABLE_AMOUNT_PROPERTY = "displayableAmount";
	String DATE_BANKED_PROPERTY = "dateBanked";
	String CC_TRANSACTION_PROP = "cc_trans";
	String ADMINISTRATION_CENTRE_PROPERTY = "administrationCentre";

	/**
	 * @return type of payment as string
	 */
	String getTypeOfPayment();

	/**
	 * @return method of payment 
	 */
	PaymentMethodInterface getPaymentMethod();

	void setPaymentMethod(PaymentMethodInterface method);

	/**
	 * @return creation date
	 */
	Date getCreatedOn();

	/**
	 * @return reconciled
	 */
	Boolean getReconciled();

	/**
	 * @param reconciled
	 */
	void setReconciled(Boolean reconciled);

	boolean isSuccess();

	ish.math.Money getAmount();

	PaymentStatus getStatus();

	void setPersistenceState(int ps);

	void removeFromPaymentLines(PaymentLineInterface pLine);

	void addToPaymentLines(PaymentLineInterface pLine);

	List<? extends PaymentLineInterface> getPaymentLines();

	ContactInterface getContact();

	void setAccount(AccountInterface account);

	BankingInterface getBanking();

	void setBanking(BankingInterface banking);
	
	void setUndepositedFundsAccount(AccountInterface account);
	
	CreditCardType getCreditCardType();

	String getStatusString();
}
