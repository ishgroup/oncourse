/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;


import ish.common.types.PaymentType;

public interface PaymentMethodInterface extends PersistentObjectI {

	public static final String TYPE_PROPERTY = "type";
	public static final String NAME_PROPERTY = "name";

	public String getName();

	public Boolean getActive();

	public AccountInterface getAccount();

	public Boolean getBankedAutomatically();
	
	public Boolean getReconcilable();
	
	public PaymentType getType();
	
	public AccountInterface getUndepositedFundsAccount();
}
