
package ish.oncourse.webservices.v23.stubs.replication;

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
 *         &lt;element name="submittedById" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="tutorComments" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="studentComments" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "submittedById",
    "tutorComments",
    "studentComments"
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
    protected Long submittedById;
    @XmlElement(required = true)
    protected String tutorComments;
    @XmlElement(required = true)
    protected String studentComments;

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
     * Gets the value of the submittedById property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getSubmittedById() {
        return submittedById;
    }

    /**
     * Sets the value of the submittedById property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubmittedById(Long value) {
        this.submittedById = value;
    }

    /**
     * Gets the value of the tutorComments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTutorComments() {
        return tutorComments;
    }

    /**
     * Sets the value of the tutorComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTutorComments(String value) {
        this.tutorComments = value;
    }

    /**
     * Gets the value of the studentComments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentComments() {
        return studentComments;
    }

    /**
     * Sets the value of the studentComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentComments(String value) {
        this.studentComments = value;
    }

}
