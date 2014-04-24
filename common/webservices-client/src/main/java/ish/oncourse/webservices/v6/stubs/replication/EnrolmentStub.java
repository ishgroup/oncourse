
package ish.oncourse.webservices.v6.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.adapters.StringToIntegerAdapter;
import ish.oncourse.webservices.util.adapters.StringToLongAdapter;


/**
 * <p>Java class for enrolmentStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enrolmentStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="reasonForStudy" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="courseClassId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="invoiceLineId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="feeHelpStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="creditOfferedValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creditProvider" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creditUsedValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creditType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="creditFoeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creditLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="creditProviderType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="feeStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="creditTotal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enrolmentStub", propOrder = {
    "reasonForStudy",
    "source",
    "status",
    "studentId",
    "courseClassId",
    "invoiceLineId",
    "feeHelpStatus",
    "creditOfferedValue",
    "creditProvider",
    "creditUsedValue",
    "creditType",
    "creditFoeId",
    "creditLevel",
    "creditProviderType",
    "feeStatus",
    "creditTotal"
})
public class EnrolmentStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer reasonForStudy;
    @XmlElement(required = true)
    protected String source;
    @XmlElement(required = true)
    protected String status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long studentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long courseClassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long invoiceLineId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer feeHelpStatus;
    @XmlElement(required = true)
    protected String creditOfferedValue;
    @XmlElement(required = true)
    protected String creditProvider;
    @XmlElement(required = true)
    protected String creditUsedValue;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer creditType;
    @XmlElement(required = true)
    protected String creditFoeId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer creditLevel;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer creditProviderType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer feeStatus;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer creditTotal;

    /**
     * Gets the value of the reasonForStudy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getReasonForStudy() {
        return reasonForStudy;
    }

    /**
     * Sets the value of the reasonForStudy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonForStudy(Integer value) {
        this.reasonForStudy = value;
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
    public String getStatus() {
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
    public void setStatus(String value) {
        this.status = value;
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
     * Gets the value of the courseClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCourseClassId() {
        return courseClassId;
    }

    /**
     * Sets the value of the courseClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseClassId(Long value) {
        this.courseClassId = value;
    }

    /**
     * Gets the value of the invoiceLineId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getInvoiceLineId() {
        return invoiceLineId;
    }

    /**
     * Sets the value of the invoiceLineId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceLineId(Long value) {
        this.invoiceLineId = value;
    }

    /**
     * Gets the value of the feeHelpStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getFeeHelpStatus() {
        return feeHelpStatus;
    }

    /**
     * Sets the value of the feeHelpStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeHelpStatus(Integer value) {
        this.feeHelpStatus = value;
    }

    /**
     * Gets the value of the creditOfferedValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditOfferedValue() {
        return creditOfferedValue;
    }

    /**
     * Sets the value of the creditOfferedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditOfferedValue(String value) {
        this.creditOfferedValue = value;
    }

    /**
     * Gets the value of the creditProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditProvider() {
        return creditProvider;
    }

    /**
     * Sets the value of the creditProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditProvider(String value) {
        this.creditProvider = value;
    }

    /**
     * Gets the value of the creditUsedValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditUsedValue() {
        return creditUsedValue;
    }

    /**
     * Sets the value of the creditUsedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditUsedValue(String value) {
        this.creditUsedValue = value;
    }

    /**
     * Gets the value of the creditType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCreditType() {
        return creditType;
    }

    /**
     * Sets the value of the creditType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditType(Integer value) {
        this.creditType = value;
    }

    /**
     * Gets the value of the creditFoeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditFoeId() {
        return creditFoeId;
    }

    /**
     * Sets the value of the creditFoeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditFoeId(String value) {
        this.creditFoeId = value;
    }

    /**
     * Gets the value of the creditLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCreditLevel() {
        return creditLevel;
    }

    /**
     * Sets the value of the creditLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditLevel(Integer value) {
        this.creditLevel = value;
    }

    /**
     * Gets the value of the creditProviderType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCreditProviderType() {
        return creditProviderType;
    }

    /**
     * Sets the value of the creditProviderType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditProviderType(Integer value) {
        this.creditProviderType = value;
    }

    /**
     * Gets the value of the feeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getFeeStatus() {
        return feeStatus;
    }

    /**
     * Sets the value of the feeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeStatus(Integer value) {
        this.feeStatus = value;
    }

    /**
     * Gets the value of the creditTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCreditTotal() {
        return creditTotal;
    }

    /**
     * Sets the value of the creditTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditTotal(Integer value) {
        this.creditTotal = value;
    }

}
