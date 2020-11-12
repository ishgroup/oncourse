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

package ish.payment;

import java.io.Serializable;

public class PaymentProcessResult implements Serializable {
    private boolean successful = true;
    private boolean sent2Willow = true;

    public boolean isSuccessful() {
        return successful;
    }

    public boolean isSent2Willow() {
        return sent2Willow;
    }

    public static PaymentProcessResult successResult() {
        return new PaymentProcessResult();
    }

    public static PaymentProcessResult failedResult(boolean sent2Willow) {
        PaymentProcessResult result = new PaymentProcessResult();
        result.successful = false;
        result.sent2Willow = sent2Willow;
        return result;
    }
}
