
package ish.oncourse.webservices.v6.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.adapters.StringToLongAdapter;


/**
 * <p>Java class for tagRelationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tagRelationStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="entityAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="entityName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="entityWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="tagId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tagRelationStub", propOrder = {
    "entityAngelId",
    "entityName",
    "entityWillowId",
    "tagId"
})
public class TagRelationStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long entityAngelId;
    @XmlElement(required = true)
    protected String entityName;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long entityWillowId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long tagId;

    /**
     * Gets the value of the entityAngelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getEntityAngelId() {
        return entityAngelId;
    }

    /**
     * Sets the value of the entityAngelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityAngelId(Long value) {
        this.entityAngelId = value;
    }

    /**
     * Gets the value of the entityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the value of the entityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityName(String value) {
        this.entityName = value;
    }

    /**
     * Gets the value of the entityWillowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getEntityWillowId() {
        return entityWillowId;
    }

    /**
     * Sets the value of the entityWillowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityWillowId(Long value) {
        this.entityWillowId = value;
    }

    /**
     * Gets the value of the tagId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * Sets the value of the tagId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTagId(Long value) {
        this.tagId = value;
    }

}
