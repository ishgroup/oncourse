package ish.oncourse.util.payment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInSucceed {
    private static final Logger logger = LogManager.getLogger();

    private PaymentInModel model;

    private PaymentInSucceed() {
    }

    public PaymentInSucceed perform() {
        return this;
    }

    public static PaymentInSucceed valueOf(PaymentInModel model)
    {
        PaymentInSucceed paymentInSucceed = new PaymentInSucceed();
        paymentInSucceed.model = model;
        return paymentInSucceed;
    }
}
