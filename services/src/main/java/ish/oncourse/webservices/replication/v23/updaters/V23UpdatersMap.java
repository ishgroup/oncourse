package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.*;
import ish.oncourse.model.Module;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;

import java.util.HashMap;
import java.util.Map;


public class V23UpdatersMap {
	
	private Map<String, IWillowUpdater> updaterMap;
	
	public void initMap(final IRichtextConverter textileConverter) {
		getUpdaterMap().clear();
		updaterMap.put(Attendance.class.getSimpleName(), new AttendanceUpdater());
		updaterMap.put(BinaryInfo.class.getSimpleName(), new BinaryInfoUpdater());
		updaterMap.put(BinaryInfoRelation.class.getSimpleName(), new BinaryInfoRelationUpdater());
		updaterMap.put(Contact.class.getSimpleName(), new ContactUpdater());
		updaterMap.put(Course.class.getSimpleName(), new CourseUpdater(textileConverter));
		updaterMap.put(CourseClass.class.getSimpleName(), new CourseClassUpdater(textileConverter));
		updaterMap.put(CourseModule.class.getSimpleName(), new CourseModuleUpdater());
		updaterMap.put(ConcessionType.class.getSimpleName(), new ConcessionTypeUpdater());
		updaterMap.put(Certificate.class.getSimpleName(), new CertificateUpdater());
		updaterMap.put(CertificateOutcome.class.getSimpleName(), new CertificateOutcomeUpdater());
		updaterMap.put(Discount.class.getSimpleName(), new DiscountUpdater());
		updaterMap.put(DiscountConcessionType.class.getSimpleName(), new DiscountConcessionTypeUpdater());
		updaterMap.put(DiscountCourseClass.class.getSimpleName(), new DiscountCourseClassUpdater());
		updaterMap.put(Enrolment.class.getSimpleName(), new EnrolmentUpdater());
		updaterMap.put(Outcome.class.getSimpleName(), new OutcomeUpdater());
		updaterMap.put(Invoice.class.getSimpleName(), new InvoiceUpdater());
		updaterMap.put(SaleOrder.class.getSimpleName(), new InvoiceUpdater());
		updaterMap.put(InvoiceLine.class.getSimpleName(), new InvoiceLineUpdater());
		updaterMap.put(InvoiceLineDiscount.class.getSimpleName(), new InvoiceLineDiscountUpdater());
		updaterMap.put(Message.class.getSimpleName(), new MessageUpdater());
		updaterMap.put(MessagePerson.class.getSimpleName(), new MessagePersonUpdater());
		updaterMap.put(MessageTemplate.class.getSimpleName(), new MessageTemplateUpdater());
		updaterMap.put(PaymentIn.class.getSimpleName(), new PaymentInUpdater());
		updaterMap.put(PaymentInLine.class.getSimpleName(), new PaymentInLineUpdater());
		updaterMap.put(PaymentOut.class.getSimpleName(), new PaymentOutUpdater());
		updaterMap.put(Preference.class.getSimpleName(), new PreferenceUpdater());
		updaterMap.put(Student.class.getSimpleName(), new StudentUpdater());
		updaterMap.put(SystemUser.class.getSimpleName(), new SystemUserUpdater());
		updaterMap.put(Tag.class.getSimpleName(), new TagUpdater(textileConverter));
		updaterMap.put(Taggable.class.getSimpleName(), new TagRelationUpdater());
		updaterMap.put(TagGroupRequirement.class.getSimpleName(), new TagGroupRequirementUpdater());
		updaterMap.put(Tutor.class.getSimpleName(), new TutorUpdater(textileConverter));
		updaterMap.put(WaitingList.class.getSimpleName(), new WaitingListUpdater());
		updaterMap.put(WaitingListSite.class.getSimpleName(), new WaitingListSiteUpdater());
		updaterMap.put(Site.class.getSimpleName(), new SiteUpdater(textileConverter));
		updaterMap.put(Room.class.getSimpleName(), new RoomUpdater(textileConverter));
		updaterMap.put(StudentConcession.class.getSimpleName(), new StudentConcessionUpdater());
		updaterMap.put(Session.class.getSimpleName(), new SessionUpdater());
		updaterMap.put(TutorRole.class.getSimpleName(), new TutorRoleUpdater());
		updaterMap.put(SessionTutor.class.getSimpleName(), new SessionTutorUpdater());
		updaterMap.put(ContactRelationType.class.getSimpleName(), new ContactRelationTypeUpdater());
		updaterMap.put(ContactRelation.class.getSimpleName(), new ContactRelationUpdater());
		updaterMap.put(DiscountMembership.class.getSimpleName(), new DiscountMembershipUpdater());
		updaterMap.put(DiscountMembershipRelationType.class.getSimpleName(), new DiscountMembershipRelationTypeUpdater());
		updaterMap.put(Membership.class.getSimpleName(), new MembershipUpdater());
		updaterMap.put(MembershipProduct.class.getSimpleName(), new MembershipProductUpdater());
		updaterMap.put(Product.class.getSimpleName(), new ProductUpdater());
		updaterMap.put(ProductItem.class.getSimpleName(), new ProductItemUpdater());
		updaterMap.put(VoucherProduct.class.getSimpleName(), new VoucherProductUpdater());
		updaterMap.put(Voucher.class.getSimpleName(), new VoucherUpdater());
		updaterMap.put(VoucherPaymentIn.class.getSimpleName(), new VoucherPaymentInUpdater());
		updaterMap.put(QueuedStatistic.class.getSimpleName(), new QueuedStatisticUpdater());
		updaterMap.put(VoucherProductCourse.class.getSimpleName(), new VoucherProductCourseUpdater());
        updaterMap.put(Survey.class.getSimpleName(), new SurveyUpdater());
        updaterMap.put(SessionModule.class.getSimpleName(), new SessionModuleUpdater());
        updaterMap.put(EntityRelation.class.getSimpleName(), new EntityRelationUpdater());
		updaterMap.put(EntityRelationType.class.getSimpleName(), new EntityRelationTypeUpdater());
		updaterMap.put(CorporatePass.class.getSimpleName(), new CorporatePassUpdater());
        updaterMap.put(CorporatePassCourseClass.class.getSimpleName(), new CorporatePassCourseClassUpdater());
		updaterMap.put(ArticleProduct.class.getSimpleName(), new ArticleProductUpdater());
		updaterMap.put(Article.class.getSimpleName(), new ArticleUpdater());
		updaterMap.put(Document.class.getSimpleName(), new DocumentUpdater());
		updaterMap.put(DocumentVersion.class.getSimpleName(), new DocumentVersionUpdater());
		updaterMap.put(CustomFieldType.class.getSimpleName(), new CustomFieldTypeUpdater());
		updaterMap.put(ContactCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(CourseCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(CourseClassCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(EnrolmentCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(ApplicationCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(WaitingListCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(SurveyCustomField.class.getSimpleName(), new CustomFieldUpdater());
		updaterMap.put(Application.class.getSimpleName(), new ApplicationUpdater());
		updaterMap.put(EmailTemplate.class.getSimpleName(), new EmailTemplateUpdater());
		updaterMap.put(Script.class.getSimpleName(), new ScriptUpdater());
		updaterMap.put(CourseClassPaymentPlanLine.class.getSimpleName(), new CourseClassPaymentPlanLineUpdater());
		updaterMap.put(InvoiceDueDate.class.getSimpleName(), new InvoiceDueDateUpdater());
		updaterMap.put(CorporatePassDiscount.class.getSimpleName(), new CorporatePassDiscountUpdater());
		updaterMap.put(PriorLearning.class.getSimpleName(), new PriorLearningUpdater());
		updaterMap.put(CorporatePassProduct.class.getSimpleName(), new CorporatePassProductUpdater());
		updaterMap.put(ContactDuplicate.class.getSimpleName(), new ContactDuplicateUpdater());
		updaterMap.put(Assessment.class.getSimpleName(), new AssessmentUpdater());
		updaterMap.put(AssessmentClass.class.getSimpleName(), new AssessmentClassUpdater());
		updaterMap.put(AssessmentClassTutor.class.getSimpleName(), new AssessmentClassTutorUpdater());
		updaterMap.put(AssessmentSubmission.class.getSimpleName(), new AssessmentSubmissionUpdater());
		updaterMap.put(Field.class.getSimpleName(), new FieldUpdater());
		updaterMap.put(ApplicationFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater());
		updaterMap.put(EnrolmentFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater());
		updaterMap.put(WaitingListFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater());
		updaterMap.put(SurveyFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater());
		updaterMap.put(PayerFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater());
		updaterMap.put(ParentFieldConfiguration.class.getSimpleName(), new FieldConfigurationUpdater());
		updaterMap.put(FieldHeading.class.getSimpleName(), new FieldHeadingUpdater());
		updaterMap.put(FieldConfigurationLink.class.getSimpleName(), new FieldConfigurationLinkUpdater());
		updaterMap.put(FieldConfigurationScheme.class.getSimpleName(), new FieldConfigurationSchemeUpdater());
		updaterMap.put(AssessmentClassModule.class.getSimpleName(), new AssessmentClassModuleUpdater());
		updaterMap.put(Tax.class.getSimpleName(), new TaxUpdater());
		updaterMap.put(Module.class.getSimpleName(), new ModuleUpdater());
		updaterMap.put(Qualification.class.getSimpleName(), new QualificationUpdater());
		updaterMap.put(Checkout.class.getSimpleName(), new CheckoutUpdater());
		updaterMap.put(GradingItem.class.getSimpleName(), new GradingItemUpdater());
		updaterMap.put(GradingType.class.getSimpleName(), new GradingTypeUpdater());
	}

	/**
	 * @return the updaterMap
	 */
	public Map<String, IWillowUpdater> getUpdaterMap() {
		if (updaterMap == null) {
			updaterMap = new HashMap<>();
		}
		return updaterMap;
	}
}
