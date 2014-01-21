package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.v4.builders.*;
import ish.oncourse.webservices.replication.v5.builders.CorporatePassCourseClassStubBuilder;
import ish.oncourse.webservices.replication.v5.builders.CorporatePassStubBuilder;
import ish.oncourse.webservices.replication.v5.builders.SurveyStubBuilder;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.Cayenne;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

/**
 * The bash command line generates test methods for all stubs
 * ls | grep -Ev '(^Abstract|^Willow)' | awk -F . '{print $1}' | awk '{print "@Test\npublic void test"$1"(){\n         this.testStubBuilder(.class, new "$1"());\n}"}'
 */
public class AllStubBuildersTest extends AbstractAllStubBuildersTest {

    @Test
    public void testV5AttendanceStubBuilder() {
        this.testStubBuilder(Attendance.class, new ish.oncourse.webservices.replication.v5.builders.AttendanceStubBuilder());
    }

    @Test
    public void testV5BinaryInfoStubBuilder() {
        this.testStubBuilder(BinaryInfo.class, new BinaryInfoStubBuilder(),"webVisible");
    }

    @Test
    public void testV5ConcessionTypeStubBuilder() {
        this.testStubBuilder(ConcessionType.class, new ish.oncourse.webservices.replication.v5.builders.ConcessionTypeStubBuilder());
    }

    @Test
    public void testV5ContactStubBuilder() {
        this.testStubBuilder(Contact.class, new ish.oncourse.webservices.replication.v5.builders.ContactStubBuilder());
    }

    @Test
    public void testV5CourseClassStubBuilder() {
        this.testStubBuilder(CourseClass.class, new ish.oncourse.webservices.replication.v5.builders.CourseClassStubBuilder(),
                "materialsTextile","modified","sessionDetailTextile", "startDate","startingMinutePerSession", "timeZone");
    }

    @Test
    public void testV5CourseStubBuilder() {
        this.testStubBuilder(Course.class, new ish.oncourse.webservices.replication.v5.builders.CourseStubBuilder());
    }

    @Test
    public void testV5DiscountStubBuilder() {
        this.testStubBuilder(Discount.class, new ish.oncourse.webservices.replication.v5.builders.DiscountStubBuilder(), "codeRequired");
    }

    @Test
    public void testV5EnrolmentStubBuilder() {
    	//invoiceLineId added to exclude property because relationship updated to many
        this.testStubBuilder(Enrolment.class, new ish.oncourse.webservices.replication.v5.builders.EnrolmentStubBuilder(), "invoiceLineId");
    }


    @Test
    public void testV5InvoiceLineDiscountStubBuilder() {
        this.testStubBuilder(InvoiceLineDiscount.class, new ish.oncourse.webservices.replication.v5.builders.InvoiceLineDiscountStubBuilder());
    }

    @Test
    public void testV5InvoiceLineStubBuilder() {
        this.testStubBuilder(InvoiceLine.class, new ish.oncourse.webservices.replication.v5.builders.InvoiceLineStubBuilder());
    }

    @Test
    public void testV5InvoiceStubBuilder() {
        this.testStubBuilder(Invoice.class, new ish.oncourse.webservices.replication.v5.builders.InvoiceStubBuilder(), "status", "customerPO");
    }

    @Test
    public void testV5MessagePersonStubBuilder() {
        this.testStubBuilder(MessagePerson.class, new ish.oncourse.webservices.replication.v5.builders.MessagePersonStubBuilder(),1l, "tutorId");
        this.testStubBuilder(MessagePerson.class, new ish.oncourse.webservices.replication.v5.builders.MessagePersonStubBuilder(),2l, "studentId");
    }

    @Test
    public void testV5OutcomeStubBuilder() {
        this.testStubBuilder(Outcome.class, new ish.oncourse.webservices.replication.v5.builders.OutcomeStubBuilder(),"endDate","hoursAttended","startDate","status");
    }

    @Test
    public void testV5PaymentInLineStubBuilder() {
        this.testStubBuilder(PaymentInLine.class, new ish.oncourse.webservices.replication.v5.builders.PaymentInLineStubBuilder());
    }

    @Test
    public void testV5PaymentInStubBuilder() {
        this.testStubBuilder(PaymentIn.class, new ish.oncourse.webservices.replication.v5.builders.PaymentInStubBuilder());
    }

    @Test
    public void testV5PaymentOutStubBuilder() {
        this.testStubBuilder(PaymentOut.class, new ish.oncourse.webservices.replication.v5.builders.PaymentOutStubBuilder());
    }

    @Test
    public void testV5PreferenceStubBuilder() {
        this.testStubBuilder(Preference.class, new ish.oncourse.webservices.replication.v5.builders.PreferenceStubBuilder());
    }

