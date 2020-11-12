/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
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
