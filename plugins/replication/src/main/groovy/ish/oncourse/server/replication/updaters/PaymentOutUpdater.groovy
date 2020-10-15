/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.webservices.v21.stubs.replication.PaymentOutStub
import ish.util.LocalDateUtils
import ish.util.PaymentMethodUtil

import java.time.ZoneId

/**
 */
class PaymentOutUpdater extends AbstractAngelUpdater<PaymentOutStub, PaymentOut> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(PaymentOutStub stub, PaymentOut entity, RelationShipCallback callback) {
		entity.setAmount(new Money(stub.getAmount()))
		def payee = callback.updateRelationShip(stub.getContactId(), Contact.class)
		entity.setPayee(payee)
		entity.setPaymentInGatewayReference(stub.getPaymentInTxnReference())

		if (entity.getId() == null) {
			switch (TypesUtil.getEnumForDatabaseValue(stub.getType(), PaymentType.class)) {
				case CREDIT_CARD:
					SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(entity.getContext(), PaymentMethod.class), entity).set()
					break
				case INTERNAL:
					SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(entity.getContext(), PaymentMethod.class), entity).set()
					break
				case VOUCHER:
					SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(entity.getContext(), PaymentMethod.class), entity).set()
					break
				case REVERSE:
					SetPaymentMethod.valueOf(PaymentMethodUtil.getREVERSPaymentMethods(entity.getContext(), PaymentMethod.class), entity).set()
				default:
					throw new IllegalArgumentException("Unsupported type for web PaymentOut willowID:" + entity.getWillowId())
			}

		} else if (entity.getPaymentMethod() == null) {
			throw new IllegalArgumentException("PaymentMethod is not represented for PaymentOut willowID:" + entity.getWillowId())
		}

		if (stub.getStatus() != null) {
			entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), PaymentStatus.class))
		}

		entity.setPaymentDate(LocalDateUtils.dateToValue(stub.getDatePaid()))
		if (stub.getConfirmationStatus() != null) {
			entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class))
		}
		if (entity.getPaymentDate() == null) {
			entity.setPaymentDate(stub.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
		}
	}
}
