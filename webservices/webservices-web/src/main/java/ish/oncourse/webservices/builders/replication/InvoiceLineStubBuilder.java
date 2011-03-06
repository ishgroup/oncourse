package ish.oncourse.webservices.builders.replication;

import java.util.Map;

import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub;

public class InvoiceLineStubBuilder extends AbstractWillowStubBuilder<InvoiceLine, InvoiceLineStub> {

	public InvoiceLineStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}

	@Override
	protected InvoiceLineStub createFullStub(InvoiceLine entity) {
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setWillowId(entity.getId());
		stub.setAngelId(entity.getAngelId());
		stub.setCreated(entity.getCreated());
		stub.setDescription(entity.getDescription());
		stub.setDiscountEachExTax(entity.getDiscountEachExTax().toBigDecimal());
		stub.setEnrolment(findRelationshipStub(entity.getEnrolment()));
		stub.setEntityIdentifier(entity.getObjectId().getEntityName());
		stub.setInvoice(findRelationshipStub(entity.getInvoice()));
		stub.setModified(entity.getModified());
		stub.setPriceEachExTax(entity.getPriceEachExTax().toBigDecimal());
		stub.setQuantity(entity.getQuantity());
		stub.setTaxEach(entity.getTaxEach().toBigDecimal());
		stub.setTitle(entity.getTitle());
		stub.setUnit(entity.getUnit());
		return stub;
	}
}
