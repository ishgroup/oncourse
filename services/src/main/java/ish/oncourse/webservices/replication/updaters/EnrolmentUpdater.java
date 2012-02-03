package ish.oncourse.webservices.replication.updaters;

import org.apache.commons.lang.StringUtils;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
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

		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class));
		String statusString = stub.getStatus();
		if (StringUtils.trimToNull(statusString) != null) {
			entity.setStatus(EnrolmentStatus.valueOf(statusString));
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.warn("Enrolment with id = " + stub.getAngelId() + " with empty status detected!", 
					new Throwable("Enrolment with id = " + stub.getAngelId() + " with empty status detected!"));
			}
		}		
		Student student = callback.updateRelationShip(stub.getStudentId(), Student.class);
		entity.setStudent(student);
	}
}
