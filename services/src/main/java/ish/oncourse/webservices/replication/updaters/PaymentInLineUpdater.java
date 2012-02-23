package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub;

import org.apache.log4j.Logger;

public class PaymentInLineUpdater extends AbstractWillowUpdater<PaymentInLineStub, PaymentInLine> {

	/**
	 * Logger
	 */
	//private static final Logger logger = Logger.getLogger(PaymentInLineUpdater.class);

	@Override
	protected void updateEntity(PaymentInLineStub stub, PaymentInLine entity, RelationShipCallback callback) {
		entity.setAmount(stub.getAmount());
		entity.setCreated(stub.getCreated());

		Invoice invoice = callback.updateRelationShip(stub.getInvoiceId(), Invoice.class);
		if (invoice != null) {
			entity.setInvoice(invoice);
		} else {
            String message =  String.format("Can not find invoice by angelId:%s for paymentInLine with angelId: %s and willowId: %s",
                    stub.getInvoiceId(), stub.getAngelId(), entity.getId());
            throw new UpdaterException(message);
		}

		entity.setModified(stub.getModified());
		PaymentIn paymentIn = callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class);
		if (paymentIn != null) {
			entity.setPaymentIn(paymentIn);
		} else {
            String message =  String.format("Can not find paymentIn by angelId:%s for paymentInLine with angelId: %s and willowId %s",
                    stub.getPaymentInId(), stub.getAngelId(), entity.getId());
            throw new UpdaterException(message);
		}
	}
}
