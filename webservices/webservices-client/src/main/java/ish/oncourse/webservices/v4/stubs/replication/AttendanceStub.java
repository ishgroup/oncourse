
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for attendanceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attendanceStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="attendanceType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="marker" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *         &lt;element name="session" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *         &lt;element name="student" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attendanceStub", propOrder = {
    "attendanceType",
    "marker",
    "session",
    "student"
})
public class AttendanceStub
    extends ReplicationStub
{

    protected int attendanceType;
    @XmlElement(required = true)
    protected ReplicationStub marker;
    @XmlElement(required = true)
    protected ReplicationStub session;
    @XmlElement(required = true)
    protected ReplicationStub student;

    /**
     * Gets the value of the attendanceType property.
     * 
     */
    public int getAttendanceType() {
        return attendanceType;
    }

    /**
     * Sets the value of the attendanceType property.
     * 
     */
    public void setAttendanceType(int value) {
        this.attendanceType = value;
    }

    /**
     * Gets the value of the marker property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationStub }
     *     
     */
    public ReplicationStub getMarker() {
        return marker;
    }

    /**
     * Sets the value of the marker property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationStub }
     *     
     */
    public void setMarker(ReplicationStub value) {
        this.marker = value;
    }

    /**
     * Gets the value of the session property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationStub }
     *     
     */
    public ReplicationStub getSession() {
        return session;
    }

    /**
     * Sets the value of the session property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationStub }
     *     
     */
    public void setSession(ReplicationStub value) {
        this.session = value;
    }

    /**
     * Gets the value of the student property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationStub }
     *     
     */
    public ReplicationStub getStudent() {
        return student;
    }

    /**
     * Sets the value of the student property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationStub }
     *     
     */
    public void setStudent(ReplicationStub value) {
        this.student = value;
    }

}
