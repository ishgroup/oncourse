
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentContactRole complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentContactRole">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AllowGroupContact" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AllowMultipleCurrent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsImplicit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="RequiredTrainingComponentTypes" type="{http://training.gov.au/services/}TrainingComponentTypes" minOccurs="0"/>
 *         &lt;element name="Role" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentContactRole", propOrder = {
    "allowGroupContact",
    "allowMultipleCurrent",
    "description",
    "isImplicit",
    "requiredTrainingComponentTypes",
    "role"
})
public class TrainingComponentContactRole {

    @XmlElement(name = "AllowGroupContact")
    protected Boolean allowGroupContact;
    @XmlElement(name = "AllowMultipleCurrent")
    protected Boolean allowMultipleCurrent;
    @XmlElementRef(name = "Description", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> description;
    @XmlElement(name = "IsImplicit")
    protected Boolean isImplicit;
    @XmlList
    @XmlElement(name = "RequiredTrainingComponentTypes")
    protected List<String> requiredTrainingComponentTypes;
    @XmlElementRef(name = "Role", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> role;

    /**
     * Gets the value of the allowGroupContact property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowGroupContact() {
        return allowGroupContact;
    }

    /**
     * Sets the value of the allowGroupContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowGroupContact(Boolean value) {
        this.allowGroupContact = value;
    }

    /**
     * Gets the value of the allowMultipleCurrent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowMultipleCurrent() {
        return allowMultipleCurrent;
    }

    /**
     * Sets the value of the allowMultipleCurrent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowMultipleCurrent(Boolean value) {
        this.allowMultipleCurrent = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the isImplicit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsImplicit() {
        return isImplicit;
    }

    /**
     * Sets the value of the isImplicit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsImplicit(Boolean value) {
        this.isImplicit = value;
    }

    /**
     * Gets the value of the requiredTrainingComponentTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredTrainingComponentTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredTrainingComponentTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRequiredTrainingComponentTypes() {
        if (requiredTrainingComponentTypes == null) {
            requiredTrainingComponentTypes = new ArrayList<>();
        }
        return this.requiredTrainingComponentTypes;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRole(JAXBElement<String> value) {
        this.role = ((JAXBElement<String> ) value);
    }

}
