package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;

import java.util.HashMap;
import java.util.Map;

public class V23BuildersMap {
	
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
		builderMap.put(Attendance.class.getSimpleName(), new AttendanceStubBuilder());
		builderMap.put(BinaryInfo.class.getSimpleName(), new BinaryInfoStubBuilder());
		builderMap.put(ConcessionType.class.getSimpleName(), new ConcessionTypeStubBuilder());
		builderMap.put(CourseClass.class.getSimpleName(), new CourseClassStubBuilder());
		builderMap.put(Contact.class.getSimpleName(), new ContactStubBuilder());
		builderMap.put(Enrolment.class.getSimpleName(), new EnrolmentStubBuilder());
		builderMap.put(Discount.class.getSimpleName(), new DiscountStubBuilder());
		builderMap.put(Invoice.class.getSimpleName(), new InvoiceStubBuilder());
		builderMap.put(SaleOrder.class.getSimpleName(), new InvoiceStubBuilder());
		builderMap.put(InvoiceLine.class.getSimpleName(), new InvoiceLineStubBuilder());
		builderMap.put(InvoiceLineDiscount.class.getSimpleName(), new InvoiceLineDiscountStubBuilder());
		builderMap.put(MessagePerson.class.getSimpleName(), new MessagePersonStubBuilder());
		builderMap.put(PaymentInLine.class.getSimpleName(), new PaymentInLineStubBuilder());
		builderMap.put(PaymentIn.class.getSimpleName(), new PaymentInStubBuilder());
		builderMap.put(PaymentOut.class.getSimpleName(), new PaymentOutStubBuilder());
		builderMap.put(Preference.class.getSimpleName(), new PreferenceStubBuilder());
		builderMap.put(StudentConcession.class.getSimpleName(), new StudentConcessionStubBuilder());
		builderMap.put(Student.class.getSimpleName(), new StudentStubBuilder());
		builderMap.put(SystemUser.class.getSimpleName(), new SystemUserStubBuilder());
		builderMap.put(Tutor.class.getSimpleName(), new TutorStubBuilder());
		builderMap.put(TutorRole.class.getSimpleName(), new TutorRoleStubBuilder());
		builderMap.put(Site.class.getSimpleName(), new SiteStubBuilder());
		builderMap.put(Course.class.getSimpleName(), new CourseStubBuilder());
		builderMap.put(Room.class.getSimpleName(), new RoomStubBuilder());
		builderMap.put(WaitingList.class.getSimpleName(), new WaitingListStubBuilder());
		builderMap.put(Taggable.class.getSimpleName(), new TaggableStubBuilder());
		builderMap.put(Outcome.class.getSimpleName(), new OutcomeStubBuilder());
        builderMap.put(Certificate.class.getSimpleName(), new CertificateStubBuilder());
        builderMap.put(CertificateOutcome.class.getSimpleName(), new CertificateOutcomeStubBuilder());
        builderMap.put(Voucher.class.getSimpleName(), new VoucherStubBuilder());
		builderMap.put(VoucherProduct.class.getSimpleName(), new VoucherProductStubBuilder());
        builderMap.put(VoucherPaymentIn.class.getSimpleName(), new VoucherPaymentInStubBuilder());
        builderMap.put(VoucherProductCourse.class.getSimpleName(), new VoucherProductCourseStubBuilder());
        builderMap.put(Survey.class.getSimpleName(), new SurveyStubBuilder());
        builderMap.put(Membership.class.getSimpleName(), new MembershipStubBuilder());
        builderMap.put(MembershipProduct.class.getSimpleName(), new MembershipProductStubBuilder());
        builderMap.put(SessionModule.class.getSimpleName(), new SessionModuleStubBuilder());
		builderMap.put(CorporatePass.class.getSimpleName(), new CorporatePassStubBuilder());
        builderMap.put(CorporatePassCourseClass.class.getSimpleName(), new CorporatePassCourseClassStubBuilder());
		builderMap.put(Article.class.getSimpleName(), new ArticleStubBuilder());
		builderMap.put(ArticleProduct.class.getSimpleName(), new ArticleProductStubBuilder());
		builderMap.put(Document.class.getSimpleName(), new DocumentStubBuilder());
		builderMap.put(DocumentVersion.class.getSimpleName(), new DocumentVersionStubBuilder());
		builderMap.put(CustomFieldType.class.getSimpleName(), new CustomFieldTypeStubBuilder());
		builderMap.put(ContactCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
		builderMap.put(CourseCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
		builderMap.put(CourseClassCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
		builderMap.put(EnrolmentCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
		builderMap.put(ApplicationCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
		builderMap.put(WaitingListCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
		builderMap.put(SurveyCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
        builderMap.put(ContactRelation.class.getSimpleName(), new ContactRelationStubBuilder());
		builderMap.put(Application.class.getSimpleName(), new ApplicationStubBuilder());
		builderMap.put(EmailTemplate.class.getSimpleName(), new EmailTemplateStubBuilred());
		builderMap.put(Script.class.getSimpleName(), new ScriptStubBuilder());
		builderMap.put(CourseClassPaymentPlanLine.class.getSimpleName(), new CourseClassPaymentPlanLineStubBuilder());
		builderMap.put(InvoiceDueDate.class.getSimpleName(), new InvoiceDueDateStubBuilder());
		builderMap.put(CorporatePassDiscount.class.getSimpleName(), new CorporatePassDiscountStubBuilder());
		builderMap.put(PriorLearning.class.getSimpleName(), new PriorLearningStubBuilder());
		builderMap.put(CorporatePassProduct.class.getSimpleName(), new CorporatePassProductStubBuilder());
		builderMap.put(ContactDuplicate.class.getSimpleName(), new ContactDuplicateStubBuilder());
		builderMap.put(Assessment.class.getSimpleName(), new AssessmentStubBuilder());
		builderMap.put(AssessmentClass.class.getSimpleName(), new AssessmentClassStubBuilder());
		builderMap.put(AssessmentClassTutor.class.getSimpleName(), new AssessmentClassTutorStubBuilder());
		builderMap.put(AssessmentSubmission.class.getSimpleName(), new AssessmentSubmissionStubBuilder());
		builderMap.put(BinaryInfoRelation.class.getSimpleName(), new BinaryInfoRelationStubBuilder());
		builderMap.put(Field.class.getSimpleName(), new FieldStubBuilder());
		builderMap.put(FieldHeading.class.getSimpleName(), new FieldHeadingStubBuilder());
		builderMap.put(ApplicationFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(EnrolmentFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(WaitingListFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(SurveyFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(PayerFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(ParentFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(ArticleFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(MembershipFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(VoucherFieldConfiguration.class.getSimpleName(), new FieldConfigurationStubBuilder());
		builderMap.put(FieldConfigurationLink.class.getSimpleName(), new FieldConfigurationLinkStubBuilder());
		builderMap.put(FieldConfigurationScheme.class.getSimpleName(), new FieldConfigurationSchemeStubBuilder());
		builderMap.put(AssessmentClassModule.class.getSimpleName(), new AssessmentClassModuleStubBuilder());
		builderMap.put(Checkout.class.getSimpleName(), new CheckoutStubBuilder());
		builderMap.put(GradingType.class.getSimpleName(), new GradingTypeStubBuilder());
		builderMap.put(GradingItem.class.getSimpleName(), new GradingItemStubBuilder());
	}
}
