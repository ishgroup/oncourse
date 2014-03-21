
package ish.oncourse.webservices.v5.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for contactRelationTypeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactRelationTypeStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v5.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="fromContactName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="toContactName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactRelationTypeStub", propOrder = {
    "fromContactName",
    "toContactName"
})
public class ContactRelationTypeStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String fromContactName;
    @XmlElement(required = true)
    protected String toContactName;

    /**
     * Gets the value of the fromContactName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromContactName() {
        return fromContactName;
    }

    /**
     * Sets the value of the fromContactName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromContactName(String value) {
        this.fromContactName = value;
    }

    /**
     * Gets the value of the toContactName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToContactName() {
        return toContactName;
    }

    /**
     * Sets the value of the toContactName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToContactName(String value) {
        this.toContactName = value;
    }

}
