package ish.oncourse.webservices.replication.v8.updaters;

import ish.common.types.*;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v8.stubs.replication.EnrolmentStub;
import org.apache.commons.lang.StringUtils;

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
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));

		/**
		 * we need to relate Enrolment and InvoiceLine in this updater to be sure that willow side enrolments is related to InvoiceLine always.
		 */
		InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		if (invoiceLine != null) {
			invoiceLine.setEnrolment(entity);
		} else {
			final String message = String.format("Enrollment with angelId = %s and willowid = %s with missed original invoiceline with id = %s record detected for update! " 
				+ "If this message occured on enrollment instruction call please add invoiceline instruction and retry enrollment instruction.", 
				stub.getAngelId(), stub.getWillowId(), stub.getInvoiceLineId());
			throw new UpdaterException(message);
		}
		entity.setCreditOfferedValue(stub.getCreditOfferedValue());
		entity.setCreditProvider(stub.getCreditProvider());
		entity.setCreditUsedValue(stub.getCreditUsedValue());
		entity.setCreditFOEId(stub.getCreditFoeId());
		if (stub.getCreditType() != null) {
			entity.setCreditType(TypesUtil.getEnumForDatabaseValue(stub.getCreditType(), CreditType.class));
		}
		if (stub.getCreditLevel() != null) {
			entity.setCreditLevel(TypesUtil.getEnumForDatabaseValue(stub.getCreditLevel(), CreditLevel.class));
		}
		if (stub.getCreditProviderType() != null) {
			entity.setCreditProviderType(TypesUtil.getEnumForDatabaseValue(stub.getCreditProviderType(), CreditProviderType.class));
		}
		if (stub.getFeeStatus() != null) {
			entity.setFeeStatus(TypesUtil.getEnumForDatabaseValue(stub.getFeeStatus(), StudentStatusForUnitOfStudy.class));
		}
		if (stub.getCreditTotal() != null) {
			entity.setCreditTotal(TypesUtil.getEnumForDatabaseValue(stub.getCreditTotal(), RecognitionOfPriorLearningIndicator.class));
		}
		if (stub.getFeeHelpStatus() != null) {
			entity.setFeeHelpStatus(TypesUtil.getEnumForDatabaseValue(stub.getFeeHelpStatus(), EnrolmentVETFeeHelpStatus.class));
		}
	}
}
