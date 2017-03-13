/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model;

import org.apache.cayenne.ObjectContext;

public interface IPaymentTransaction {
	
	void setSoapResponse(String soapResponse);
	void setResponse(String response);
	void setTxnReference(String txnReference);
	void setIsFinalised(Boolean isFinalised);
	ObjectContext getObjectContext();
}
