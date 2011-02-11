
package ish.oncourse.webservices.v4.stubs.replication;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}attendance"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}binaryData"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}binaryInfo"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}binaryInfoRelation"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}contact"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}course"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseClass"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseModule"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}session"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}sessionTutor"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}tutor"/>
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
@XmlType(name = "replicationResult", propOrder = {
    "attendanceOrBinaryDataOrBinaryInfo"
})
public class ReplicationResult {

    @XmlElements({
        @XmlElement(name = "course", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseStub.class),
        @XmlElement(name = "binaryData", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = BinaryDataStub.class),
        @XmlElement(name = "session", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = SessionStub.class),
        @XmlElement(name = "tutor", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = TutorStub.class),
        @XmlElement(name = "courseClass", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseClassStub.class),
        @XmlElement(name = "binaryInfoRelation", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = BinaryInfoRelationStub.class),
        @XmlElement(name = "binaryInfo", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = BinaryInfoStub.class),
        @XmlElement(name = "contact", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ContactStub.class),
        @XmlElement(name = "attendance", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = AttendanceStub.class),
        @XmlElement(name = "sessionTutor", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = SessionTutorStub.class),
        @XmlElement(name = "courseModule", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseModuleStub.class)
    })
    protected List<ReplicationStub> attendanceOrBinaryDataOrBinaryInfo;

    /**
     * Gets the value of the attendanceOrBinaryDataOrBinaryInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attendanceOrBinaryDataOrBinaryInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttendanceOrBinaryDataOrBinaryInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CourseStub }
     * {@link BinaryDataStub }
     * {@link SessionStub }
     * {@link TutorStub }
     * {@link CourseClassStub }
     * {@link BinaryInfoRelationStub }
     * {@link BinaryInfoStub }
     * {@link ContactStub }
     * {@link AttendanceStub }
     * {@link SessionTutorStub }
     * {@link CourseModuleStub }
     * 
     * 
     */
    public List<ReplicationStub> getAttendanceOrBinaryDataOrBinaryInfo() {
        if (attendanceOrBinaryDataOrBinaryInfo == null) {
            attendanceOrBinaryDataOrBinaryInfo = new ArrayList<ReplicationStub>();
        }
        return this.attendanceOrBinaryDataOrBinaryInfo;
    }

}
