/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
