
package ish.oncourse.webservices.v16.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for fieldConfigurationSchemeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fieldConfigurationSchemeStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v16.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="applicationFieldConfigurationId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="enrolFieldConfigurationId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="waitingListFieldConfigurationId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fieldConfigurationSchemeStub", propOrder = {
    "name",
    "applicationFieldConfigurationId",
    "enrolFieldConfigurationId",
    "waitingListFieldConfigurationId"
})
public class FieldConfigurationSchemeStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long applicationFieldConfigurationId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long enrolFieldConfigurationId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long waitingListFieldConfigurationId;

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
     * Gets the value of the applicationFieldConfigurationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getApplicationFieldConfigurationId() {
        return applicationFieldConfigurationId;
    }

    /**
     * Sets the value of the applicationFieldConfigurationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationFieldConfigurationId(Long value) {
        this.applicationFieldConfigurationId = value;
    }

    /**
     * Gets the value of the enrolFieldConfigurationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getEnrolFieldConfigurationId() {
        return enrolFieldConfigurationId;
    }

    /**
     * Sets the value of the enrolFieldConfigurationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrolFieldConfigurationId(Long value) {
        this.enrolFieldConfigurationId = value;
    }

    /**
     * Gets the value of the waitingListFieldConfigurationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getWaitingListFieldConfigurationId() {
        return waitingListFieldConfigurationId;
    }

    /**
     * Sets the value of the waitingListFieldConfigurationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitingListFieldConfigurationId(Long value) {
        this.waitingListFieldConfigurationId = value;
    }

}
