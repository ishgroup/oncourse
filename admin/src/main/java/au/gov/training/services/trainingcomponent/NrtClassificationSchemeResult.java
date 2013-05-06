
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NrtClassificationSchemeResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NrtClassificationSchemeResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AllowMultipleValues" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AppliesToComponentTypes" type="{http://training.gov.au/services/}TrainingComponentTypes" minOccurs="0"/>
 *         &lt;element name="ClassificationValues" type="{http://training.gov.au/services/}ArrayOfClassificationValue"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsProtected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RequiredForComponentTypes" type="{http://training.gov.au/services/}TrainingComponentTypes" minOccurs="0"/>
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
@XmlType(name = "NrtClassificationSchemeResult", propOrder = {
    "allowMultipleValues",
    "appliesToComponentTypes",
    "classificationValues",
    "description",
    "isProtected",
    "name",
    "requiredForComponentTypes",
    "schemeCode"
})
public class NrtClassificationSchemeResult {

    @XmlElement(name = "AllowMultipleValues")
    protected Boolean allowMultipleValues;
    @XmlList
    @XmlElement(name = "AppliesToComponentTypes")
    protected List<String> appliesToComponentTypes;
    @XmlElement(name = "ClassificationValues", required = true, nillable = true)
    protected ArrayOfClassificationValue classificationValues;
    @XmlElement(name = "Description", required = true, nillable = true)
    protected String description;
    @XmlElement(name = "IsProtected")
    protected Boolean isProtected;
    @XmlElement(name = "Name", required = true, nillable = true)
    protected String name;
    @XmlList
    @XmlElement(name = "RequiredForComponentTypes")
    protected List<String> requiredForComponentTypes;
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
     * Gets the value of the appliesToComponentTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the appliesToComponentTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAppliesToComponentTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAppliesToComponentTypes() {
        if (appliesToComponentTypes == null) {
            appliesToComponentTypes = new ArrayList<String>();
        }
        return this.appliesToComponentTypes;
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
     * Gets the value of the requiredForComponentTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredForComponentTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredForComponentTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRequiredForComponentTypes() {
        if (requiredForComponentTypes == null) {
            requiredForComponentTypes = new ArrayList<String>();
        }
        return this.requiredForComponentTypes;
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
