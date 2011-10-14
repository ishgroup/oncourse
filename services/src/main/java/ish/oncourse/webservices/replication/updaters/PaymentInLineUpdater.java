package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub;

public class PaymentInLineUpdater extends AbstractWillowUpdater<PaymentInLineStub, PaymentInLine> {

	@Override
	protected void updateEntity(PaymentInLineStub stub, PaymentInLine entity, RelationShipCallback callback) {
		entity.setAmount(stub.getAmount());
		entity.setCreated(stub.getCreated());
		Invoice invoice = callback.updateRelationShip(stub.getInvoiceId(), Invoice.class);
		entity.setInvoice(invoice);
		
		entity.setModified(stub.getModified());
		entity.setPaymentIn(callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class));
	}
}
