
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentSummary3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentSummary3">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}TrainingComponentSummary2">
 *       &lt;sequence>
 *         &lt;element name="CurrencyStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentSummary3", propOrder = {
    "currencyStatus"
})
public class TrainingComponentSummary3
    extends TrainingComponentSummary2
{

    @XmlElementRef(name = "CurrencyStatus", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> currencyStatus;

    /**
     * Gets the value of the currencyStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCurrencyStatus() {
        return currencyStatus;
    }

    /**
     * Sets the value of the currencyStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCurrencyStatus(JAXBElement<String> value) {
        this.currencyStatus = value;
    }

}
