
package ish.oncourse.webservices.v23.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for entityRelationTypeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entityRelationTypeStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="toName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fromName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="isShownOnWeb" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="considerHistory" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="shoppingCart" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="discountId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entityRelationTypeStub", propOrder = {
    "name",
    "toName",
    "fromName",
    "description",
    "isShownOnWeb",
    "considerHistory",
    "shoppingCart",
    "discountId"
})
public class EntityRelationTypeStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String toName;
    @XmlElement(required = true)
    protected String fromName;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isShownOnWeb;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean considerHistory;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer shoppingCart;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long discountId;

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
     * Gets the value of the toName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToName() {
        return toName;
    }

    /**
     * Sets the value of the toName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToName(String value) {
        this.toName = value;
    }

    /**
     * Gets the value of the fromName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromName() {
        return fromName;
    }

    /**
     * Sets the value of the fromName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromName(String value) {
        this.fromName = value;
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

    /**
     * Gets the value of the isShownOnWeb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsShownOnWeb() {
        return isShownOnWeb;
    }

    /**
     * Sets the value of the isShownOnWeb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsShownOnWeb(Boolean value) {
        this.isShownOnWeb = value;
    }

    /**
     * Gets the value of the considerHistory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isConsiderHistory() {
        return considerHistory;
    }

    /**
     * Sets the value of the considerHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsiderHistory(Boolean value) {
        this.considerHistory = value;
    }

    /**
     * Gets the value of the shoppingCart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Sets the value of the shoppingCart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShoppingCart(Integer value) {
        this.shoppingCart = value;
    }

    /**
     * Gets the value of the discountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getDiscountId() {
        return discountId;
    }

    /**
     * Sets the value of the discountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountId(Long value) {
        this.discountId = value;
    }

}
