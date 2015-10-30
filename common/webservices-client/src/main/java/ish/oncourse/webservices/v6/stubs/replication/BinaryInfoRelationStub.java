
package ish.oncourse.webservices.v6.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for binaryInfoRelationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binaryInfoRelationStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="entityAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="entityWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="entityName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="binaryInfoId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="specialType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="documentVersionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binaryInfoRelationStub", propOrder = {
    "entityAngelId",
    "entityWillowId",
    "entityName",
    "binaryInfoId",
    "specialType",
    "documentVersionId"
})
public class BinaryInfoRelationStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long entityAngelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long entityWillowId;
    @XmlElement(required = true)
    protected String entityName;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long binaryInfoId;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer specialType;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long documentVersionId;

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
     * Gets the value of the binaryInfoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getBinaryInfoId() {
        return binaryInfoId;
    }

    /**
     * Sets the value of the binaryInfoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinaryInfoId(Long value) {
        this.binaryInfoId = value;
    }

    /**
     * Gets the value of the specialType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getSpecialType() {
        return specialType;
    }

    /**
     * Sets the value of the specialType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialType(Integer value) {
        this.specialType = value;
    }

    /**
     * Gets the value of the documentVersionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getDocumentVersionId() {
        return documentVersionId;
    }

    /**
     * Sets the value of the documentVersionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentVersionId(Long value) {
        this.documentVersionId = value;
    }

}
