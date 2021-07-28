
package ish.oncourse.webservices.v23.stubs.replication;

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
 * &lt;complexType name="invoiceStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="amountOwing" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="billToAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="customerReference" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="dateDue" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="invoiceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="invoiceNumber" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="publicNotes" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="shippingAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="totalExGst" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="totalGst" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="corporatePassId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="confirmationStatus" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="authorisedRebillingCardId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="allowAutoPay" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invoiceStub", propOrder = {
    "amountOwing",
    "billToAddress",
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
    "corporatePassId",
    "confirmationStatus",
    "authorisedRebillingCardId",
    "type",
    "allowAutoPay",
    "title"
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
    protected String customerReference;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dateDue;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date invoiceDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
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
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long contactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long corporatePassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer confirmationStatus;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long authorisedRebillingCardId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer type;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean allowAutoPay;
    @XmlElement(required = true)
    protected String title;

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

    /**
     * Gets the value of the authorisedRebillingCardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getAuthorisedRebillingCardId() {
        return authorisedRebillingCardId;
    }

    /**
     * Sets the value of the authorisedRebillingCardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorisedRebillingCardId(Long value) {
        this.authorisedRebillingCardId = value;
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
     * Gets the value of the allowAutoPay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isAllowAutoPay() {
        return allowAutoPay;
    }

    /**
     * Sets the value of the allowAutoPay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowAutoPay(Boolean value) {
        this.allowAutoPay = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

}
