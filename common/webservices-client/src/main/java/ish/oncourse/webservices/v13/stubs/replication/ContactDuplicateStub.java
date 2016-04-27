
package ish.oncourse.webservices.v13.stubs.replication;

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
 *     &lt;extension base="{http://repl.v13.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contactToDeleteId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="contactToUpdateId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "contactToDeleteId",
    "contactToUpdateId",
    "createdBy"
})
public class ContactDuplicateStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String status;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactToDeleteId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactToUpdateId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long createdBy;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
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
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the contactToDeleteId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactToDeleteId() {
        return contactToDeleteId;
    }

    /**
     * Sets the value of the contactToDeleteId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactToDeleteId(Long value) {
        this.contactToDeleteId = value;
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

}
