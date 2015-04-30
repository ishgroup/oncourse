package ish.oncourse.util.payment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInFail {
    private static final Logger logger = LogManager.getLogger();

    private PaymentInModel model;

    private PaymentInFail() {
    }

    public PaymentInFail perform() {
        return this;
    }

    public static PaymentInFail valueOf(PaymentInModel model)
    {
        PaymentInFail paymentInSucceed = new PaymentInFail();
        paymentInSucceed.model = model;
        return paymentInSucceed;
    }
}
