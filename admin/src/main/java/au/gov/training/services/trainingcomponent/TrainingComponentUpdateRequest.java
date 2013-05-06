
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentUpdateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentUpdateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CompletionMappingList" type="{http://training.gov.au/services/}CompletionMappingList" minOccurs="0"/>
 *         &lt;element name="CurrencyPeriodList" type="{http://training.gov.au/services/}CurrencyPeriodList" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TrainingComponentClassificationList" type="{http://training.gov.au/services/}TrainingComponentClassificationList" minOccurs="0"/>
 *         &lt;element name="TrainingComponentContactList" type="{http://training.gov.au/services/}TrainingComponentContactList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentUpdateRequest", propOrder = {
    "code",
    "completionMappingList",
    "currencyPeriodList",
    "title",
    "trainingComponentClassificationList",
    "trainingComponentContactList"
})
public class TrainingComponentUpdateRequest {

    @XmlElement(name = "Code", required = true, nillable = true)
    protected String code;
    @XmlElementRef(name = "CompletionMappingList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<CompletionMappingList> completionMappingList;
    @XmlElementRef(name = "CurrencyPeriodList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<CurrencyPeriodList> currencyPeriodList;
    @XmlElementRef(name = "Title", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> title;
    @XmlElementRef(name = "TrainingComponentClassificationList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TrainingComponentClassificationList> trainingComponentClassificationList;
    @XmlElementRef(name = "TrainingComponentContactList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TrainingComponentContactList> trainingComponentContactList;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the completionMappingList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CompletionMappingList }{@code >}
     *     
     */
    public JAXBElement<CompletionMappingList> getCompletionMappingList() {
        return completionMappingList;
    }

    /**
     * Sets the value of the completionMappingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CompletionMappingList }{@code >}
     *     
     */
    public void setCompletionMappingList(JAXBElement<CompletionMappingList> value) {
        this.completionMappingList = value;
    }

    /**
     * Gets the value of the currencyPeriodList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CurrencyPeriodList }{@code >}
     *     
     */
    public JAXBElement<CurrencyPeriodList> getCurrencyPeriodList() {
        return currencyPeriodList;
    }

    /**
     * Sets the value of the currencyPeriodList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CurrencyPeriodList }{@code >}
     *     
     */
    public void setCurrencyPeriodList(JAXBElement<CurrencyPeriodList> value) {
        this.currencyPeriodList = value;
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
        this.title = value;
    }

    /**
     * Gets the value of the trainingComponentClassificationList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentClassificationList }{@code >}
     *     
     */
    public JAXBElement<TrainingComponentClassificationList> getTrainingComponentClassificationList() {
        return trainingComponentClassificationList;
    }

    /**
     * Sets the value of the trainingComponentClassificationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentClassificationList }{@code >}
     *     
     */
    public void setTrainingComponentClassificationList(JAXBElement<TrainingComponentClassificationList> value) {
        this.trainingComponentClassificationList = value;
    }

    /**
     * Gets the value of the trainingComponentContactList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentContactList }{@code >}
     *     
     */
    public JAXBElement<TrainingComponentContactList> getTrainingComponentContactList() {
        return trainingComponentContactList;
    }

    /**
     * Sets the value of the trainingComponentContactList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentContactList }{@code >}
     *     
     */
    public void setTrainingComponentContactList(JAXBElement<TrainingComponentContactList> value) {
        this.trainingComponentContactList = value;
    }

}
