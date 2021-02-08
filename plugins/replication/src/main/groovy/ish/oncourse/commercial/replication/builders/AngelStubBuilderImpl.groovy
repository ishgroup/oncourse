/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.*
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.webservices.util.GenericReplicationStub

import java.util.HashMap
import java.util.Map

class AngelStubBuilderImpl implements IAngelStubBuilder {
	private static final String BUILDER_NOT_FOUND_DURING_RECORD_CONVERSION_MESSAGE = "Builder not found during record conversion: %s"
	private Map<String, IAngelStubBuilder> builderMap = new HashMap<>()

	AngelStubBuilderImpl() {
		def relStubBuilder = new AttachmentRelationStubBuilder()
		builderMap.put(CertificateAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(ContactAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(CourseAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(CourseClassAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(DiscountConcessionType.class.getSimpleName(), new DiscountConcessionTypeStubBuilder())
		builderMap.put(DiscountCourseClass.class.getSimpleName(), new DiscountCourseClassStubBuilder())
		builderMap.put(EnrolmentAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(InvoiceAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(PriorLearningAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(RoomAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(SessionAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(SiteAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(StudentAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(TagAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(TutorAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(ApplicationAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(AssessmentAttachmentRelation.class.getSimpleName(), relStubBuilder)
		builderMap.put(CourseModule.class.getSimpleName(), new CourseModuleStubBuilder())
		builderMap.put(PaymentIn.class.getSimpleName(), new PaymentInStubBuilder())
		builderMap.put(PaymentInLine.class.getSimpleName(), new PaymentInLineStubBuilder())
		builderMap.put(PaymentOut.class.getSimpleName(), new PaymentOutStubBuilder())
		builderMap.put(Preference.class.getSimpleName(), new PreferenceStubBuilder())
		builderMap.put(ConcessionType.class.getSimpleName(), new ConcessionTypeStubBuilder())
		builderMap.put(Site.class.getSimpleName(), new SiteStubBuilder())
		builderMap.put(Room.class.getSimpleName(), new RoomStubBuilder())
		builderMap.put(Discount.class.getSimpleName(), new DiscountStubBuilder())
		builderMap.put(Attendance.class.getSimpleName(), new AttendanceStubBuilder())
		builderMap.put(Certificate.class.getSimpleName(), new CertificateStubBuilder())
		builderMap.put(CertificateOutcome.class.getSimpleName(), new CertificateOutcomeStubBuilder())
		builderMap.put(Contact.class.getSimpleName(), new ContactStubBuilder())
		builderMap.put(Course.class.getSimpleName(), new CourseStubBuilder())
		builderMap.put(CourseClass.class.getSimpleName(), new CourseClassStubBuilder())
		builderMap.put(CourseClassTutor.class.getSimpleName(), new CourseClassTutorStubBuilder())
		builderMap.put(Enrolment.class.getSimpleName(), new EnrolmentStubBuilder())
		builderMap.put(Invoice.class.getSimpleName(), new InvoiceStubBuilder())
		builderMap.put(SaleOrder.class.getSimpleName(), new InvoiceStubBuilder())
		builderMap.put(InvoiceLine.class.getSimpleName(), new InvoiceLineStubBuilder())
		builderMap.put(InvoiceLineDiscount.class.getSimpleName(), new InvoiceLineDiscountStubBuilder())
		builderMap.put(Outcome.class.getSimpleName(), new OutcomeStubBuilder())
		builderMap.put(TutorAttendance.class.getSimpleName(), new TutorAttendanceStubBuilder())
		builderMap.put(Student.class.getSimpleName(), new StudentStubBuilder())
		builderMap.put(Tag.class.getSimpleName(), new TagStubBuilder())
		builderMap.put(TagRequirement.class.getSimpleName(), new TagRequirementStubBuilder())
		builderMap.put(Message.class.getSimpleName(), new MessageStubBuilder())
		builderMap.put(MessagePerson.class.getSimpleName(), new MessagePersonStubBuilder())
		builderMap.put(MessageTemplate.class.getSimpleName(), new MessageTemplateStubBuilder())
		builderMap.put(ContactTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(CourseClassTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(CourseTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(ReportTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(SiteTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(WaitingListTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(RoomTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(TagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(Tutor.class.getSimpleName(), new TutorStubBuilder())
		builderMap.put(Session.class.getSimpleName(), new SessionStubBuilder())
		builderMap.put(StudentConcession.class.getSimpleName(), new StudentConcessionStubBuilder())
		builderMap.put(SystemUser.class.getSimpleName(), new SystemUserStubBuilder())
		builderMap.put(WaitingList.class.getSimpleName(), new WaitingListStubBuilder())
		builderMap.put(WaitingListSite.class.getSimpleName(), new WaitingListSiteStubBuilder())
		builderMap.put(DiscountMembership.class.getSimpleName(), new DiscountMembershipStubBuilder())
		builderMap.put(DiscountMembershipRelationType.class.getSimpleName(), new DiscountMembershipRelationTypeStubBuilder())
		builderMap.put(ContactRelation.class.getSimpleName(), new ContactRelationStubBuilder())
		builderMap.put(ContactRelationType.class.getSimpleName(), new ContactRelationTypeStubBuilder())
		builderMap.put(Membership.class.getSimpleName(), new MembershipStubBuilder())
		builderMap.put(MembershipProduct.class.getSimpleName(), new MembershipProductStubBuilder())
		builderMap.put(Product.class.getSimpleName(), new ProductStubBuilder())
		builderMap.put(ProductItem.class.getSimpleName(), new ProductItemStubBuilder())
		builderMap.put(Voucher.class.getSimpleName(), new VoucherStubBuilder())
		builderMap.put(VoucherProduct.class.getSimpleName(), new VoucherProductStubBuilder())
		builderMap.put(VoucherProductCourse.class.getSimpleName(), new VoucherProductCourseStubBuilder())
		builderMap.put(VoucherPaymentIn.class.getSimpleName(), new VoucherPaymentInStubBuilder())
		builderMap.put(Survey.class.getSimpleName(), new SurveyStubBuilder())
		builderMap.put(SessionModule.class.getSimpleName(), new SessionModuleStubBuilder())
		builderMap.put(EntityRelation.class.getSimpleName(), new EntityRelationStubBuilder())
		builderMap.put(EntityRelationType.class.getSimpleName(), new EntityRelationTypeStubBuilder())
		builderMap.put(CorporatePass.class.getSimpleName(), new CorporatePassStubBuilder())
		builderMap.put(CorporatePassCourseClass.class.getSimpleName(), new CorporatePassCourseClassStubBuilder())
		builderMap.put(CorporatePassProduct.class.getSimpleName(), new CorporatePassProductStubBuilder())
		builderMap.put(Article.class.getSimpleName(), new ArticleStubBuilder())
		builderMap.put(ArticleProduct.class.getSimpleName(), new ArticleProductStubBuilder())
		builderMap.put(Document.class.getSimpleName(), new DocumentStubBuilder())
		builderMap.put(DocumentVersion.class.getSimpleName(), new DocumentVersionStubBuilder())
		builderMap.put(DocumentTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(CustomFieldType.class.getSimpleName(), new CustomFieldTypeStubBuilder())
		builderMap.put(ContactCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(CourseCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(CourseClassCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(EnrolmentCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(ApplicationCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(WaitingListCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(SurveyCustomField.class.getSimpleName(), new CustomFieldStubBuilder())
		builderMap.put(Application.class.getSimpleName(), new ApplicationStubBuilder())
		builderMap.put(ApplicationTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(EmailTemplate.class.getSimpleName(), new EmailTemplateStubBuilder())
		builderMap.put(Script.class.getSimpleName(), new ScriptStubBuilder())
		builderMap.put(CourseClassPaymentPlanLine.class.getSimpleName(), new CourseClassPaymentPlanLineStubBuilder())
		builderMap.put(InvoiceDueDate.class.getSimpleName(), new InvoiceDueDateStubBuilder())
		builderMap.put(EnrolmentTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(CorporatePassDiscount.class.getSimpleName(), new CorporatePassDiscountStubBuilder())
		builderMap.put(PriorLearning.class.getSimpleName(), new PriorLearningStubBuilder())
		builderMap.put(ContactDuplicate.class.getSimpleName(), new ContactDuplicateStubBuilder())
		builderMap.put(Assessment.class.getSimpleName(), new AssessmentStubBuilder())
		builderMap.put(AssessmentClass.class.getSimpleName(), new AssessmentClassStubBuilder())
		builderMap.put(AssessmentClassTutor.class.getSimpleName(), new AssessmentClassTutorStubBuilbedr())
		builderMap.put(AssessmentSubmission.class.getSimpleName(), new AssessmentSubmissionStubBuilder())
		builderMap.put(Field.class.getSimpleName(), new FieldStubBuilder())
		builderMap.put(ApplicationFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder())
		builderMap.put(EnrolmentFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder())
		builderMap.put(WaitingListFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder())
		builderMap.put(SurveyFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder())
		builderMap.put(PayerFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder())
		builderMap.put(ParentFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder())
		builderMap.put(FieldConfigurationLink.class.getSimpleName(), new FieldConfigurationLinkStubBuilder())
		builderMap.put(FieldConfigurationScheme.class.getSimpleName(), new FieldConfigurationSchemeStubBuilder())
		builderMap.put(FieldHeading.class.getSimpleName(), new FieldHeadingStubBuilder())
		builderMap.put(AssessmentClassModule.class.getSimpleName(), new AssessmentClassModuleStubBuilbedr())
		builderMap.put(AssessmentTagRelation.class.getSimpleName(), new TagRelationStubBuilder())
		builderMap.put(Tax.class.getSimpleName(), new TaxStubBuilder())
		builderMap.put(ish.oncourse.server.cayenne.Module.class.getSimpleName(), new ModuleStubBuilder())
		builderMap.put(Qualification.class.getSimpleName(), new QualificationStubBuilder())
		builderMap.put(Checkout.class.getSimpleName(), new CheckoutStubBuilder())

	}

	@Override
	GenericReplicationStub convert(final QueuedRecord entity) {
		final def key = entity.getTableName()
		final def builder = builderMap.get(key)
		if (builder == null) {
			throw new BuilderNotFoundException(String.format(BUILDER_NOT_FOUND_DURING_RECORD_CONVERSION_MESSAGE, key), key)
		}
		return builder.convert(entity)
	}

	/**
	 * @see IAngelStubBuilder#convert(Queueable)
	 */
	@Override
	GenericReplicationStub convert(final Queueable entity) {
		final def key = entity.getObjectId().getEntityName()
		final def builder = builderMap.get(key)
		if (builder == null) {
			throw new BuilderNotFoundException(String.format(BUILDER_NOT_FOUND_DURING_RECORD_CONVERSION_MESSAGE, key), key)
		}
		return builder.convert(entity)
	}
}
