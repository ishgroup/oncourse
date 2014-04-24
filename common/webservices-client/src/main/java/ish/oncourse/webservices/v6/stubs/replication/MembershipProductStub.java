
package ish.oncourse.webservices.v6.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter3;


/**
 * <p>Java class for membershipProductStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="membershipProductStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}productStub">
 *       &lt;sequence>
 *         &lt;element name="expiryDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="expiryType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer expiryDays;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
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
