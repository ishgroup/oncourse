
package ish.oncourse.webservices.v8.stubs.replication;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for discountStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discountStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v8.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="combinationType" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="detail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="discountAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="discountRate" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="maximumDiscount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="minimumDiscount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="roundingMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="studentAge" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="studentAgeOperator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="studentEnrolledWithinDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="studentPostcodes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="validFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="validTo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="discountType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="hideOnWeb" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="availableOnWeb" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discountStub", propOrder = {
    "code",
    "combinationType",
    "detail",
    "discountAmount",
    "discountRate",
    "maximumDiscount",
    "minimumDiscount",
    "name",
    "roundingMode",
    "studentAge",
    "studentAgeOperator",
    "studentEnrolledWithinDays",
    "studentPostcodes",
    "validFrom",
    "validTo",
    "discountType",
    "hideOnWeb",
    "availableOnWeb"
})
public class DiscountStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String code;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean combinationType;
    @XmlElement(required = true)
    protected String detail;
    @XmlElement(required = true)
    protected BigDecimal discountAmount;
    @XmlElement(required = true)
    protected BigDecimal discountRate;
    @XmlElement(required = true)
    protected BigDecimal maximumDiscount;
    @XmlElement(required = true)
    protected BigDecimal minimumDiscount;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer roundingMode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer studentAge;
    @XmlElement(required = true)
    protected String studentAgeOperator;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer studentEnrolledWithinDays;
    @XmlElement(required = true)
    protected String studentPostcodes;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date validFrom;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date validTo;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer discountType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean hideOnWeb;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean availableOnWeb;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the combinationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isCombinationType() {
        return combinationType;
    }

    /**
     * Sets the value of the combinationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCombinationType(Boolean value) {
        this.combinationType = value;
    }

    /**
     * Gets the value of the detail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetail(String value) {
        this.detail = value;
    }

    /**
     * Gets the value of the discountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the value of the discountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDiscountAmount(BigDecimal value) {
        this.discountAmount = value;
    }

    /**
     * Gets the value of the discountRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    /**
     * Sets the value of the discountRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDiscountRate(BigDecimal value) {
        this.discountRate = value;
    }

    /**
     * Gets the value of the maximumDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMaximumDiscount() {
        return maximumDiscount;
    }

    /**
     * Sets the value of the maximumDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMaximumDiscount(BigDecimal value) {
        this.maximumDiscount = value;
    }

    /**
     * Gets the value of the minimumDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMinimumDiscount() {
        return minimumDiscount;
    }

    /**
     * Sets the value of the minimumDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMinimumDiscount(BigDecimal value) {
        this.minimumDiscount = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the roundingMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getRoundingMode() {
        return roundingMode;
    }

    /**
     * Sets the value of the roundingMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoundingMode(Integer value) {
        this.roundingMode = value;
    }

    /**
     * Gets the value of the studentAge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getStudentAge() {
        return studentAge;
    }

    /**
     * Sets the value of the studentAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentAge(Integer value) {
        this.studentAge = value;
    }

    /**
     * Gets the value of the studentAgeOperator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentAgeOperator() {
        return studentAgeOperator;
    }

    /**
     * Sets the value of the studentAgeOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentAgeOperator(String value) {
        this.studentAgeOperator = value;
    }

    /**
     * Gets the value of the studentEnrolledWithinDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getStudentEnrolledWithinDays() {
        return studentEnrolledWithinDays;
    }

    /**
     * Sets the value of the studentEnrolledWithinDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentEnrolledWithinDays(Integer value) {
        this.studentEnrolledWithinDays = value;
    }

    /**
     * Gets the value of the studentPostcodes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentPostcodes() {
        return studentPostcodes;
    }

    /**
     * Sets the value of the studentPostcodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentPostcodes(String value) {
        this.studentPostcodes = value;
    }

    /**
     * Gets the value of the validFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidFrom(Date value) {
        this.validFrom = value;
    }

    /**
     * Gets the value of the validTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Sets the value of the validTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidTo(Date value) {
        this.validTo = value;
    }

    /**
     * Gets the value of the discountType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getDiscountType() {
        return discountType;
    }

    /**
     * Sets the value of the discountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountType(Integer value) {
        this.discountType = value;
    }

    /**
     * Gets the value of the hideOnWeb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isHideOnWeb() {
        return hideOnWeb;
    }

    /**
     * Sets the value of the hideOnWeb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHideOnWeb(Boolean value) {
        this.hideOnWeb = value;
    }

    /**
     * Gets the value of the availableOnWeb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isAvailableOnWeb() {
        return availableOnWeb;
    }

    /**
     * Sets the value of the availableOnWeb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvailableOnWeb(Boolean value) {
        this.availableOnWeb = value;
    }

}
