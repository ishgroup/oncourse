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

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;

/**
 * @author marcin
 */
public interface AccountInterface extends Persistent {

	public static final String ACCOUNT_CODE_PROPERTY = "accountCode";
	public static final String ID_PROPERTY = "id";

	public boolean isAsset();

	public boolean isIncome();

	public boolean isExpense();

	public boolean isLiability();

	public boolean isCOS();

	public boolean isEquity();

	public ObjectId getObjectId();

	public String getAccountCode();
}
