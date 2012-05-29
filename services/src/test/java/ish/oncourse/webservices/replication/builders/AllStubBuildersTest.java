package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import org.apache.cayenne.Cayenne;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

/**
 * The bash command line generates test methods for all stubs
 * ls | grep -Ev '(^Abstract|^Willow)' | awk -F . '{print $1}' | awk '{print "@Test\npublic void test"$1"(){\n         this.testStubBuilder(.class, new "$1"());\n}"}'
 */
public class AllStubBuildersTest extends ServiceTest {

    @Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

        initReferenceDataSet();
        initOncourseBinaryDataSet();
        initOncourseDataSet();
    }

    private void initOncourseBinaryDataSet() throws Exception {

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/builders/oncourseBinaryDataSet.xml");
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);


        DataSource onDataSource = getDataSource("jdbc/oncourse_binary");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }


    private void initOncourseDataSet() throws Exception {


        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/builders/oncourseDataSet.xml");
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);


        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }

    private void initReferenceDataSet() throws Exception {
        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/builders/oncourseReferenceDataSet.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
    }

    private <E extends Queueable, S extends ReplicationStub> void testStubBuilder(Class<E> entityClass,
                                                                                  AbstractWillowStubBuilder<E, S> stubBuilder, Long entityId,
                                                                                  String... excludeProperty
    ) {
        ICayenneService cayenneService = getService(ICayenneService.class);
        E entity = Cayenne.objectForPK(cayenneService.sharedContext(), entityClass, entityId);
        StubBuilderTestHelper<E, S> stubBuilderTestHelper = new StubBuilderTestHelper<E, S>(entity,excludeProperty);
        stubBuilderTestHelper.assertStubBuilder(stubBuilder);
    }

    private <E extends Queueable, S extends ReplicationStub> void testStubBuilder(Class<E> entityClass, 
                                                                                  AbstractWillowStubBuilder<E, S> stubBuilder, 
                                                                                  String... excludeProperty
    ) {
        this.testStubBuilder(entityClass,stubBuilder, 1l, excludeProperty);
    }


    @Test
    public void testAttendanceStubBuilder() {
        this.testStubBuilder(Attendance.class, new AttendanceStubBuilder());
    }

    @Test
    public void testBinaryDataStubBuilder() {
        this.testStubBuilder(BinaryData.class, new BinaryDataStubBuilder());
    }

    @Test
    public void testBinaryInfoStubBuilder() {
        this.testStubBuilder(BinaryInfo.class, new BinaryInfoStubBuilder(),"webVisible");
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
        this.testStubBuilder(Enrolment.class, new EnrolmentStubBuilder());
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
        this.testStubBuilder(Invoice.class, new InvoiceStubBuilder(), "status");
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
        /**
         * TODO: The stub builder sets drivingDirectionsTextile to  drivingDirections
         * TODO: The stub builder sets publicTransportDirections_textile to publicTransportDirections
         * TODO: The stub builder sets specialInstructions_textile to specialInstructions
         */
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
}
