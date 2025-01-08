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

import javax.inject.Inject
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.server.CayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class PaymentInDao implements CayenneLayer<PaymentIn> {
    @Override
    PaymentIn newObject(ObjectContext context) {
        context.newObject(PaymentIn)
    }

    @Override
    PaymentIn getById(ObjectContext context, Long id) {
        SelectById.query(PaymentIn, id)
                .selectOne(context)
    }

    @Inject
    CayenneService cayenneService

    List<Long> getRefundablePaymentIds(ObjectContext context, Contact contact, Money amount) {

        ObjectSelect.query(PaymentIn)
                .where(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.CREDIT_CARD))
                .and(PaymentIn.PAYER.eq(contact))
                .and(PaymentIn.STATUS.eq(PaymentStatus.SUCCESS))
                .and(PaymentIn.BANKING.isNotNull())
                .and(PaymentIn.GATEWAY_REFERENCE.isNotNull())
                .and(PaymentIn.AMOUNT.gte(amount))
                .column(PaymentIn.ID)
                .select(context)
    }

    static String getCreditCardId(Contact contact) {
        PaymentIn payment = getCCquery(contact)
                .selectFirst(contact.context)
        return payment?.billingId
    }

    void removeCChistory(Contact contact) {
        getCCquery(contact)
                .select(contact.context)
                .each { PaymentIn payment ->
                    payment.billingId = null
                }
    }

    private static ObjectSelect<PaymentIn> getCCquery(Contact contact) {
        ObjectSelect.query(PaymentIn)
                .where(PaymentIn.STATUS.eq(PaymentStatus.SUCCESS))
                .and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.CREDIT_CARD))
                .and(PaymentIn.BILLING_ID.isNotNull())
                .and(PaymentIn.PAYER.eq(contact))
                .orderBy(PaymentIn.CREATED_ON.desc())
    }
}
