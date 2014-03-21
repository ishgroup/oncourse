
package ish.oncourse.webservices.v4.stubs.replication;

import org.w3._2001.xmlschema.Adapter2;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for contactRelationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactRelationStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="fromContactId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="toContactId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="typeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long fromContactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long toContactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
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
