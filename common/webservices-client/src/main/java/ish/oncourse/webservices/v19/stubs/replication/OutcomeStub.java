
package ish.oncourse.webservices.v19.stubs.replication;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for outcomeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="outcomeStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v19.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="deliveryMode" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fundingSource" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="reportableHours" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="hoursAttended" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="enrolmentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="moduleId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="priorLearningId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="markedByTutorId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="markedByTutorDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="modifiedByUserId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="modifiedByUserDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "outcomeStub", propOrder = {
    "deliveryMode",
    "fundingSource",
    "reportableHours",
    "hoursAttended",
    "enrolmentId",
    "moduleId",
    "startDate",
    "endDate",
    "status",
    "priorLearningId",
    "markedByTutorId",
    "markedByTutorDate",
    "modifiedByUserId",
    "modifiedByUserDateTime"
})
public class OutcomeStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer deliveryMode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer fundingSource;
    @XmlElement(required = true)
    protected BigDecimal reportableHours;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer hoursAttended;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long enrolmentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long moduleId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long priorLearningId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long markedByTutorId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date markedByTutorDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long modifiedByUserId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date modifiedByUserDateTime;

    /**
     * Gets the value of the deliveryMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getDeliveryMode() {
        return deliveryMode;
    }

    /**
     * Sets the value of the deliveryMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryMode(Integer value) {
        this.deliveryMode = value;
    }

    /**
     * Gets the value of the fundingSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getFundingSource() {
        return fundingSource;
    }

    /**
     * Sets the value of the fundingSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFundingSource(Integer value) {
        this.fundingSource = value;
    }

    /**
     * Gets the value of the reportableHours property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getReportableHours() {
        return reportableHours;
    }

    /**
     * Sets the value of the reportableHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setReportableHours(BigDecimal value) {
        this.reportableHours = value;
    }

    /**
     * Gets the value of the hoursAttended property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getHoursAttended() {
        return hoursAttended;
    }

    /**
     * Sets the value of the hoursAttended property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoursAttended(Integer value) {
        this.hoursAttended = value;
    }

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
     * Gets the value of the moduleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getModuleId() {
        return moduleId;
    }

    /**
     * Sets the value of the moduleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModuleId(Long value) {
        this.moduleId = value;
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
     * Gets the value of the priorLearningId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getPriorLearningId() {
        return priorLearningId;
    }

    /**
     * Sets the value of the priorLearningId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorLearningId(Long value) {
        this.priorLearningId = value;
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

    /**
     * Gets the value of the modifiedByUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getModifiedByUserId() {
        return modifiedByUserId;
    }

    /**
     * Sets the value of the modifiedByUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedByUserId(Long value) {
        this.modifiedByUserId = value;
    }

    /**
     * Gets the value of the modifiedByUserDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getModifiedByUserDateTime() {
        return modifiedByUserDateTime;
    }

    /**
     * Sets the value of the modifiedByUserDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedByUserDateTime(Date value) {
        this.modifiedByUserDateTime = value;
    }

}
