/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.v8.builders.*;
import org.junit.Test;

public class AllV8StubBuildersTest extends AbstractAllStubBuildersTest {

	@Test
	public void testArticleProductStubBuilder() {
		this.testStubBuilder(ArticleProduct.class, new ArticleProductStubBuilder(), 2l, "incomeAccountId", "taxAmount", "taxId", "weight");
	}

	@Test
	public void testArticleStubBuilder() {
		this.testStubBuilder(Article.class, new ArticleStubBuilder(),2l);
	}

	@Test
	public void testAttendanceStubBuilder() {
		this.testStubBuilder(Attendance.class, new AttendanceStubBuilder());
	}

	@Test
	public void testBinaryInfoStubBuilder() {
		this.testStubBuilder(BinaryInfo.class, new BinaryInfoStubBuilder(), "fileUUID", "thumbnail");
	}

	@Test
	public void testCertificateOutcomeStubBuilder() {
		this.testStubBuilder(CertificateOutcome.class, new CertificateOutcomeStubBuilder());
	}

	@Test
	public void testCertificateStubBuilder() {
		this.testStubBuilder(Certificate.class, new CertificateStubBuilder());
	}

	@Test
	public void testConcessionTypeStubBuilder() {
		this.testStubBuilder(ConcessionType.class, new ConcessionTypeStubBuilder());
	}

	@Test
	public void testContactStubBuilder() {
		this.testStubBuilder(Contact.class, new ContactStubBuilder());
	}

	@Test
	public void testCorporatePassCourseClassStubBuilder() {
		this.testStubBuilder(CorporatePassCourseClass.class, new CorporatePassCourseClassStubBuilder());
	}

	@Test
	public void testCorporatePassStubBuilder() {
		this.testStubBuilder(CorporatePass.class, new CorporatePassStubBuilder());
	}

	@Test
	public void testCourseClassStubBuilder() {
		this.testStubBuilder(CourseClass.class, new CourseClassStubBuilder(),
				"materialsTextile", "sessionDetailTextile", "startDate",
				"startingMinutePerSession", "timeZone");
	}

	@Test
	public void testCourseStubBuilder() {
		this.testStubBuilder(Course.class, new CourseStubBuilder());
	}

	@Test
	public void testDiscountStubBuilder() {
		this.testStubBuilder(Discount.class, new DiscountStubBuilder(), "codeRequired");
	}

	@Test
	public void testEnrolmentStubBuilder() {
		this.testStubBuilder(Enrolment.class, new EnrolmentStubBuilder(),
				"invoiceLineId");
	}

	@Test
	public void testEntityRelationStubBuilder() {
		this.testStubBuilder(EntityRelation.class, new EntityRelationStubBuilder(),"fromEntityAngelId","toEntityAngelId");
	}

	@Test
	public void testInvoiceLineDiscountStubBuilder() {
		this.testStubBuilder(InvoiceLineDiscount.class, new InvoiceLineDiscountStubBuilder());
	}

	@Test
	public void testInvoiceLineStubBuilder() {
		this.testStubBuilder(InvoiceLine.class, new InvoiceLineStubBuilder());
	}

	@Test
	public void testInvoiceStubBuilder() {
		this.testStubBuilder(Invoice.class, new InvoiceStubBuilder(), "status", "customerPO");
	}

	@Test
	public void testMembershipProductStubBuilder() {
		this.testStubBuilder(MembershipProduct.class, new MembershipProductStubBuilder(), 1l,"incomeAccountId","taxAmount","taxId");
	}

	@Test
	public void testMembershipStubBuilder() {
		this.testStubBuilder(Membership.class, new MembershipStubBuilder());
	}

	@Test
	public void testMessagePersonStubBuilder() {
		this.testStubBuilder(MessagePerson.class, new MessagePersonStubBuilder(), 1l, "tutorId");
		this.testStubBuilder(MessagePerson.class, new MessagePersonStubBuilder() ,2l, "studentId");
	}

	@Test
	public void testOutcomeStubBuilder() {
		this.testStubBuilder(Outcome.class, new OutcomeStubBuilder(),"endDate","hoursAttended","startDate");
	}

	@Test
	public void testPaymentInLineStubBuilder() {
		this.testStubBuilder(PaymentInLine.class, new PaymentInLineStubBuilder());
	}

