/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.TransactionResult2;

public class TransactionResult {
	
	private TransactionResult2 result2;
	private ResultStatus status; 

	public TransactionResult2 getResult2() {
		return result2;
	}

	public void setResult2(TransactionResult2 result) {
		this.result2 = result;
	}

	public ResultStatus getStatus() {
		return status;
	}

	public void setStatus(ResultStatus status) {
		this.status = status;
	}

	public static enum ResultStatus {
		SUCCESS,
		FAILED,
		UNKNOWN
	}
}
