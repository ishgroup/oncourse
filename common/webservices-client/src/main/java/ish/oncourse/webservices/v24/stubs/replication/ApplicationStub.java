
package ish.oncourse.webservices.v24.stubs.replication;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for applicationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="applicationStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v24.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="courseId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="enrolBy" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="feeOverride" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="confirmationStatus" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "applicationStub", propOrder = {
    "courseId",
    "studentId",
    "source",
    "status",
    "enrolBy",
    "feeOverride",
    "reason",
    "confirmationStatus"
})
public class ApplicationStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long courseId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long studentId;
    @XmlElement(required = true)
    protected String source;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date enrolBy;
    @XmlElement(required = true)
    protected BigDecimal feeOverride;
    @XmlElement(required = true)
    protected String reason;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer confirmationStatus;

    /**
     * Gets the value of the courseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseId(Long value) {
        this.courseId = value;
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
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

    /**
     * Gets the value of the enrolBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getEnrolBy() {
        return enrolBy;
    }

    /**
     * Sets the value of the enrolBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrolBy(Date value) {
        this.enrolBy = value;
    }

    /**
     * Gets the value of the feeOverride property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFeeOverride() {
        return feeOverride;
    }

    /**
     * Sets the value of the feeOverride property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFeeOverride(BigDecimal value) {
        this.feeOverride = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the confirmationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getConfirmationStatus() {
        return confirmationStatus;
    }

    /**
     * Sets the value of the confirmationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationStatus(Integer value) {
        this.confirmationStatus = value;
    }

}
