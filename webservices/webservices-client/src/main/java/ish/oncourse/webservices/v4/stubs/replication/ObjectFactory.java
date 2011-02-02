
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


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
    private final static QName _Course_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "course");
    private final static QName _CourseClass_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "courseClass");
    private final static QName _SessionTutor_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "sessionTutor");
    private final static QName _CourseModule_QNAME = new QName("http://repl.v4.soap.webservices.oncourse.ish/", "courseModule");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ish.oncourse.webservices.v4.stubs.replication
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CourseClassStub }
     * 
     */
    public CourseClassStub createCourseClassStub() {
        return new CourseClassStub();
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
     * Create an instance of {@link CourseStub }
     * 
     */
    public CourseStub createCourseStub() {
        return new CourseStub();
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
     * Create an instance of {@link SessionStub }
     * 
     */
    public SessionStub createSessionStub() {
        return new SessionStub();
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

}
