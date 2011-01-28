
package ish.oncourse.webservices.v4.stubs.replication;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}course"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseClass"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseModule"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}session"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}sessionTutor"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationRequest", propOrder = {
    "courseOrCourseClassOrCourseModule"
})
public class ReplicationRequest {

    @XmlElements({
        @XmlElement(name = "sessionTutor", type = SessionTutorStub.class),
        @XmlElement(name = "courseModule", type = CourseModuleStub.class),
        @XmlElement(name = "courseClass", type = CourseClassStub.class),
        @XmlElement(name = "course", type = CourseStub.class),
        @XmlElement(name = "session", type = SessionStub.class)
    })
    protected List<SoapStub> courseOrCourseClassOrCourseModule;

    /**
     * Gets the value of the courseOrCourseClassOrCourseModule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the courseOrCourseClassOrCourseModule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCourseOrCourseClassOrCourseModule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SessionTutorStub }
     * {@link CourseModuleStub }
     * {@link CourseClassStub }
     * {@link CourseStub }
     * {@link SessionStub }
     * 
     * 
     */
    public List<SoapStub> getCourseOrCourseClassOrCourseModule() {
        if (courseOrCourseClassOrCourseModule == null) {
            courseOrCourseClassOrCourseModule = new ArrayList<SoapStub>();
        }
        return this.courseOrCourseClassOrCourseModule;
    }

}
