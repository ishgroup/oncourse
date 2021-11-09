
package ish.oncourse.webservices.v25.stubs.replication;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for productStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="productStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v25.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="incomeAccountId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="isOnSale" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="isWebVisible" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="priceExTax" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="taxAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="taxAdjustment" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="taxId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fieldConfigurationSchemeId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productStub", propOrder = {
    "sku",
    "description",
    "incomeAccountId",
    "isOnSale",
    "isWebVisible",
    "name",
    "notes",
    "priceExTax",
    "taxAmount",
    "taxAdjustment",
    "taxId",
    "type",
    "fieldConfigurationSchemeId"
})
@XmlSeeAlso({
    ArticleProductStub.class,
    MembershipProductStub.class,
    VoucherProductStub.class
})
public class ProductStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String sku;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long incomeAccountId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isOnSale;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isWebVisible;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String notes;
    @XmlElement(required = true)
    protected BigDecimal priceExTax;
    @XmlElement(required = true)
    protected BigDecimal taxAmount;
    @XmlElement(required = true)
    protected BigDecimal taxAdjustment;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long taxId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer type;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long fieldConfigurationSchemeId;

    /**
     * Gets the value of the sku property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the value of the sku property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSku(String value) {
        this.sku = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the incomeAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getIncomeAccountId() {
        return incomeAccountId;
    }

    /**
     * Sets the value of the incomeAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncomeAccountId(Long value) {
        this.incomeAccountId = value;
    }

    /**
     * Gets the value of the isOnSale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsOnSale() {
        return isOnSale;
    }

    /**
     * Sets the value of the isOnSale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsOnSale(Boolean value) {
        this.isOnSale = value;
    }

    /**
     * Gets the value of the isWebVisible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsWebVisible() {
        return isWebVisible;
    }

    /**
     * Sets the value of the isWebVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsWebVisible(Boolean value) {
        this.isWebVisible = value;
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
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the priceExTax property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPriceExTax() {
        return priceExTax;
    }

    /**
     * Sets the value of the priceExTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPriceExTax(BigDecimal value) {
        this.priceExTax = value;
    }

    /**
     * Gets the value of the taxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the value of the taxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTaxAmount(BigDecimal value) {
        this.taxAmount = value;
    }

    /**
     * Gets the value of the taxAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTaxAdjustment() {
        return taxAdjustment;
    }

    /**
     * Sets the value of the taxAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTaxAdjustment(BigDecimal value) {
        this.taxAdjustment = value;
    }

    /**
     * Gets the value of the taxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTaxId() {
        return taxId;
    }

    /**
     * Sets the value of the taxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxId(Long value) {
        this.taxId = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(Integer value) {
        this.type = value;
    }

    /**
     * Gets the value of the fieldConfigurationSchemeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getFieldConfigurationSchemeId() {
        return fieldConfigurationSchemeId;
    }

    /**
     * Sets the value of the fieldConfigurationSchemeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldConfigurationSchemeId(Long value) {
        this.fieldConfigurationSchemeId = value;
    }

}
