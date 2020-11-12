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

package ish.oncourse.server.lifecycle;

import ish.common.types.PaymentStatus;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.util.PaymentMethodUtil;
import ish.util.SetBankingMethod;

import static ish.oncourse.server.lifecycle.ChangeFilter.getAtrAttributeChange;

class PaymentHelper {

    static void tryToAssignBankingAutomatically(PaymentInterface payment) {
        //assign only after Payment.status has been changed to SUCCESS
        var change = getAtrAttributeChange(payment.getObjectContext(), payment.getObjectId(), PaymentInterface.STATUS_PROPERTY);

        if (change != null && PaymentStatus.SUCCESS.equals(change.getNewValue())) {
            //validate payment before setting
            if (PaymentMethodUtil.SYSTEM_TYPES.contains(payment.getPaymentMethod().getType())) {
                return;
            }
            if (!payment.getPaymentMethod().getBankedAutomatically()) {
                return;
            }
            if (payment.getBanking() != null) {
                return;
            }
            if (payment instanceof PaymentIn && ((PaymentIn) payment).getReversalOf() != null) {
                return;
            }

            SetBankingMethod.valueOf(payment).set();
        }
    }
}
