/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;


public interface PaymentMethodInterface extends PersistentObjectI {

	public static final String REAL_TIME_CREDIT_CARD_PROPERTY = "realTimeCreditCard";
	public static final String NAME_PROPERTY = "name";

	public String getName();

	public Boolean getRealTimeCreditCard();

	public Boolean getActive();

	public AccountInterface getAccount();

	public Boolean getBankedAutomatically();

	public Boolean getSystem();
	
	public Boolean getReconcilable();
}
