
package ish.oncourse.webservices.v22.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for emailTemplateStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="emailTemplateStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v22.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bodyHtml" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="bodyPlain" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="entity" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "emailTemplateStub", propOrder = {
    "bodyHtml",
    "bodyPlain",
    "entity",
    "name",
    "subject"
})
public class EmailTemplateStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String bodyHtml;
    @XmlElement(required = true)
    protected String bodyPlain;
    @XmlElement(required = true)
    protected String entity;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String subject;

    /**
     * Gets the value of the bodyHtml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodyHtml() {
        return bodyHtml;
    }

    /**
     * Sets the value of the bodyHtml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodyHtml(String value) {
        this.bodyHtml = value;
    }

    /**
     * Gets the value of the bodyPlain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodyPlain() {
        return bodyPlain;
    }

    /**
     * Sets the value of the bodyPlain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodyPlain(String value) {
        this.bodyPlain = value;
    }

    /**
     * Gets the value of the entity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Sets the value of the entity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntity(String value) {
        this.entity = value;
    }

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
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

}
