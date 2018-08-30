
package ish.oncourse.webservices.v13.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for attendanceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attendanceStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v13.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="attendanceType" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="durationMinutes" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="markedByTutorId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="markedByTutorDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attendanceStub", propOrder = {
    "attendanceType",
    "sessionId",
    "studentId",
    "durationMinutes",
    "note",
    "markedByTutorId",
    "markedByTutorDate"
})
public class AttendanceStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer attendanceType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long sessionId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long studentId;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer durationMinutes;
    protected String note;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long markedByTutorId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date markedByTutorDate;

    /**
     * Gets the value of the attendanceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getAttendanceType() {
        return attendanceType;
    }

    /**
     * Sets the value of the attendanceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttendanceType(Integer value) {
        this.attendanceType = value;
    }

    /**
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(Long value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the studentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Sets the value of the studentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentId(Long value) {
        this.studentId = value;
    }

    /**
     * Gets the value of the durationMinutes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Sets the value of the durationMinutes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDurationMinutes(Integer value) {
        this.durationMinutes = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the markedByTutorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getMarkedByTutorId() {
        return markedByTutorId;
    }

    /**
     * Sets the value of the markedByTutorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarkedByTutorId(Long value) {
        this.markedByTutorId = value;
    }

    /**
     * Gets the value of the markedByTutorDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getMarkedByTutorDate() {
        return markedByTutorDate;
    }

    /**
     * Sets the value of the markedByTutorDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarkedByTutorDate(Date value) {
        this.markedByTutorDate = value;
    }

}
