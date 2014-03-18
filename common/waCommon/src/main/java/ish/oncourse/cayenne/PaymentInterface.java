/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.cayenne;

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

	public static final Map<String, PaymentSource> SOURCES = Maps.asLinkedMap(new String[] {
			PaymentSource.SOURCE_WEB.getDisplayName(),
			PaymentSource.SOURCE_ONCOURSE.getDisplayName() }, new PaymentSource[] { PaymentSource.SOURCE_WEB, PaymentSource.SOURCE_ONCOURSE });

	/**
	 * property mapped to the payer/payee
	 */
	public static final String PERSON_PROPERTY = "contact";
	/**
	 * property mapped to payment type
	 */
	public static final String PAYMENT_TYPE_PROPERTY = "paymentType";

	/**
	 * property mapped to payment type
	 */
	public static final String STATUS_PROPERTY = "status";

	/**
	 * payment in type
	 */
	public static final String TYPE_IN = "payment in";
	/**
	 * payment out type
	 */
	public static final String TYPE_OUT = "payment out";

	/**
	 * handy map of payment types
	 */
	public static final Map<String, String> paymentTypes = Maps.asLinkedMap(new String[] { "In", "Out" }, new String[] { TYPE_IN, TYPE_OUT });
	public static final String CREATED_ON_PROPERTY = "createdOn";
	public static final String SOURCE_PROPERTY = "source";
	public static final String TYPE_PROPERTY = "type";
	public static final String AMOUNT_PROPERTY = "amount";
	public static final String DISPLAYABLE_AMOUNT_PROPERTY = "displayableAmount";
	public static final String DATE_BANKED_PROPERTY = "dateBanked";
	public static final String CC_TRANSACTION_PROP = "cc_trans";
	public static final String ADMINISTRATION_CENTRE_PROPERTY = "administrationCentre";

	/**
	 * @return type of payment as string
	 */
	public String getTypeOfPayment();

	/**
	 * @return type of payment as integer
	 */
	public PaymentType getType();

	/**
	 * @return creation date
	 */
	public Date getCreatedOn();

	/**
	 * @param dateBanked
	 */
	public void setDateBanked(Date dateBanked);

	/**
	 * @return date banked
	 */
	public Date getDateBanked();

	/**
	 * @return reconciled
	 */
	public Boolean getReconciled();

	/**
	 * @param reconciled
	 */
	public void setReconciled(Boolean reconciled);

	public boolean isSuccess();

	public ish.math.Money getAmount();

	public PaymentStatus getStatus();

	public void setPersistenceState(int ps);

	public void removeFromPaymentLines(PaymentLineInterface pLine);

	public void addToPaymentLines(PaymentLineInterface pLine);

	public List<? extends PaymentLineInterface> getPaymentLines();

	public ContactInterface getContact();

}
