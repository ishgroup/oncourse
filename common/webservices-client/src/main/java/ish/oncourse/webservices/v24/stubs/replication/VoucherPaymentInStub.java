
package ish.oncourse.webservices.v24.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for voucherPaymentInStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voucherPaymentInStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v24.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paymentInId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="voucherId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="enrolmentsCount" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="invoiceLineId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voucherPaymentInStub", propOrder = {
    "paymentInId",
    "voucherId",
    "enrolmentsCount",
    "status",
    "invoiceLineId"
})
public class VoucherPaymentInStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long paymentInId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long voucherId;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer enrolmentsCount;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long invoiceLineId;

    /**
     * Gets the value of the paymentInId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getPaymentInId() {
        return paymentInId;
    }

    /**
     * Sets the value of the paymentInId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentInId(Long value) {
        this.paymentInId = value;
    }

    /**
     * Gets the value of the voucherId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getVoucherId() {
        return voucherId;
    }

    /**
     * Sets the value of the voucherId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucherId(Long value) {
        this.voucherId = value;
    }

    /**
     * Gets the value of the enrolmentsCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getEnrolmentsCount() {
        return enrolmentsCount;
    }

    /**
     * Sets the value of the enrolmentsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrolmentsCount(Integer value) {
        this.enrolmentsCount = value;
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

}
