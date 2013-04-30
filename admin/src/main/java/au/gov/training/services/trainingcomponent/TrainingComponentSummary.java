
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
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * <p>Java class for TrainingComponentSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ComponentType" type="{http://training.gov.au/services/}TrainingComponentTypes" minOccurs="0"/>
 *         &lt;element name="CreatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="IsConfidential" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsCurrent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UpdatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="UsageReccomendation" type="{http://schemas.datacontract.org/2004/07/Deewr.Tga.Model}UsageRecommendationState" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentSummary", propOrder = {
    "code",
    "componentType",
    "createdDate",
    "isConfidential",
    "isCurrent",
    "title",
    "updatedDate",
    "usageReccomendation"
})
public class TrainingComponentSummary {

    @XmlElementRef(name = "Code", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> code;
    @XmlList
    @XmlElement(name = "ComponentType")
    protected List<String> componentType;
    @XmlElement(name = "CreatedDate")
    protected DateTimeOffset createdDate;
    @XmlElement(name = "IsConfidential")
    protected Boolean isConfidential;
    @XmlElementRef(name = "IsCurrent", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<Boolean> isCurrent;
    @XmlElementRef(name = "Title", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> title;
    @XmlElement(name = "UpdatedDate")
    protected DateTimeOffset updatedDate;
    @XmlList
    @XmlElement(name = "UsageReccomendation")
    protected List<String> usageReccomendation;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCode(JAXBElement<String> value) {
        this.code = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the componentType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the componentType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponentType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getComponentType() {
        if (componentType == null) {
            componentType = new ArrayList<>();
        }
        return this.componentType;
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeOffset }
     *     
     */
    public DateTimeOffset getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeOffset }
     *     
     */
    public void setCreatedDate(DateTimeOffset value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the isConfidential property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsConfidential() {
        return isConfidential;
    }

    /**
     * Sets the value of the isConfidential property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsConfidential(Boolean value) {
        this.isConfidential = value;
    }

    /**
     * Gets the value of the isCurrent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIsCurrent() {
        return isCurrent;
    }

    /**
     * Sets the value of the isCurrent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIsCurrent(JAXBElement<Boolean> value) {
        this.isCurrent = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTitle(JAXBElement<String> value) {
        this.title = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the updatedDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeOffset }
     *     
     */
    public DateTimeOffset getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Sets the value of the updatedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeOffset }
     *     
     */
    public void setUpdatedDate(DateTimeOffset value) {
        this.updatedDate = value;
    }

    /**
     * Gets the value of the usageReccomendation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usageReccomendation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsageReccomendation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUsageReccomendation() {
        if (usageReccomendation == null) {
            usageReccomendation = new ArrayList<>();
        }
        return this.usageReccomendation;
    }

}
