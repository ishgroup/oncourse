package ish.oncourse.webservices.replication.builders;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;

import ish.oncourse.model.*;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {

	private Map<String, IWillowStubBuilder> builderMap = new HashMap<String, IWillowStubBuilder>();

	public WillowStubBuilderImpl() {
		builderMap.put(getEntityName(Attendance.class), new AttendanceStubBuilder());
		builderMap.put(getEntityName(BinaryData.class), new BinaryDataStubBuilder());
		builderMap.put(getEntityName(BinaryInfo.class), new BinaryInfoStubBuilder());
		builderMap.put(getEntityName(ConcessionType.class), new ConcessionTypeStubBuilder());
		builderMap.put(getEntityName(CourseClass.class), new CourseClassStubBuilder());
		builderMap.put(getEntityName(Contact.class), new ContactStubBuilder());
		builderMap.put(getEntityName(Enrolment.class), new EnrolmentStubBuilder());
		builderMap.put(getEntityName(Discount.class), new DiscountStubBuilder());
		builderMap.put(getEntityName(Invoice.class), new InvoiceStubBuilder());
		builderMap.put(getEntityName(InvoiceLine.class), new InvoiceLineStubBuilder());
		builderMap.put(getEntityName(InvoiceLineDiscount.class), new InvoiceLineDiscountStubBuilder());
		builderMap.put(getEntityName(MessagePerson.class), new MessagePersonStubBuilder());
		builderMap.put(getEntityName(PaymentInLine.class), new PaymentInLineStubBuilder());
		builderMap.put(getEntityName(PaymentIn.class), new PaymentInStubBuilder());
		builderMap.put(getEntityName(PaymentOut.class), new PaymentOutStubBuilder());
		builderMap.put(getEntityName(Preference.class), new PreferenceStubBuilder());
		builderMap.put(getEntityName(StudentConcession.class), new StudentConcessionStubBuilder());
		builderMap.put(getEntityName(Student.class), new StudentStubBuilder());
		builderMap.put(getEntityName(Tutor.class), new TutorStubBuilder());
		builderMap.put(getEntityName(TutorRole.class), new TutorRoleStubBuilder());
		builderMap.put(getEntityName(Site.class), new SiteStubBuilder());
		builderMap.put(getEntityName(Course.class), new CourseStubBuilder());
		builderMap.put(getEntityName(Room.class), new RoomStubBuilder());
		builderMap.put(getEntityName(WaitingList.class), new WaitingListStubBuilder());
		builderMap.put(getEntityName(Taggable.class), new TaggableStubBuilder());
		builderMap.put(getEntityName(Outcome.class), new OutcomeStubBuilder());
        builderMap.put(getEntityName(Certificate.class), new CertificateStubBuilder());
        builderMap.put(getEntityName(CertificateOutcome.class), new CertificateOutcomeStubBuilder());
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ish.oncourse.webservices.replication.builders.IWillowStubBuilder#convert
	 * (ish.oncourse.model.QueuedRecord)
	 */
	@Override
	public ReplicationStub convert(QueuedRecord entity) {
		String key = entity.getEntityIdentifier();

		IWillowStubBuilder builder = builderMap.get(key);

		if (builder == null) {
			throw new BuilderNotFoundException(String.format("Builder not found during record conversion: %s.", key),
					key);
		}

		return builder.convert(entity);
	}

	@Override
	public ReplicationStub convert(Queueable entity) {
		String key = entity.getObjectId().getEntityName();

		IWillowStubBuilder builder = builderMap.get(key);

		if (builder == null) {
			throw new BuilderNotFoundException(String.format("Builder not found during record conversion: %s.", key),
					key);
		}

		return builder.convert(entity);
	}
}
