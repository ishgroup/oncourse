
package ish.oncourse.webservices.v17.stubs.replication;

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
 * &lt;complexType name="binaryInfoRelationStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v17.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="entityAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="entityWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="entityName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="binaryInfoId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="specialType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="documentVersionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="documentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
    "documentVersionId",
    "documentId"
})
public class BinaryInfoRelationStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long entityAngelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long entityWillowId;
    @XmlElement(required = true)
    protected String entityName;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long binaryInfoId;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer specialType;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long documentVersionId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long documentId;

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

    /**
     * Gets the value of the documentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getDocumentId() {
        return documentId;
    }

    /**
     * Sets the value of the documentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentId(Long value) {
        this.documentId = value;
    }

}
