package ish.oncourse.webservices.replication.v10.builders;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;

import java.util.HashMap;
import java.util.Map;

public class V10BuildersMap {
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
        builderMap.put(EntityRelation.class.getSimpleName(), new EntityRelationStubBuilder());
        builderMap.put(CourseProductRelation.class.getSimpleName(), new EntityRelationStubBuilder());
        builderMap.put(CorporatePass.class.getSimpleName(), new CorporatePassStubBuilder());
        builderMap.put(CorporatePassCourseClass.class.getSimpleName(), new CorporatePassCourseClassStubBuilder());
		builderMap.put(Article.class.getSimpleName(), new ArticleStubBuilder());
		builderMap.put(ArticleProduct.class.getSimpleName(), new ArticleProductStubBuilder());
		builderMap.put(Document.class.getSimpleName(), new DocumentStubBuilder());
		builderMap.put(DocumentVersion.class.getSimpleName(), new DocumentVersionStubBuilder());
		builderMap.put(CustomFieldType.class.getSimpleName(), new CustomFieldTypeStubBuilder());
		builderMap.put(ContactCustomField.class.getSimpleName(), new CustomFieldStubBuilder());
        builderMap.put(ContactRelation.class.getSimpleName(), new ContactRelationStubBuilder());
		builderMap.put(Application.class.getSimpleName(), new ApplicationStubBuilder());
		builderMap.put(EmailTemplate.class.getSimpleName(), new EmailTemplateStubBuilred());
		builderMap.put(Script.class.getSimpleName(), new ScriptStubBuilder());
		builderMap.put(CourseClassPaymentPlanLine.class.getSimpleName(), new CourseClassPaymentPlanLineStubBuilder());
		builderMap.put(InvoiceDueDate.class.getSimpleName(), new InvoiceDueDateStubBuilder());
    }
}
