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

		entity.setModified(stub.getModified());
		entity.setReasonForStudy(stub.getReasonForStudy());

		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class));

        if (StringUtils.trimToNull(stub.getStatus()) == null) {
            String message = String.format("Enrolment with angelId = %s with empty status detected!", stub.getAngelId());
            throw new UpdaterException(message);
        }
        entity.setStatus(EnrolmentStatus.valueOf(stub.getStatus()));
		Student student = callback.updateRelationShip(stub.getStudentId(), Student.class);
		entity.setStudent(student);
	}

}
