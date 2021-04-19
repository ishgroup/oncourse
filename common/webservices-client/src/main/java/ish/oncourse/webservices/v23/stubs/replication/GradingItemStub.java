
package ish.oncourse.webservices.v23.stubs.replication;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for gradingItemStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gradingItemStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="lowerBound" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="gradingTypeId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gradingItemStub", propOrder = {
    "name",
    "lowerBound",
    "gradingTypeId"
})
public class GradingItemStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected BigDecimal lowerBound;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long gradingTypeId;

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
     * Gets the value of the lowerBound property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLowerBound() {
        return lowerBound;
    }

    /**
     * Sets the value of the lowerBound property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLowerBound(BigDecimal value) {
        this.lowerBound = value;
    }

    /**
     * Gets the value of the gradingTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getGradingTypeId() {
        return gradingTypeId;
    }

    /**
     * Sets the value of the gradingTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGradingTypeId(Long value) {
        this.gradingTypeId = value;
    }

}
