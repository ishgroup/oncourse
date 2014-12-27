package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.*;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;

public class V7UpdatersMap {
private Map<String, IWillowUpdater> updaterMap;
	
	public void initMap(final ITextileConverter textileConverter) {
		getUpdaterMap().clear();
		updaterMap.put(getEntityName(Attendance.class), new AttendanceUpdater());
		updaterMap.put(getEntityName(BinaryInfo.class), new BinaryInfoUpdater());
		updaterMap.put(getEntityName(BinaryInfoRelation.class), new BinaryInfoRelationUpdater());
		updaterMap.put(getEntityName(Contact.class), new ContactUpdater());
		updaterMap.put(getEntityName(Course.class), new CourseUpdater(textileConverter));
		updaterMap.put(getEntityName(CourseClass.class), new CourseClassUpdater(textileConverter));
		updaterMap.put(getEntityName(CourseModule.class), new CourseModuleUpdater());
		updaterMap.put(getEntityName(ConcessionType.class), new ConcessionTypeUpdater());
		updaterMap.put(getEntityName(Certificate.class), new CertificateUpdater());
		updaterMap.put(getEntityName(CertificateOutcome.class), new CertificateOutcomeUpdater());
		updaterMap.put(getEntityName(Discount.class), new DiscountUpdater());
		updaterMap.put(getEntityName(DiscountConcessionType.class), new DiscountConcessionTypeUpdater());
		updaterMap.put(getEntityName(DiscountCourseClass.class), new DiscountCourseClassUpdater());
		updaterMap.put(getEntityName(Enrolment.class), new EnrolmentUpdater());
		updaterMap.put(getEntityName(Outcome.class), new OutcomeUpdater());
		updaterMap.put(getEntityName(Invoice.class), new InvoiceUpdater());
		updaterMap.put(getEntityName(InvoiceLine.class), new InvoiceLineUpdater());
		updaterMap.put(getEntityName(InvoiceLineDiscount.class), new InvoiceLineDiscountUpdater());
		updaterMap.put(getEntityName(Message.class), new MessageUpdater());
		updaterMap.put(getEntityName(MessagePerson.class), new MessagePersonUpdater());
		updaterMap.put(getEntityName(MessageTemplate.class), new MessageTemplateUpdater());
		updaterMap.put(getEntityName(PaymentIn.class), new PaymentInUpdater());
		updaterMap.put(getEntityName(PaymentInLine.class), new PaymentInLineUpdater());
		updaterMap.put(getEntityName(PaymentOut.class), new PaymentOutUpdater());
		updaterMap.put(getEntityName(Preference.class), new PreferenceUpdater());
		updaterMap.put(getEntityName(Student.class), new StudentUpdater());
		updaterMap.put(getEntityName(SystemUser.class), new SystemUserUpdater());
		updaterMap.put(getEntityName(Tag.class), new TagUpdater(textileConverter));
		updaterMap.put(getEntityName(Taggable.class), new TagRelationUpdater());
		updaterMap.put(getEntityName(TagGroupRequirement.class), new TagGroupRequirementUpdater());
		updaterMap.put(getEntityName(Tutor.class), new TutorUpdater(textileConverter));
		updaterMap.put(getEntityName(WaitingList.class), new WaitingListUpdater());
		updaterMap.put(getEntityName(WaitingListSite.class), new WaitingListSiteUpdater());
		updaterMap.put(getEntityName(Site.class), new SiteUpdater(textileConverter));
		updaterMap.put(getEntityName(Room.class), new RoomUpdater(textileConverter));
		updaterMap.put(getEntityName(StudentConcession.class), new StudentConcessionUpdater());
		updaterMap.put(getEntityName(Session.class), new SessionUpdater());
		updaterMap.put(getEntityName(TutorRole.class), new TutorRoleUpdater());
		updaterMap.put(getEntityName(SessionTutor.class), new SessionTutorUpdater());
		updaterMap.put(getEntityName(ContactRelationType.class), new ContactRelationTypeUpdater());
		updaterMap.put(getEntityName(ContactRelation.class), new ContactRelationUpdater());
		updaterMap.put(getEntityName(DiscountMembership.class), new DiscountMembershipUpdater());
		updaterMap.put(getEntityName(DiscountMembershipRelationType.class), new DiscountMembershipRelationTypeUpdater());
		updaterMap.put(getEntityName(Membership.class), new MembershipUpdater());
		updaterMap.put(getEntityName(MembershipProduct.class), new MembershipProductUpdater());
		updaterMap.put(getEntityName(Product.class), new ProductUpdater());
		updaterMap.put(getEntityName(ProductItem.class), new ProductItemUpdater());
		updaterMap.put(getEntityName(VoucherProduct.class), new VoucherProductUpdater());
		updaterMap.put(getEntityName(Voucher.class), new VoucherUpdater());
		updaterMap.put(getEntityName(VoucherPaymentIn.class), new VoucherPaymentInUpdater());
		updaterMap.put(getEntityName(QueuedStatistic.class), new QueuedStatisticUpdater());
		updaterMap.put(getEntityName(VoucherProductCourse.class), new VoucherProductCourseUpdater());
        updaterMap.put(getEntityName(Survey.class), new SurveyUpdater());
        updaterMap.put(getEntityName(SessionModule.class), new SessionModuleUpdater());
        updaterMap.put(getEntityName(EntityRelation.class), new EntityRelationUpdater());
        updaterMap.put(getEntityName(CourseCourseRelation.class), new CourseCourseRelationUpdater());
        updaterMap.put(getEntityName(CourseProductRelation.class), new EntityRelationUpdater());
        updaterMap.put(getEntityName(CorporatePass.class), new CorporatePassUpdater());
        updaterMap.put(getEntityName(CorporatePassCourseClass.class), new CorporatePassCourseClassUpdater());
		updaterMap.put(getEntityName(ArticleProduct.class), new ArticleProductUpdater());
		updaterMap.put(getEntityName(Article.class), new ArticleUpdater());
		updaterMap.put(getEntityName(Document.class), new DocumentUpdater());
		updaterMap.put(getEntityName(DocumentVersion.class), new DocumentVersionUpdater());
		updaterMap.put(getEntityName(CustomFieldType.class), new CustomFieldTypeUpdater());
		updaterMap.put(getEntityName(CustomField.class), new CustomFieldUpdater());
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
