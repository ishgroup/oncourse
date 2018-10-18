
package ish.oncourse.webservices.v18.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for contactRelationTypeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactRelationTypeStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v18.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fromContactName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="toContactName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="delegatedAccessToContact" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactRelationTypeStub", propOrder = {
    "fromContactName",
    "toContactName",
    "delegatedAccessToContact"
})
public class ContactRelationTypeStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String fromContactName;
    @XmlElement(required = true)
    protected String toContactName;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer delegatedAccessToContact;

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

    /**
     * Gets the value of the delegatedAccessToContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getDelegatedAccessToContact() {
        return delegatedAccessToContact;
    }

    /**
     * Sets the value of the delegatedAccessToContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelegatedAccessToContact(Integer value) {
        this.delegatedAccessToContact = value;
    }

}
