
package ish.oncourse.webservices.v22.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for contactRelationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactRelationStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v22.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fromContactId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="toContactId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="typeId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactRelationStub", propOrder = {
    "fromContactId",
    "toContactId",
    "typeId"
})
public class ContactRelationStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long fromContactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long toContactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long typeId;

    /**
     * Gets the value of the fromContactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getFromContactId() {
        return fromContactId;
    }

    /**
     * Sets the value of the fromContactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromContactId(Long value) {
        this.fromContactId = value;
    }

    /**
     * Gets the value of the toContactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getToContactId() {
        return toContactId;
    }

    /**
     * Sets the value of the toContactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToContactId(Long value) {
        this.toContactId = value;
    }

    /**
     * Gets the value of the typeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeId(Long value) {
        this.typeId = value;
    }

}
