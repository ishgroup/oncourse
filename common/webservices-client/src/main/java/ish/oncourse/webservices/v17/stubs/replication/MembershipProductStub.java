
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for membershipProductStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="membershipProductStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v17.soap.webservices.oncourse.ish/}productStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="expiryDays" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="expiryType" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "membershipProductStub", propOrder = {
    "expiryDays",
    "expiryType"
})
public class MembershipProductStub
    extends ProductStub
{

    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer expiryDays;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer expiryType;

    /**
     * Gets the value of the expiryDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getExpiryDays() {
        return expiryDays;
    }

    /**
     * Sets the value of the expiryDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDays(Integer value) {
        this.expiryDays = value;
    }

    /**
     * Gets the value of the expiryType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getExpiryType() {
        return expiryType;
    }

    /**
     * Sets the value of the expiryType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryType(Integer value) {
        this.expiryType = value;
    }

}
