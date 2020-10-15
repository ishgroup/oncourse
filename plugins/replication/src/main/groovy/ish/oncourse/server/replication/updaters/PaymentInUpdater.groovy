/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.common.types.*
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.webservices.v21.stubs.replication.PaymentInStub
import ish.util.LocalDateUtils
import ish.util.PaymentMethodUtil
import ish.util.SetBankingMethod

import java.time.ZoneId

/**
 */
class PaymentInUpdater extends AbstractAngelUpdater<PaymentInStub, PaymentIn> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(PaymentInStub stub, PaymentIn entity, RelationShipCallback callback) {
		entity.setAmount(new Money(stub.getAmount()))
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setPayer(callback.updateRelationShip(stub.getContactId(), Contact.class))
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
					break
				default:
					throw new IllegalArgumentException("Unsupported type for web PaymentIn willowID:" + entity.getWillowId())
			}

		} else if (entity.getPaymentMethod() == null) {
			throw new IllegalArgumentException("PaymentMethod is not represented for PaymentIn willowID:" + entity.getWillowId())
		}

		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), PaymentStatus.class))
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class))
		entity.setGatewayReference(stub.getGatewayReference())
		entity.setGatewayResponse(stub.getGatewayResponse())
		entity.setSessionId(stub.getSessionId())
		entity.setCreditCardExpiry(stub.getCreditCardExpiry())
		entity.setCreditCardName(stub.getCreditCardName())
		entity.setCreditCardNumber(stub.getCreditCardNumber())
		if (stub.getCreditCardType() != null) {
			entity.setCreditCardType(TypesUtil.getEnumForDatabaseValue(stub.getCreditCardType(), CreditCardType.class))
		}

		entity.setPrivateNotes(stub.getPrivateNotes())
		if (stub.getConfirmationStatus() != null) {
			entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class))
		}
		entity.setBillingId(stub.getBillingId())

		if (entity.getPaymentDate() == null) {
			entity.setPaymentDate(stub.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
		}

		if (entity.getBanking() == null && PaymentStatus.SUCCESS.equals(entity.getStatus()) && stub.getDateBanked() != null) {
			SetBankingMethod.valueOf(entity,  LocalDateUtils.dateToValue(stub.getDateBanked())).set()
		}
	}
}
