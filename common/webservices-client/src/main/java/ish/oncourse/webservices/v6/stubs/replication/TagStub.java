
package ish.oncourse.webservices.v6.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.adapters.StringToBooleanAdapter;
import ish.oncourse.webservices.util.adapters.StringToIntegerAdapter;
import ish.oncourse.webservices.util.adapters.StringToLongAdapter;


/**
 * <p>Java class for tagStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tagStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="detail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="detailTextile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tagGroup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="webVisible" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nodeType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="shortName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="weighting" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="parentId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="specialType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tagStub", propOrder = {
    "detail",
    "detailTextile",
    "tagGroup",
    "webVisible",
    "name",
    "nodeType",
    "shortName",
    "weighting",
    "parentId",
    "specialType"
})
public class TagStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String detail;
    @XmlElement(required = true)
    protected String detailTextile;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean tagGroup;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean webVisible;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer nodeType;
    @XmlElement(required = true)
    protected String shortName;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer weighting;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long parentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToIntegerAdapter.class)
    @XmlSchemaType(name = "int")
    protected Integer specialType;

    /**
     * Gets the value of the detail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetail(String value) {
        this.detail = value;
    }

    /**
     * Gets the value of the detailTextile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetailTextile() {
        return detailTextile;
    }

    /**
     * Sets the value of the detailTextile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetailTextile(String value) {
        this.detailTextile = value;
    }

    /**
     * Gets the value of the tagGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isTagGroup() {
        return tagGroup;
    }

    /**
     * Sets the value of the tagGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTagGroup(Boolean value) {
        this.tagGroup = value;
    }

    /**
     * Gets the value of the webVisible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isWebVisible() {
        return webVisible;
    }

    /**
     * Sets the value of the webVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebVisible(Boolean value) {
        this.webVisible = value;
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
     * Gets the value of the nodeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getNodeType() {
        return nodeType;
    }

    /**
     * Sets the value of the nodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeType(Integer value) {
        this.nodeType = value;
    }

    /**
     * Gets the value of the shortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the value of the shortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Gets the value of the weighting property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getWeighting() {
        return weighting;
    }

    /**
     * Sets the value of the weighting property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeighting(Integer value) {
        this.weighting = value;
    }

    /**
     * Gets the value of the parentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentId(Long value) {
        this.parentId = value;
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

}
