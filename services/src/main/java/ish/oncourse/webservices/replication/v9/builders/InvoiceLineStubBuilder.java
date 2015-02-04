package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v9.stubs.replication.InvoiceLineStub;

public class InvoiceLineStubBuilder extends AbstractWillowStubBuilder<InvoiceLine, InvoiceLineStub> {

	@Override
	protected InvoiceLineStub createFullStub(InvoiceLine entity) {
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setCreated(entity.getCreated());
		stub.setDescription(entity.getDescription());
		stub.setDiscountEachExTax(entity.getDiscountEachExTax().toBigDecimal());
		Enrolment enrolment = entity.getEnrolment();
		if (enrolment != null){
			stub.setEnrolmentId(enrolment.getId());
		}
		stub.setInvoiceId(entity.getInvoice().getId());
		stub.setModified(entity.getModified());
		stub.setPriceEachExTax(entity.getPriceEachExTax().toBigDecimal());
		stub.setQuantity(entity.getQuantity());
		stub.setTaxEach(entity.getTaxEach().toBigDecimal());
		stub.setTitle(entity.getTitle());
		stub.setUnit(entity.getUnit());
		stub.setSortOrder(entity.getSortOrder());

		CourseClass courseClass = entity.getCourseClass();
		if (courseClass != null) {
			stub.setCourseClassId(courseClass.getId());
		}
		
		return stub;
	}
}
