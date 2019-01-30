
package ish.oncourse.webservices.v19.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for discountConcessionTypeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discountConcessionTypeStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v19.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="concessionTypeId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="discountId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discountConcessionTypeStub", propOrder = {
    "concessionTypeId",
    "discountId"
})
public class DiscountConcessionTypeStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long concessionTypeId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long discountId;

    /**
     * Gets the value of the concessionTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getConcessionTypeId() {
        return concessionTypeId;
    }

    /**
     * Sets the value of the concessionTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcessionTypeId(Long value) {
        this.concessionTypeId = value;
    }

    /**
     * Gets the value of the discountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getDiscountId() {
        return discountId;
    }

    /**
     * Sets the value of the discountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountId(Long value) {
        this.discountId = value;
    }

}
