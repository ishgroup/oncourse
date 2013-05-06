
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Scope complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Scope">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractDto">
 *       &lt;sequence>
 *         &lt;element name="ExtentCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsImplicit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsRefused" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="NrtCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TrainingComponentType" type="{http://training.gov.au/services/}TrainingComponentTypes" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scope", propOrder = {
    "extentCode",
    "isImplicit",
    "isRefused",
    "nrtCode",
    "trainingComponentType"
})
@XmlSeeAlso({
    Scope2 .class
})
public class Scope
    extends AbstractDto
{

    @XmlElementRef(name = "ExtentCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> extentCode;
    @XmlElement(name = "IsImplicit")
    protected Boolean isImplicit;
    @XmlElement(name = "IsRefused")
    protected Boolean isRefused;
    @XmlElementRef(name = "NrtCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nrtCode;
    @XmlList
    @XmlElement(name = "TrainingComponentType")
    protected List<String> trainingComponentType;

    /**
     * Gets the value of the extentCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExtentCode() {
        return extentCode;
    }

    /**
     * Sets the value of the extentCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExtentCode(JAXBElement<String> value) {
        this.extentCode = value;
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
     * Gets the value of the isRefused property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRefused() {
        return isRefused;
    }

    /**
     * Sets the value of the isRefused property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRefused(Boolean value) {
        this.isRefused = value;
    }

    /**
     * Gets the value of the nrtCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNrtCode() {
        return nrtCode;
    }

    /**
     * Sets the value of the nrtCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNrtCode(JAXBElement<String> value) {
        this.nrtCode = value;
    }

    /**
     * Gets the value of the trainingComponentType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trainingComponentType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrainingComponentType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTrainingComponentType() {
        if (trainingComponentType == null) {
            trainingComponentType = new ArrayList<String>();
        }
        return this.trainingComponentType;
    }

}
