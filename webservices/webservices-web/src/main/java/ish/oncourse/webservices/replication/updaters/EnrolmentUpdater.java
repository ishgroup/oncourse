package ish.oncourse.webservices.replication.updaters;

import ish.common.types.PaymentSource;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;

public class EnrolmentUpdater extends AbstractWillowUpdater<EnrolmentStub, Enrolment> {

	@Override
	protected void updateEntity(EnrolmentStub stub, Enrolment entity, RelationShipCallback callback) {
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
		entity.setCreated(stub.getCreated());
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class));
		entity.setModified(stub.getModified());
		entity.setReasonForStudy(stub.getReasonForStudy());
		
		if(stub.getSource() != null) {
			entity.setSource(PaymentSource.getSourceForValue(stub.getSource()));
		}
		
		if (stub.getStatus() != null) {
			entity.setStatus(EnrolmentStatus.getEnumForDatabaseValue(stub.getStatus()));
		}
		
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
	}
}
