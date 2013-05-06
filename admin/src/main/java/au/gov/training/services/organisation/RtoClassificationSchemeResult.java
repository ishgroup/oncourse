
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RtoClassificationSchemeResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RtoClassificationSchemeResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AllowMultipleValues" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ClassificationValues" type="{http://training.gov.au/services/}ArrayOfClassificationValue"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsProtected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SchemeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RtoClassificationSchemeResult", propOrder = {
    "allowMultipleValues",
    "classificationValues",
    "description",
    "isProtected",
    "isRequired",
    "name",
    "schemeCode"
})
public class RtoClassificationSchemeResult {

    @XmlElement(name = "AllowMultipleValues")
    protected Boolean allowMultipleValues;
    @XmlElement(name = "ClassificationValues", required = true, nillable = true)
    protected ArrayOfClassificationValue classificationValues;
    @XmlElement(name = "Description", required = true, nillable = true)
    protected String description;
    @XmlElement(name = "IsProtected")
    protected Boolean isProtected;
    @XmlElement(name = "IsRequired")
    protected Boolean isRequired;
    @XmlElement(name = "Name", required = true, nillable = true)
    protected String name;
    @XmlElement(name = "SchemeCode", required = true, nillable = true)
    protected String schemeCode;

    /**
     * Gets the value of the allowMultipleValues property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    /**
     * Sets the value of the allowMultipleValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowMultipleValues(Boolean value) {
        this.allowMultipleValues = value;
    }

    /**
     * Gets the value of the classificationValues property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfClassificationValue }
     *     
     */
    public ArrayOfClassificationValue getClassificationValues() {
        return classificationValues;
    }

    /**
     * Sets the value of the classificationValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfClassificationValue }
     *     
     */
    public void setClassificationValues(ArrayOfClassificationValue value) {
        this.classificationValues = value;
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
     * Gets the value of the isProtected property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsProtected() {
        return isProtected;
    }

    /**
     * Sets the value of the isProtected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsProtected(Boolean value) {
        this.isProtected = value;
    }

    /**
     * Gets the value of the isRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRequired() {
        return isRequired;
    }

    /**
     * Sets the value of the isRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRequired(Boolean value) {
        this.isRequired = value;
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
     * Gets the value of the schemeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemeCode() {
        return schemeCode;
    }

    /**
     * Sets the value of the schemeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemeCode(String value) {
        this.schemeCode = value;
    }

}
