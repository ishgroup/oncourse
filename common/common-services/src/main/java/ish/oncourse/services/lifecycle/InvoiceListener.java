package ish.oncourse.services.lifecycle;

import ish.oncourse.model.Invoice;
import ish.oncourse.services.persistence.ISHObjectContext;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;

/**
 * updateAmountOwing for Invoice was moved to the listener to exclude recalculating amoutOwing when we get the invoice from angel.
 * TODO: It is temporary solution and it should be removed after all our willow logic will be independent from automatic amountOwing update.
 */
public class InvoiceListener {

	@PrePersist(value = Invoice.class)
	public void prePersist(Invoice invoice) {
		updateAmoutOwing(invoice);

	}

	@PreUpdate(value = Invoice.class)
	public void preUpdate(Invoice invoice) {
		updateAmoutOwing(invoice);
	}


	private void updateAmoutOwing(Invoice invoice) {
		ISHObjectContext context = (ISHObjectContext) invoice.getObjectContext();
		if (context.getIsRecordQueueingEnabled())
			invoice.updateAmountOwing();
	}

}
