package ish.oncourse.webservices.replication.v14.builders;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;

public class V14BuildersMap {
private Map<String, IWillowStubBuilder> builderMap;
	
	/**
	 * @return the builderMap
	 */
	public Map<String, IWillowStubBuilder> getBuilderMap() {
		if (builderMap == null) {
			builderMap = new HashMap<>();
		}
		return builderMap;
	}
	
	public void initMap() {
		getBuilderMap().clear();
		builderMap.put(getEntityName(Attendance.class), new AttendanceStubBuilder());
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
		builderMap.put(getEntityName(SystemUser.class), new SystemUserStubBuilder());
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
        builderMap.put(getEntityName(Voucher.class), new VoucherStubBuilder());
		builderMap.put(getEntityName(VoucherProduct.class), new VoucherProductStubBuilder());
        builderMap.put(getEntityName(VoucherPaymentIn.class), new VoucherPaymentInStubBuilder());
        builderMap.put(getEntityName(VoucherProductCourse.class), new VoucherProductCourseStubBuilder());
        builderMap.put(getEntityName(Survey.class), new SurveyStubBuilder());
        builderMap.put(getEntityName(Membership.class), new MembershipStubBuilder());
        builderMap.put(getEntityName(MembershipProduct.class), new MembershipProductStubBuilder());
        builderMap.put(getEntityName(SessionModule.class), new SessionModuleStubBuilder());
        builderMap.put(getEntityName(EntityRelation.class), new EntityRelationStubBuilder());
        builderMap.put(getEntityName(CourseProductRelation.class), new EntityRelationStubBuilder());
        builderMap.put(getEntityName(CorporatePass.class), new CorporatePassStubBuilder());
        builderMap.put(getEntityName(CorporatePassCourseClass.class), new CorporatePassCourseClassStubBuilder());
		builderMap.put(getEntityName(Article.class), new ArticleStubBuilder());
		builderMap.put(getEntityName(ArticleProduct.class), new ArticleProductStubBuilder());
		builderMap.put(getEntityName(Document.class), new DocumentStubBuilder());
		builderMap.put(getEntityName(DocumentVersion.class), new DocumentVersionStubBuilder());
		builderMap.put(getEntityName(CustomFieldType.class), new CustomFieldTypeStubBuilder());
		builderMap.put(getEntityName(ContactCustomField.class), new CustomFieldStubBuilder());
        builderMap.put(getEntityName(ContactRelation.class), new ContactRelationStubBuilder());
		builderMap.put(getEntityName(Application.class), new ApplicationStubBuilder());
		builderMap.put(getEntityName(EmailTemplate.class), new EmailTemplateStubBuilred());
		builderMap.put(getEntityName(Script.class), new ScriptStubBuilder());
		builderMap.put(getEntityName(CourseClassPaymentPlanLine.class), new CourseClassPaymentPlanLineStubBuilder());
		builderMap.put(getEntityName(InvoiceDueDate.class), new InvoiceDueDateStubBuilder());
		builderMap.put(getEntityName(CorporatePassDiscount.class), new CorporatePassDiscountStubBuilder());
		builderMap.put(getEntityName(PriorLearning.class), new PriorLearningStubBuilder());
		builderMap.put(getEntityName(CorporatePassProduct.class), new CorporatePassProductStubBuilder());
		builderMap.put(getEntityName(ContactDuplicate.class), new ContactDuplicateStubBuilder());
		builderMap.put(getEntityName(Assessment.class), new AssessmentStubBuilder());
		builderMap.put(getEntityName(AssessmentClass.class), new AssessmentClassStubBuilder());
		builderMap.put(getEntityName(AssessmentClassTutor.class), new AssessmentClassTutorStubBuilder());
		builderMap.put(getEntityName(AssessmentSubmission.class), new AssessmentSubmissionStubBuilder());
		builderMap.put(getEntityName(BinaryInfoRelation.class), new BinaryInfoRelationStubBuilder());
		builderMap.put(getEntityName(Field.class), new FieldStubBuilder());
		builderMap.put(getEntityName(FieldHeading.class), new FieldHeadingStubBuilder());
		builderMap.put(getEntityName(EnrolmentFieldConfiguration.class), new FieldConfigurationStubBuilder());
		builderMap.put(getEntityName(FieldConfigurationScheme.class), new FieldConfigurationSchemeStubBuilder());
		builderMap.put(getEntityName(AssessmentClassModule.class), new AssessmentClassModuleStubBuilder());
	}
}
