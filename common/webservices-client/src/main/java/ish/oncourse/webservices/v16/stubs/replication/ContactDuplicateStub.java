
package ish.oncourse.webservices.v16.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for contactDuplicateStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactDuplicateStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v16.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="contactToDeleteWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="contactToDeleteAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="contactToUpdateId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactDuplicateStub", propOrder = {
    "status",
    "contactToDeleteWillowId",
    "contactToDeleteAngelId",
    "contactToUpdateId",
    "createdBy",
    "description"
})
public class ContactDuplicateStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactToDeleteWillowId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactToDeleteAngelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactToUpdateId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long createdBy;
    @XmlElement(required = true)
    protected String description;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

    /**
     * Gets the value of the contactToDeleteWillowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactToDeleteWillowId() {
        return contactToDeleteWillowId;
    }

    /**
     * Sets the value of the contactToDeleteWillowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactToDeleteWillowId(Long value) {
        this.contactToDeleteWillowId = value;
    }

    /**
     * Gets the value of the contactToDeleteAngelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactToDeleteAngelId() {
        return contactToDeleteAngelId;
    }

    /**
     * Sets the value of the contactToDeleteAngelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactToDeleteAngelId(Long value) {
        this.contactToDeleteAngelId = value;
    }

    /**
     * Gets the value of the contactToUpdateId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactToUpdateId() {
        return contactToUpdateId;
    }

    /**
     * Sets the value of the contactToUpdateId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactToUpdateId(Long value) {
        this.contactToUpdateId = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(Long value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
