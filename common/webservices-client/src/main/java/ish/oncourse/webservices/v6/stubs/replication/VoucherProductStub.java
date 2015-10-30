
package ish.oncourse.webservices.v6.stubs.replication;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for voucherProductStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voucherProductStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}productStub">
 *       &lt;sequence>
 *         &lt;element name="expiryDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="expiryType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="maxCoursesRedemption" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voucherProductStub", propOrder = {
    "expiryDays",
    "expiryType",
    "value",
    "maxCoursesRedemption"
})
public class VoucherProductStub
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
    @XmlElement(required = true, nillable = true)
    protected BigDecimal value;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer maxCoursesRedemption;

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

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the maxCoursesRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getMaxCoursesRedemption() {
        return maxCoursesRedemption;
    }

    /**
     * Sets the value of the maxCoursesRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxCoursesRedemption(Integer value) {
        this.maxCoursesRedemption = value;
    }

}
