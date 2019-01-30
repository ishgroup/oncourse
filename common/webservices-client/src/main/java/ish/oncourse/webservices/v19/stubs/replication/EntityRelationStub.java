
package ish.oncourse.webservices.v19.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for entityRelationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entityRelationStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v19.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fromEntityIdentifier" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="toEntityIdentifier" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fromEntityAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="fromEntityWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="toEntityAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="toEntityWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entityRelationStub", propOrder = {
    "fromEntityIdentifier",
    "toEntityIdentifier",
    "fromEntityAngelId",
    "fromEntityWillowId",
    "toEntityAngelId",
    "toEntityWillowId"
})
public class EntityRelationStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer fromEntityIdentifier;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer toEntityIdentifier;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long fromEntityAngelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long fromEntityWillowId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long toEntityAngelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long toEntityWillowId;

    /**
     * Gets the value of the fromEntityIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getFromEntityIdentifier() {
        return fromEntityIdentifier;
    }

    /**
     * Sets the value of the fromEntityIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromEntityIdentifier(Integer value) {
        this.fromEntityIdentifier = value;
    }

    /**
     * Gets the value of the toEntityIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getToEntityIdentifier() {
        return toEntityIdentifier;
    }

    /**
     * Sets the value of the toEntityIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToEntityIdentifier(Integer value) {
        this.toEntityIdentifier = value;
    }

    /**
     * Gets the value of the fromEntityAngelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getFromEntityAngelId() {
        return fromEntityAngelId;
    }

    /**
     * Sets the value of the fromEntityAngelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromEntityAngelId(Long value) {
        this.fromEntityAngelId = value;
    }

    /**
     * Gets the value of the fromEntityWillowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getFromEntityWillowId() {
        return fromEntityWillowId;
    }

    /**
     * Sets the value of the fromEntityWillowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromEntityWillowId(Long value) {
        this.fromEntityWillowId = value;
    }

    /**
     * Gets the value of the toEntityAngelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getToEntityAngelId() {
        return toEntityAngelId;
    }

    /**
     * Sets the value of the toEntityAngelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToEntityAngelId(Long value) {
        this.toEntityAngelId = value;
    }

    /**
     * Gets the value of the toEntityWillowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getToEntityWillowId() {
        return toEntityWillowId;
    }

    /**
     * Sets the value of the toEntityWillowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToEntityWillowId(Long value) {
        this.toEntityWillowId = value;
    }

}
