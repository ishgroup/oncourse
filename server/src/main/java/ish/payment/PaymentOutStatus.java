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

public class PaymentOutStatus implements Serializable {

    private boolean status;
    private String message;
    private boolean canRetry;

    private PaymentOutStatus() {

    }

    public boolean status() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean canRetry() {
        return canRetry;
    }

    public static PaymentOutStatus valueOf(boolean status, String message, boolean canRetry) {
        PaymentOutStatus paymentOutStatus = new PaymentOutStatus();
        paymentOutStatus.status = status;
        paymentOutStatus.message = message;
        paymentOutStatus.canRetry = canRetry;
        return paymentOutStatus;
    }
}
