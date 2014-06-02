
package ish.oncourse.webservices.v7.stubs.replication;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.GenericInvoiceStub;


/**
 * <p>Java class for invoiceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invoiceStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v7.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="amountOwing" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="billToAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="customerPO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="customerReference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateDue" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="invoiceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="invoiceNumber" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="publicNotes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="shippingAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="totalExGst" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="totalGst" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="corporatePassId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invoiceStub", propOrder = {
    "amountOwing",
    "billToAddress",
    "customerPO",
    "customerReference",
    "dateDue",
    "description",
    "invoiceDate",
    "invoiceNumber",
    "publicNotes",
    "shippingAddress",
    "source",
    "status",
    "totalExGst",
    "totalGst",
    "contactId",
    "corporatePassId"
})
public class InvoiceStub
    extends ReplicationStub
    implements GenericInvoiceStub
{

    @XmlElement(required = true)
    protected BigDecimal amountOwing;
    @XmlElement(required = true)
    protected String billToAddress;
    @XmlElement(required = true)
    protected String customerPO;
    @XmlElement(required = true)
    protected String customerReference;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dateDue;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date invoiceDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long invoiceNumber;
    @XmlElement(required = true)
    protected String publicNotes;
    @XmlElement(required = true)
    protected String shippingAddress;
    @XmlElement(required = true)
    protected String source;
    @XmlElement(required = true)
    protected String status;
    @XmlElement(required = true)
    protected BigDecimal totalExGst;
    @XmlElement(required = true)
    protected BigDecimal totalGst;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long corporatePassId;

    /**
     * Gets the value of the amountOwing property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmountOwing() {
        return amountOwing;
    }

    /**
     * Sets the value of the amountOwing property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmountOwing(BigDecimal value) {
        this.amountOwing = value;
    }

    /**
     * Gets the value of the billToAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillToAddress() {
        return billToAddress;
    }

    /**
     * Sets the value of the billToAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillToAddress(String value) {
        this.billToAddress = value;
    }

    /**
     * Gets the value of the customerPO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerPO() {
        return customerPO;
    }

    /**
     * Sets the value of the customerPO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerPO(String value) {
        this.customerPO = value;
    }

    /**
     * Gets the value of the customerReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerReference() {
        return customerReference;
    }

    /**
     * Sets the value of the customerReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerReference(String value) {
        this.customerReference = value;
    }

    /**
     * Gets the value of the dateDue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDateDue() {
        return dateDue;
    }

    /**
     * Sets the value of the dateDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateDue(Date value) {
        this.dateDue = value;
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
     * Gets the value of the invoiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the value of the invoiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceDate(Date value) {
        this.invoiceDate = value;
    }

    /**
     * Gets the value of the invoiceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the value of the invoiceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceNumber(Long value) {
        this.invoiceNumber = value;
    }

    /**
     * Gets the value of the publicNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicNotes() {
        return publicNotes;
    }

    /**
     * Sets the value of the publicNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicNotes(String value) {
        this.publicNotes = value;
    }

    /**
     * Gets the value of the shippingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the value of the shippingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingAddress(String value) {
        this.shippingAddress = value;
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
     * Gets the value of the totalExGst property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalExGst() {
        return totalExGst;
    }

    /**
     * Sets the value of the totalExGst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalExGst(BigDecimal value) {
        this.totalExGst = value;
    }

    /**
     * Gets the value of the totalGst property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalGst() {
        return totalGst;
    }

    /**
     * Sets the value of the totalGst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalGst(BigDecimal value) {
        this.totalGst = value;
    }

    /**
     * Gets the value of the contactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactId() {
        return contactId;
    }

    /**
     * Sets the value of the contactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactId(Long value) {
        this.contactId = value;
    }

    /**
     * Gets the value of the corporatePassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCorporatePassId() {
        return corporatePassId;
    }

    /**
     * Sets the value of the corporatePassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorporatePassId(Long value) {
        this.corporatePassId = value;
    }

}
