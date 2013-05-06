
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentDetailsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentDetailsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InformationRequest" type="{http://training.gov.au/services/}TrainingComponentInformationRequested" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentDetailsRequest", propOrder = {
    "code",
    "informationRequest"
})
public class TrainingComponentDetailsRequest {

    @XmlElement(name = "Code", required = true, nillable = true)
    protected String code;
    @XmlElementRef(name = "InformationRequest", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TrainingComponentInformationRequested> informationRequest;

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
     * Gets the value of the informationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentInformationRequested }{@code >}
     *     
     */
    public JAXBElement<TrainingComponentInformationRequested> getInformationRequest() {
        return informationRequest;
    }

    /**
     * Sets the value of the informationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentInformationRequested }{@code >}
     *     
     */
    public void setInformationRequest(JAXBElement<TrainingComponentInformationRequested> value) {
        this.informationRequest = value;
    }

}
