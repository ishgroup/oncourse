/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.common.types.EntityMapping
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.util.GenericReplicationStub

@CompileStatic
class AngelUpdaterImpl implements IAngelUpdater {

	private Map<String, IAngelUpdater> updaterMap = new HashMap<>()

	AngelUpdaterImpl() {
		updaterMap.put(Attendance.class.getSimpleName(), new AttendanceUpdater())
		updaterMap.put(Course.class.getSimpleName(), new CourseUpdater())
		updaterMap.put(CourseClass.class.getSimpleName(), new CourseClassUpdater())
		updaterMap.put(CourseClassTutor.class.getSimpleName(), new CourseClassTutorUpdater())
		updaterMap.put(Enrolment.class.getSimpleName(), new EnrolmentUpdater())
		updaterMap.put(Discount.class.getSimpleName(), new DiscountUpdater())
		updaterMap.put(WaitingList.class.getSimpleName(), new WaitingListUpdater())
		updaterMap.put(Contact.class.getSimpleName(), new ContactUpdater())
		updaterMap.put(Invoice.class.getSimpleName(), new InvoiceUpdater())
		updaterMap.put(Quote.class.getSimpleName(), new InvoiceUpdater())
		updaterMap.put(InvoiceLine.class.getSimpleName(), new InvoiceLineUpdater())
		updaterMap.put(QuoteLine.class.getSimpleName(), new InvoiceLineUpdater())
		updaterMap.put(InvoiceLineDiscount.class.getSimpleName(), new InvoiceLineDiscountUpdater())
		updaterMap.put(MessagePerson.class.getSimpleName(), new MessagePersonUpdater())
		updaterMap.put(PaymentIn.class.getSimpleName(), new PaymentInUpdater())
		updaterMap.put(PaymentInLine.class.getSimpleName(), new PaymentInLineUpdater())
		updaterMap.put(PaymentOut.class.getSimpleName(), new PaymentOutUpdater())
		updaterMap.put(Preference.class.getSimpleName(), new PreferenceUpdater())
		updaterMap.put(Student.class.getSimpleName(), new StudentUpdater())
		updaterMap.put(TagRelation.class.getSimpleName(), new TagRelationUpdater())
		updaterMap.put(Tutor.class.getSimpleName(), new TutorUpdater())
		updaterMap.put(Room.class.getSimpleName(), new RoomUpdater())
		updaterMap.put(Site.class.getSimpleName(), new SiteUpdater())
		updaterMap.put(ConcessionType.class.getSimpleName(), new ConcessionTypeUpdater())
		updaterMap.put(StudentConcession.class.getSimpleName(), new StudentConcessionUpdater())
		updaterMap.put(Outcome.class.getSimpleName(), new OutcomeUpdater())
		updaterMap.put(VoucherProductCourse.class.getSimpleName(), new VoucherProductCourseUpdater())
		updaterMap.put(Voucher.class.getSimpleName(), new VoucherUpdater())
		updaterMap.put(VoucherProduct.class.getSimpleName(), new VoucherProductUpdater())
		updaterMap.put(VoucherPaymentIn.class.getSimpleName(), new VoucherPaymentInUpdater())
		updaterMap.put(Survey.class.getSimpleName(), new SurveyUpdater())
		updaterMap.put(Membership.class.getSimpleName(), new MembershipUpdater())
		updaterMap.put(MembershipProduct.class.getSimpleName(), new MembershipProductUpdater())
		updaterMap.put(CorporatePass.class.getSimpleName(), new CorporatePassUpdater())
		updaterMap.put(CorporatePassCourseClass.class.getSimpleName(), new CorporatePassCourseClassUpdater())
		updaterMap.put(CorporatePassProduct.class.getSimpleName(), new CorporatePassProductUpdater())
		updaterMap.put(ArticleProduct.class.getSimpleName(), new ArticleProductUpdater())
		updaterMap.put(Article.class.getSimpleName(), new ArticleUpdater())
		updaterMap.put(Document.class.getSimpleName(), new DocumentUpdater())
		updaterMap.put(DocumentVersion.class.getSimpleName(), new DocumentVersionUpdater())
		updaterMap.put(CustomFieldType.class.getSimpleName(), new CustomFieldTypeUpdater())
		updaterMap.put(ContactCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(CourseCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(CourseClassCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(EnrolmentCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(ApplicationCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(WaitingListCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(SurveyCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(ArticleCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(MembershipCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(VoucherCustomField.class.getSimpleName(), new CustomFieldUpdater())
		updaterMap.put(SystemUser.class.getSimpleName(), new SystemUserUpdater())
        updaterMap.put(ContactRelation.class.getSimpleName(), new ContactRelationUpdater())
		updaterMap.put(Application.class.getSimpleName(), new ApplicationUpdater())
		updaterMap.put(EmailTemplate.class.getSimpleName(), new EmailTemplateUpdater())
		updaterMap.put(Script.class.getSimpleName(), new ScriptUpdater())
		updaterMap.put(CourseClassPaymentPlanLine.class.getSimpleName(), new CourseClassPaymentPlanLineUpdater())
		updaterMap.put(InvoiceDueDate.class.getSimpleName(), new InvoiceDueDateUpdater())
		updaterMap.put(CorporatePassDiscount.class.getSimpleName(), new ContactRelationUpdater())
		updaterMap.put(PriorLearning.class.getSimpleName(), new PriorLearningUpdater())
		updaterMap.put(ContactDuplicate.class.getSimpleName(), new ContactDuplicateUpdater())
		updaterMap.put(Certificate.class.getSimpleName(),new CertificateUpdater())
		updaterMap.put(Assessment.class.getSimpleName(), new AssessmentUpdater())
		updaterMap.put(AssessmentClass.class.getSimpleName(), new AssessmentClassUpdater())
		updaterMap.put(AssessmentClassTutor.class.getSimpleName(), new AssessmentClassTutorUpdater())
		updaterMap.put(AssessmentSubmission.class.getSimpleName(), new AssessmentSubmissionUpdater())
		updaterMap.put(CertificateAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(ContactAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(CourseAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(CourseClassAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(EnrolmentAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(InvoiceAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(PriorLearningAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(RoomAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(SessionAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(SiteAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(StudentAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(TagAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(TutorAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(ApplicationAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(AssessmentAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(AssessmentSubmissionAttachmentRelation.class.getSimpleName(), new AttachmentRelationUpdater())
		updaterMap.put(Field.class.getSimpleName(), new FieldUpdater())
		updaterMap.put(FieldHeading.class.getSimpleName(), new FieldHeadingUpdater())
		updaterMap.put(ApplicationFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(EnrolmentFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(WaitingListFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(SurveyFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(PayerFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(ParentFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(ArticleFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(MembershipFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(VoucherFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater())
		updaterMap.put(FieldConfigurationLink.class.getSimpleName(), new FieldConfigurationLinkUpdater())
		updaterMap.put(FieldConfigurationScheme.class.getSimpleName(), new FieldConfigurationSchemeUpdater())
		updaterMap.put(AssessmentClassModule.class.getSimpleName(), new AssessmentClassModuleUpdater())
		updaterMap.put(Checkout.class.getSimpleName(), new CheckoutUpdater())
		updaterMap.put(GradingType.class.getSimpleName(), new GradingTypeUpdater())
		updaterMap.put(GradingItem.class.getSimpleName(), new GradingItemUpdater())
	}

	/**
	 * @see IAngelUpdater#updateEntityFromStub(GenericReplicationStub,
	 *      Queueable, RelationShipCallback)
	 */
	@Override
	void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {

		def key = EntityMapping.getAngelEntityIdentifer(stub.getEntityIdentifier())

		def updater = updaterMap.get(key)

		if (updater == null) {
			throw new UpdaterNotFoundException(String.format("Updater not found for entity with key:%s", key), key)
		}

		updater.updateEntityFromStub(stub, entity, callback)
	}
}
