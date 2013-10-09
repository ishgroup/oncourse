package ish.oncourse.webservices.replication.v5.updaters;

import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Queueable;
import ish.oncourse.util.CommonUtils;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub;

public class InvoiceLineUpdater extends AbstractWillowUpdater<InvoiceLineStub, InvoiceLine> {

	private static final String INVOICE_LINE_WITH_MISSED_ENROLMENT_MESSAGE = 
		"InvoiceLine with angelId = %s and willowid = %s with missed enrolment id = %s record detected for update! ";

	@Override
	protected void updateEntity(InvoiceLineStub stub, InvoiceLine entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setDiscountEachExTax(Money.valueOf(stub.getDiscountEachExTax()));
		entity.setInvoice(callback.updateRelationShip(stub.getInvoiceId(), Invoice.class));
		entity.setModified(stub.getModified());
		entity.setPriceEachExTax(Money.valueOf(stub.getPriceEachExTax()));
		entity.setQuantity(stub.getQuantity());
		entity.setTaxEach(Money.valueOf(stub.getTaxEach()));
		entity.setTitle(stub.getTitle());
		entity.setUnit(stub.getUnit());
		entity.setSortOrder(stub.getSortOrder());

		// ugly hack to handle InvoiceLine.sortOrder logic without migration to new stubs version
		if (CommonUtils.compare(getCurrentCollegeAngelVersion(entity), CommonUtils.VERSION_5_0) >= 0) {
			entity.setSortOrder(stub.getSortOrder());
		}

		if (stub.getEnrolmentId() != null) {
			if (isSupportOneToManyOnEnrolment_InvoiceLine_Relation(entity)) {
				Enrolment enrolment = callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class);
				if (enrolment != null) {
					entity.setEnrolment(enrolment);
				} else {
					final String message = String.format(INVOICE_LINE_WITH_MISSED_ENROLMENT_MESSAGE 
						+ "If this message occured on invoiceline instruction call please add enrollment instruction and retry enrollment instruction.", 
						stub.getAngelId(), stub.getWillowId(), stub.getEnrolmentId());
					throw new UpdaterException(message);
				}
			} else {
				if (entity.getEnrolment() == null) {
					if (LOG.isDebugEnabled()) {
						final String message = String.format("Invoice line with angelid = %s and willowid  = %s haven't linked yet with enrollment " 
							+ "with angelid = %s but link exist!", stub.getAngelId(), stub.getWillowId(), stub.getEnrolmentId());
						LOG.warn(message);
					}
				}
			}
		}
	}
	
	private boolean isSupportOneToManyOnEnrolment_InvoiceLine_Relation(Queueable entity) {
		return CommonUtils.compare(getCurrentCollegeAngelVersion(entity), Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION) >= 0;
	}
}
