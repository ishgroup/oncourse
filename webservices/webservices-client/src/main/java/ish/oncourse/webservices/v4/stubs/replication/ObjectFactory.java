
package ish.oncourse.webservices.v4.stubs.replication;

import java.util.Date;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3._2001.xmlschema.Adapter1;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ish.oncourse.webservices.v4.stubs.replication package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Session_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "session");
    private final static QName _Attendance_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "attendance");
    private final static QName _Tutor_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "tutor");
    private final static QName _BinaryInfo_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "binaryInfo");
    private final static QName _Course_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "course");
    private final static QName _CourseClass_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "courseClass");
    private final static QName _BinaryData_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "binaryData");
    private final static QName _Contact_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "contact");
    private final static QName _SessionTutor_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "sessionTutor");
    private final static QName _CourseModule_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "courseModule");
    private final static QName _ContactStubDateOfBirth_QNAME = new QName("", "dateOfBirth");
    private final static QName _ContactStubStreet_QNAME = new QName("", "street");
    private final static QName _ContactStubFamilyName_QNAME = new QName("", "familyName");
    private final static QName _ContactStubState_QNAME = new QName("", "state");
    private final static QName _ContactStubCollege_QNAME = new QName("", "college");
    private final static QName _ContactStubIsMarketingViaEmailAllowed_QNAME = new QName("", "isMarketingViaEmailAllowed");
    private final static QName _ContactStubGivenName_QNAME = new QName("", "givenName");
    private final static QName _ContactStubHomePhoneNumber_QNAME = new QName("", "homePhoneNumber");
    private final static QName _ContactStubPassword_QNAME = new QName("", "password");
    private final static QName _ContactStubMobilePhoneNumber_QNAME = new QName("", "mobilePhoneNumber");
    private final static QName _ContactStubIsMale_QNAME = new QName("", "isMale");
    private final static QName _ContactStubPasswordHash_QNAME = new QName("", "passwordHash");
    private final static QName _ContactStubFaxNumber_QNAME = new QName("", "faxNumber");
    private final static QName _ContactStubStudent_QNAME = new QName("", "student");
    private final static QName _ContactStubCreated_QNAME = new QName("", "created");
    private final static QName _ContactStubSuburb_QNAME = new QName("", "suburb");
    private final static QName _ContactStubTutor_QNAME = new QName("", "tutor");
    private final static QName _ContactStubIsMarketingViaSMSAllowed_QNAME = new QName("", "isMarketingViaSMSAllowed");
    private final static QName _ContactStubCountryId_QNAME = new QName("", "countryId");
    private final static QName _ContactStubCookieHash_QNAME = new QName("", "cookieHash");
    private final static QName _ContactStubEmailAddress_QNAME = new QName("", "emailAddress");
    private final static QName _ContactStubPostcode_QNAME = new QName("", "postcode");
    private final static QName _ContactStubIsCompany_QNAME = new QName("", "isCompany");
    private final static QName _ContactStubModified_QNAME = new QName("", "modified");
    private final static QName _ContactStubBusinessPhoneNumber_QNAME = new QName("", "businessPhoneNumber");
    private final static QName _ContactStubTaxFileNumber_QNAME = new QName("", "taxFileNumber");
    private final static QName _ContactStubIsMarketingViaPostAllowed_QNAME = new QName("", "isMarketingViaPostAllowed");
    private final static QName _ContactStubUniqueCode_QNAME = new QName("", "uniqueCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ish.oncourse.webservices.v4.stubs.replication
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContactStub }
     * 
     */
    public ContactStub createContactStub() {
        return new ContactStub();
    }

    /**
     * Create an instance of {@link SessionStub }
     * 
     */
    public SessionStub createSessionStub() {
        return new SessionStub();
    }

    /**
     * Create an instance of {@link CourseClassStub }
     * 
     */
    public CourseClassStub createCourseClassStub() {
        return new CourseClassStub();
    }

    /**
     * Create an instance of {@link TutorStub }
     * 
     */
    public TutorStub createTutorStub() {
        return new TutorStub();
    }

    /**
     * Create an instance of {@link ReplicationResult }
     * 
     */
    public ReplicationResult createReplicationResult() {
        return new ReplicationResult();
    }

    /**
     * Create an instance of {@link CourseModuleStub }
     * 
     */
    public CourseModuleStub createCourseModuleStub() {
        return new CourseModuleStub();
    }

    /**
     * Create an instance of {@link ReplicationRequest }
     * 
     */
    public ReplicationRequest createReplicationRequest() {
        return new ReplicationRequest();
    }

    /**
     * Create an instance of {@link SessionTutorStub }
     * 
     */
    public SessionTutorStub createSessionTutorStub() {
        return new SessionTutorStub();
    }

    /**
     * Create an instance of {@link AttendanceStub }
     * 
     */
    public AttendanceStub createAttendanceStub() {
        return new AttendanceStub();
    }

    /**
     * Create an instance of {@link CourseStub }
     * 
     */
    public CourseStub createCourseStub() {
        return new CourseStub();
    }

    /**
     * Create an instance of {@link BinaryDataStub }
     * 
     */
    public BinaryDataStub createBinaryDataStub() {
        return new BinaryDataStub();
    }

    /**
     * Create an instance of {@link BinaryInfoStub }
     * 
     */
    public BinaryInfoStub createBinaryInfoStub() {
        return new BinaryInfoStub();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "session")
    public JAXBElement<SessionStub> createSession(SessionStub value) {
        return new JAXBElement<SessionStub>(_Session_QNAME, SessionStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttendanceStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "attendance")
    public JAXBElement<AttendanceStub> createAttendance(AttendanceStub value) {
        return new JAXBElement<AttendanceStub>(_Attendance_QNAME, AttendanceStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TutorStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "tutor")
    public JAXBElement<TutorStub> createTutor(TutorStub value) {
        return new JAXBElement<TutorStub>(_Tutor_QNAME, TutorStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryInfoStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "binaryInfo")
    public JAXBElement<BinaryInfoStub> createBinaryInfo(BinaryInfoStub value) {
        return new JAXBElement<BinaryInfoStub>(_BinaryInfo_QNAME, BinaryInfoStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CourseStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "course")
    public JAXBElement<CourseStub> createCourse(CourseStub value) {
        return new JAXBElement<CourseStub>(_Course_QNAME, CourseStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CourseClassStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "courseClass")
    public JAXBElement<CourseClassStub> createCourseClass(CourseClassStub value) {
        return new JAXBElement<CourseClassStub>(_CourseClass_QNAME, CourseClassStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryDataStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "binaryData")
    public JAXBElement<BinaryDataStub> createBinaryData(BinaryDataStub value) {
        return new JAXBElement<BinaryDataStub>(_BinaryData_QNAME, BinaryDataStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "contact")
    public JAXBElement<ContactStub> createContact(ContactStub value) {
        return new JAXBElement<ContactStub>(_Contact_QNAME, ContactStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionTutorStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "sessionTutor")
    public JAXBElement<SessionTutorStub> createSessionTutor(SessionTutorStub value) {
        return new JAXBElement<SessionTutorStub>(_SessionTutor_QNAME, SessionTutorStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CourseModuleStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "courseModule")
    public JAXBElement<CourseModuleStub> createCourseModule(CourseModuleStub value) {
        return new JAXBElement<CourseModuleStub>(_CourseModule_QNAME, CourseModuleStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Date }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "dateOfBirth", scope = ContactStub.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    public JAXBElement<Date> createContactStubDateOfBirth(Date value) {
        return new JAXBElement<Date>(_ContactStubDateOfBirth_QNAME, Date.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "street", scope = ContactStub.class)
    public JAXBElement<String> createContactStubStreet(String value) {
        return new JAXBElement<String>(_ContactStubStreet_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "familyName", scope = ContactStub.class)
    public JAXBElement<String> createContactStubFamilyName(String value) {
        return new JAXBElement<String>(_ContactStubFamilyName_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "state", scope = ContactStub.class)
    public JAXBElement<String> createContactStubState(String value) {
        return new JAXBElement<String>(_ContactStubState_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReplicationStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "college", scope = ContactStub.class)
    public JAXBElement<ReplicationStub> createContactStubCollege(ReplicationStub value) {
        return new JAXBElement<ReplicationStub>(_ContactStubCollege_QNAME, ReplicationStub.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "isMarketingViaEmailAllowed", scope = ContactStub.class)
    public JAXBElement<Boolean> createContactStubIsMarketingViaEmailAllowed(Boolean value) {
        return new JAXBElement<Boolean>(_ContactStubIsMarketingViaEmailAllowed_QNAME, Boolean.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "givenName", scope = ContactStub.class)
    public JAXBElement<String> createContactStubGivenName(String value) {
        return new JAXBElement<String>(_ContactStubGivenName_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "homePhoneNumber", scope = ContactStub.class)
    public JAXBElement<String> createContactStubHomePhoneNumber(String value) {
        return new JAXBElement<String>(_ContactStubHomePhoneNumber_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "password", scope = ContactStub.class)
    public JAXBElement<String> createContactStubPassword(String value) {
        return new JAXBElement<String>(_ContactStubPassword_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "mobilePhoneNumber", scope = ContactStub.class)
    public JAXBElement<String> createContactStubMobilePhoneNumber(String value) {
        return new JAXBElement<String>(_ContactStubMobilePhoneNumber_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "isMale", scope = ContactStub.class)
    public JAXBElement<Boolean> createContactStubIsMale(Boolean value) {
        return new JAXBElement<Boolean>(_ContactStubIsMale_QNAME, Boolean.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "passwordHash", scope = ContactStub.class)
    public JAXBElement<String> createContactStubPasswordHash(String value) {
        return new JAXBElement<String>(_ContactStubPasswordHash_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "faxNumber", scope = ContactStub.class)
    public JAXBElement<String> createContactStubFaxNumber(String value) {
        return new JAXBElement<String>(_ContactStubFaxNumber_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReplicationStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "student", scope = ContactStub.class)
    public JAXBElement<ReplicationStub> createContactStubStudent(ReplicationStub value) {
        return new JAXBElement<ReplicationStub>(_ContactStubStudent_QNAME, ReplicationStub.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Date }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "created", scope = ContactStub.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    public JAXBElement<Date> createContactStubCreated(Date value) {
        return new JAXBElement<Date>(_ContactStubCreated_QNAME, Date.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "suburb", scope = ContactStub.class)
    public JAXBElement<String> createContactStubSuburb(String value) {
        return new JAXBElement<String>(_ContactStubSuburb_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReplicationStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tutor", scope = ContactStub.class)
    public JAXBElement<ReplicationStub> createContactStubTutor(ReplicationStub value) {
        return new JAXBElement<ReplicationStub>(_ContactStubTutor_QNAME, ReplicationStub.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "isMarketingViaSMSAllowed", scope = ContactStub.class)
    public JAXBElement<Boolean> createContactStubIsMarketingViaSMSAllowed(Boolean value) {
        return new JAXBElement<Boolean>(_ContactStubIsMarketingViaSMSAllowed_QNAME, Boolean.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "countryId", scope = ContactStub.class)
    public JAXBElement<Long> createContactStubCountryId(Long value) {
        return new JAXBElement<Long>(_ContactStubCountryId_QNAME, Long.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "cookieHash", scope = ContactStub.class)
    public JAXBElement<String> createContactStubCookieHash(String value) {
        return new JAXBElement<String>(_ContactStubCookieHash_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "emailAddress", scope = ContactStub.class)
    public JAXBElement<String> createContactStubEmailAddress(String value) {
        return new JAXBElement<String>(_ContactStubEmailAddress_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "postcode", scope = ContactStub.class)
    public JAXBElement<String> createContactStubPostcode(String value) {
        return new JAXBElement<String>(_ContactStubPostcode_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "isCompany", scope = ContactStub.class)
    public JAXBElement<Boolean> createContactStubIsCompany(Boolean value) {
        return new JAXBElement<Boolean>(_ContactStubIsCompany_QNAME, Boolean.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Date }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "modified", scope = ContactStub.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    public JAXBElement<Date> createContactStubModified(Date value) {
        return new JAXBElement<Date>(_ContactStubModified_QNAME, Date.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "businessPhoneNumber", scope = ContactStub.class)
    public JAXBElement<String> createContactStubBusinessPhoneNumber(String value) {
        return new JAXBElement<String>(_ContactStubBusinessPhoneNumber_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "taxFileNumber", scope = ContactStub.class)
    public JAXBElement<String> createContactStubTaxFileNumber(String value) {
        return new JAXBElement<String>(_ContactStubTaxFileNumber_QNAME, String.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "isMarketingViaPostAllowed", scope = ContactStub.class)
    public JAXBElement<Boolean> createContactStubIsMarketingViaPostAllowed(Boolean value) {
        return new JAXBElement<Boolean>(_ContactStubIsMarketingViaPostAllowed_QNAME, Boolean.class, ContactStub.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "uniqueCode", scope = ContactStub.class)
    public JAXBElement<String> createContactStubUniqueCode(String value) {
        return new JAXBElement<String>(_ContactStubUniqueCode_QNAME, String.class, ContactStub.class, value);
    }

}
