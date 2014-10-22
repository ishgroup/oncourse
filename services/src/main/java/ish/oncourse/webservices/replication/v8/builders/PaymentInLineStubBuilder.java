package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.PaymentInLine;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.PaymentInLineStub;

public class PaymentInLineStubBuilder extends AbstractWillowStubBuilder<PaymentInLine, PaymentInLineStub> {

	@Override
	protected PaymentInLineStub createFullStub(PaymentInLine entity) {
		PaymentInLineStub stub = new PaymentInLineStub();
		stub.setAmount(entity.getAmount().toBigDecimal());
		stub.setAngelId(entity.getAngelId());
		stub.setCreated(entity.getCreated());
		stub.setInvoiceId(entity.getInvoice().getId());
		stub.setModified(entity.getModified());
		stub.setPaymentInId(entity.getPaymentIn().getId());
		return stub;
	}
}