    @Test
    public void testV5RoomStubBuilder() {
        this.testStubBuilder(Room.class, new ish.oncourse.webservices.replication.v5.builders.RoomStubBuilder(), "directions","facilities");
    }

    @Test
    public void testV5SiteStubBuilder() {
        this.testStubBuilder(Site.class, new ish.oncourse.webservices.replication.v5.builders.SiteStubBuilder(), "drivingDirections", "publicTransportDirections",
                "specialInstructions","timeZone", "webVisible");
    }

    @Test
    public void testV5StudentConcessionStubBuilder() {
        this.testStubBuilder(StudentConcession.class, new ish.oncourse.webservices.replication.v5.builders.StudentConcessionStubBuilder());
    }

    @Test
    public void testV5StudentStubBuilder() {
        this.testStubBuilder(Student.class, new ish.oncourse.webservices.replication.v5.builders.StudentStubBuilder());
    }
    
    @Test
    public void testV5SystemUserStubBuilder() {
        this.testStubBuilder(SystemUser.class, new ish.oncourse.webservices.replication.v5.builders.SystemUserStubBuilder());
    }

    @Test

    public void testV5TaggableStubBuilder() {
        this.testStubBuilder(Taggable.class, new ish.oncourse.webservices.replication.v5.builders.TaggableStubBuilder());
    }

    @Test
    public void testV5TutorRoleStubBuilder() {
        this.testStubBuilder(TutorRole.class, new ish.oncourse.webservices.replication.v5.builders.TutorRoleStubBuilder(), "inPublicity");
    }

    @Test
    public void testV5TutorStubBuilder() {
        this.testStubBuilder(Tutor.class, new ish.oncourse.webservices.replication.v5.builders.TutorStubBuilder(), "resume");
    }

    @Test
    public void testV5WaitingListStubBuilder() {
        this.testStubBuilder(WaitingList.class, new ish.oncourse.webservices.replication.v5.builders.WaitingListStubBuilder());
    }

    @Test
    public void testV5CertificateStubBuilder() {
        this.testStubBuilder(Certificate.class, new ish.oncourse.webservices.replication.v5.builders.CertificateStubBuilder());
    }

    @Test
    public void testV5CertificateOutcome() {
        this.testStubBuilder(CertificateOutcome.class, new ish.oncourse.webservices.replication.v5.builders.CertificateOutcomeStubBuilder());
    }
    
    @Test
    public void testAttendanceStubBuilder() {
        this.testStubBuilder(Attendance.class, new AttendanceStubBuilder());
    }

    @Test
    public void testBinaryInfoStubBuilder() {
        this.testStubBuilder(BinaryInfo.class, new BinaryInfoStubBuilder(),"webVisible", "contextPath");
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
    public void testCourseClassStubBuilder() {
        this.testStubBuilder(CourseClass.class, new CourseClassStubBuilder(),
                "materialsTextile","modified","sessionDetailTextile", "startDate","startingMinutePerSession", "timeZone");
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
    	//invoiceLineId added to exclude property because relationship updated to many
        this.testStubBuilder(Enrolment.class, new EnrolmentStubBuilder(), "invoiceLineId");
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
    public void testMessagePersonStubBuilder() {
        this.testStubBuilder(MessagePerson.class, new MessagePersonStubBuilder(),1l, "tutorId");
        this.testStubBuilder(MessagePerson.class, new MessagePersonStubBuilder(),2l, "studentId");
    }

    @Test
    public void testOutcomeStubBuilder() {
        this.testStubBuilder(Outcome.class, new OutcomeStubBuilder(),"endDate","hoursAttended","startDate","status");
    }

    @Test
    public void testPaymentInLineStubBuilder() {
        this.testStubBuilder(PaymentInLine.class, new PaymentInLineStubBuilder());
    }

    @Test
    public void testPaymentInStubBuilder() {
        this.testStubBuilder(PaymentIn.class, new PaymentInStubBuilder());
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
        this.testStubBuilder(Room.class, new RoomStubBuilder(), "directions","facilities");
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
    public void testWaitingListStubBuilder() {
        this.testStubBuilder(WaitingList.class, new WaitingListStubBuilder());
    }

    @Test
    public void testCertificateStubBuilder() {
        this.testStubBuilder(Certificate.class, new CertificateStubBuilder());
    }

    @Test
    public void testCertificateOutcome() {
        this.testStubBuilder(CertificateOutcome.class, new CertificateOutcomeStubBuilder());
    }

    @Test
    public void testSurvey() {
        this.testStubBuilder(Survey.class, new SurveyStubBuilder());
    }
    
    @Test
    public void testCorporatePass() {
    	this.testStubBuilder(CorporatePass.class, new CorporatePassStubBuilder());
    }
    
    @Test
    public void testCorporatePassCourseClass() {
    	this.testStubBuilder(CorporatePassCourseClass.class, new CorporatePassCourseClassStubBuilder());
    }

}
