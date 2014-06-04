
package ish.oncourse.webservices.v7.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for customFieldStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customFieldStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v7.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="customFieldTypeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="foreignId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customFieldStub", propOrder = {
    "customFieldTypeId",
    "foreignId",
    "value"
})
public class CustomFieldStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long customFieldTypeId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long foreignId;
    @XmlElement(required = true)
    protected String value;

    /**
     * Gets the value of the customFieldTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCustomFieldTypeId() {
        return customFieldTypeId;
    }

    /**
     * Sets the value of the customFieldTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomFieldTypeId(Long value) {
        this.customFieldTypeId = value;
    }

    /**
     * Gets the value of the foreignId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getForeignId() {
        return foreignId;
    }

    /**
     * Sets the value of the foreignId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignId(Long value) {
        this.foreignId = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
