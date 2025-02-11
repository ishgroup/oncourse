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

package ish.oncourse.server.api.dao

import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class PaymentOutDao implements CayenneLayer<PaymentOut>  {

    @Override
    PaymentOut newObject(ObjectContext context) {
        context.newObject(PaymentOut)
    }

    @Override
    PaymentOut getById(ObjectContext context, Long id) {
        SelectById.query(PaymentOut, id)
                .prefetch(PaymentOut.PAYMENT_OUT_LINES.joint())
                .selectOne(context)
    }


    static List<PaymentOut> getReversedFor(PaymentIn paymentIn) {
        ObjectSelect.query(PaymentOut)
                .where(PaymentOut.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.CREDIT_CARD))
                .and(PaymentOut.STATUS.eq(PaymentStatus.SUCCESS))
                .and(PaymentOut.AMOUNT.gt(Money.ZERO()))
                .and(PaymentOut.PAYMENT_IN_GATEWAY_REFERENCE.eq(paymentIn.gatewayReference))
                .select(paymentIn.context)
    }

}
