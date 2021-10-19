package ish.oncourse.webservices.replication.v24.updaters;

import ish.math.Money;
import ish.oncourse.model.AbstractInvoiceLine;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v24.stubs.replication.InvoiceLineStub;

public class InvoiceLineUpdater extends AbstractWillowUpdater<InvoiceLineStub, AbstractInvoiceLine> {

	@Override
	public void updateEntity(InvoiceLineStub stub, AbstractInvoiceLine entity, RelationShipCallback callback) {
;		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setDiscountEachExTax(Money.valueOf(stub.getDiscountEachExTax()));
		entity.setInvoice(callback.updateRelationShip(stub.getInvoiceId(), entity.getInvoicePersistentClass()));
		entity.setModified(stub.getModified());
		entity.setPriceEachExTax(Money.valueOf(stub.getPriceEachExTax()));
		entity.setQuantity(stub.getQuantity());
		entity.setTaxEach(Money.valueOf(stub.getTaxEach()));
		entity.setTitle(stub.getTitle());
		entity.setUnit(stub.getUnit());
		entity.setSortOrder(stub.getSortOrder());
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
		if (stub.getEnrolmentId() != null) {
			entity.setEnrolment(callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class));
		}
	}
}
