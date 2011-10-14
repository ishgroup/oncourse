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

		if (stub.getSource() != null) {
			entity.setSource(PaymentSource.getSourceForValue(stub.getSource()));
		}

		if (stub.getStatus() != null) {
			ish.common.types.EnrolmentStatus stubStatus = ish.common.types.EnrolmentStatus.valueOf(stub.getStatus());
			EnrolmentStatus entityStatus = null;
			switch (stubStatus) {
			case CANCELLED:
				entityStatus = EnrolmentStatus.CANCELLED;
				break;
			case CORRUPTED:
				entityStatus = null;
				break;
			case FAILED:
			case FAILED_CARD_DECLINED:
			case FAILED_NO_PLACES:
				entityStatus = EnrolmentStatus.FAILED;
				break;
			case IN_TRANSACTION:
				entityStatus = EnrolmentStatus.IN_TRANSACTION;
				break;
			case NEW:
			case QUEUED:
				entityStatus = EnrolmentStatus.PENDING;
				break;
			case REFUNDED:
				entityStatus = EnrolmentStatus.REFUNDED;
				break;
			case SUCCESS:
				entityStatus = EnrolmentStatus.SUCCESS;
				break;
			}
			entity.setStatus(entityStatus);
		}
		
		Student student = callback.updateRelationShip(stub.getStudentId(), Student.class);
		entity.setStudent(student);
	}
}
