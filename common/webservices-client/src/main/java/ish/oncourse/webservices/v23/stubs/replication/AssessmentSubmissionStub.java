
package ish.oncourse.webservices.v23.stubs.replication;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for assessmentSubmissionStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assessmentSubmissionStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="enrolmentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="assessmentClassId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="markedById" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="submittedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="markedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="grade" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assessmentSubmissionStub", propOrder = {
    "enrolmentId",
    "assessmentClassId",
    "markedById",
    "submittedOn",
    "markedOn",
    "grade"
})
public class AssessmentSubmissionStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long enrolmentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long assessmentClassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long markedById;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date submittedOn;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date markedOn;
    @XmlElement(required = true)
    protected BigDecimal grade;

    /**
     * Gets the value of the enrolmentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getEnrolmentId() {
        return enrolmentId;
    }

    /**
     * Sets the value of the enrolmentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrolmentId(Long value) {
        this.enrolmentId = value;
    }

    /**
     * Gets the value of the assessmentClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getAssessmentClassId() {
        return assessmentClassId;
    }

    /**
     * Sets the value of the assessmentClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssessmentClassId(Long value) {
        this.assessmentClassId = value;
    }

    /**
     * Gets the value of the markedById property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getMarkedById() {
        return markedById;
    }

    /**
     * Sets the value of the markedById property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarkedById(Long value) {
        this.markedById = value;
    }

    /**
     * Gets the value of the submittedOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getSubmittedOn() {
        return submittedOn;
    }

    /**
     * Sets the value of the submittedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubmittedOn(Date value) {
        this.submittedOn = value;
    }

    /**
     * Gets the value of the markedOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getMarkedOn() {
        return markedOn;
    }

    /**
     * Sets the value of the markedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarkedOn(Date value) {
        this.markedOn = value;
    }

    /**
     * Gets the value of the grade property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getGrade() {
        return grade;
    }

    /**
     * Sets the value of the grade property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setGrade(BigDecimal value) {
        this.grade = value;
    }

}
