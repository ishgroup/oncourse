
package ish.oncourse.webservices.v24.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for tutorAttendanceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tutorAttendanceStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v24.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="attendanceType" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="tutorId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="durationMinutes" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="note" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tutorAttendanceStub", propOrder = {
    "endDate",
    "startDate",
    "attendanceType",
    "sessionId",
    "tutorId",
    "durationMinutes",
    "note"
})
public class TutorAttendanceStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
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
    protected Long tutorId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer durationMinutes;
    @XmlElement(required = true)
    protected String note;

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(Date value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(Date value) {
        this.startDate = value;
    }

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
     * Gets the value of the tutorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTutorId() {
        return tutorId;
    }

    /**
     * Sets the value of the tutorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTutorId(Long value) {
        this.tutorId = value;
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

}
