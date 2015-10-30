package ish.oncourse.webservices.replication.v6.builders;

import ish.common.types.PaymentSource;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.PaymentInStub;
import ish.util.CreditCardUtil;

public class PaymentInStubBuilder extends AbstractWillowStubBuilder<PaymentIn, PaymentInStub> {

	@Override
	protected PaymentInStub createFullStub(PaymentIn entity) {
		PaymentInStub stub = new PaymentInStub();
		stub.setAmount(entity.getAmount().toBigDecimal());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setPrivateNotes(entity.getStatusNotes());
		if (entity.getSource() != null) {
			stub.setSource(entity.getSource().getDatabaseValue());
		} else {
			stub.setSource(PaymentSource.SOURCE_WEB.getDatabaseValue());
		}
		stub.setStatus(entity.getStatus().getDatabaseValue());
		stub.setType(entity.getType().getDatabaseValue());
		stub.setGatewayReference(entity.getGatewayReference());
		stub.setGatewayResponse(entity.getGatewayResponse());
		stub.setSessionId(entity.getSessionId());
		stub.setCreditCardExpiry(entity.getCreditCardExpiry());
		stub.setCreditCardName(entity.getCreditCardName());
		String cardNumber = entity.getCreditCardNumber();
		if (cardNumber != null) {
			if (!cardNumber.contains("X")) {
				stub.setCreditCardNumber(CreditCardUtil.obfuscateCCNumber(cardNumber));
			} else {
				stub.setCreditCardNumber(cardNumber);
			}
		}
		if (entity.getCreditCardType() != null) {
			stub.setCreditCardType(entity.getCreditCardType().getDatabaseValue());
		}
		stub.setDateBanked(entity.getDateBanked());
		return stub;
	}
}
