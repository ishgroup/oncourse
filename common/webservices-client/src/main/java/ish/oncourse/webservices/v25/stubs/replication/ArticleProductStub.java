
package ish.oncourse.webservices.v25.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for articleProductStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="articleProductStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v25.soap.webservices.oncourse.ish/}productStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "articleProductStub", propOrder = {
    "weight"
})
public class ArticleProductStub
    extends ProductStub
{

    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double weight;

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeight(Double value) {
        this.weight = value;
    }

}