	@Test
	public void testPaymentInStubBuilder() {
		this.testStubBuilder(PaymentIn.class, new PaymentInStubBuilder(),1l);
		//for revers payment
		this.testStubBuilder(PaymentIn.class, new PaymentInStubBuilder(),2l);
	}

	@Test
	public void testPaymentOutStubBuilder() {
		this.testStubBuilder(PaymentOut.class, new PaymentOutStubBuilder());
	}

	@Test
	public void testPreferenceStubBuilder() {
		this.testStubBuilder(Preference.class, new PreferenceStubBuilder());
	}

	@Test
	public void testRoomStubBuilder() {
		this.testStubBuilder(Room.class, new RoomStubBuilder(),"directions","facilities");
	}

	@Test
	public void testSessionModuleStubBuilder() {
		this.testStubBuilder(SessionModule.class, new SessionModuleStubBuilder());
	}

	@Test
	public void testSiteStubBuilder() {
		this.testStubBuilder(Site.class, new SiteStubBuilder(), "drivingDirections", "publicTransportDirections",
				"specialInstructions","timeZone", "webVisible");
	}

	@Test
	public void testStudentConcessionStubBuilder() {
		this.testStubBuilder(StudentConcession.class, new StudentConcessionStubBuilder());
	}

	@Test
	public void testStudentStubBuilder() {
		this.testStubBuilder(Student.class, new StudentStubBuilder());
	}

	@Test
	public void testSurveyStubBuilder() {
		this.testStubBuilder(Survey.class, new SurveyStubBuilder());
	}

	@Test
	public void testSystemUserStubBuilder() {
		this.testStubBuilder(SystemUser.class, new SystemUserStubBuilder());
	}

	@Test
	public void testTaggableStubBuilder() {
		this.testStubBuilder(Taggable.class, new TaggableStubBuilder());
	}

	@Test
	public void testTutorRoleStubBuilder() {
		this.testStubBuilder(TutorRole.class, new TutorRoleStubBuilder(), "inPublicity");
	}

	@Test
	public void testTutorStubBuilder() {
		this.testStubBuilder(Tutor.class, new TutorStubBuilder(), "resume");
	}

	@Test
	public void testVoucherPaymentInStubBuilder() {
		this.testStubBuilder(VoucherPaymentIn.class, new VoucherPaymentInStubBuilder());
	}

	@Test
	public void testVoucherProductCourseStubBuilder() {
		this.testStubBuilder(VoucherProductCourse.class, new VoucherProductCourseStubBuilder());
	}

	@Test
	public void testVoucherStubBuilder() {
		// test course voucher
		this.testStubBuilder(Voucher.class, new VoucherStubBuilder(), 3l, "key", "valueOnPurchase", "redemptionValue");

		// test money voucher
		this.testStubBuilder(Voucher.class, new VoucherStubBuilder(), 4l, "key", "redeemedCoursesCount");
	}

	@Test
	public void testVoucherProductStubBuilder() {
		// test course voucher
		this.testStubBuilder(VoucherProduct.class, new VoucherProductStubBuilder(), 3l, "incomeAccountId", "taxAmount", "taxId", "value");

		// test money voucher
		this.testStubBuilder(VoucherProduct.class, new VoucherProductStubBuilder(), 4l, "incomeAccountId", "taxAmount", "taxId", "maxCoursesRedemption");
	}

	@Test
	public void testWaitingListStubBuilder() {
		this.testStubBuilder(WaitingList.class, new WaitingListStubBuilder());
	}

	@Test
	public void testDocumentStubBuilder() {
		this.testStubBuilder(Document.class, new DocumentStubBuilder(), 1L, "webVisible");
	}

	@Test
	public void testDocumentVersionStubBuilder() {
		this.testStubBuilder(DocumentVersion.class, new DocumentVersionStubBuilder(), 1L, "thumbnail");
	}

	@Test
	public void testCustomFieldTypeStubBuilder() {
		this.testStubBuilder(CustomFieldType.class, new CustomFieldTypeStubBuilder());
	}

	@Test
	public void testCustomFieldStubBuilder() {
		this.testStubBuilder(CustomField.class, new CustomFieldStubBuilder(), "foreignId");
	}

	@Test
	public void testApplicationStubBuilder() {
		this.testStubBuilder(Application.class, new ApplicationStubBuilder(), 1L);
	}
}
