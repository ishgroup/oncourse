
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TrainingComponentTransferDataManagerRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentTransferDataManagerRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataManagerCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EffectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="RecognitionManagerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TrainingComponentCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentTransferDataManagerRequest", propOrder = {
    "dataManagerCode",
    "effectiveDate",
    "recognitionManagerCode",
    "trainingComponentCode"
})
public class TrainingComponentTransferDataManagerRequest {

    @XmlElement(name = "DataManagerCode", required = true, nillable = true)
    protected String dataManagerCode;
    @XmlElementRef(name = "EffectiveDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> effectiveDate;
    @XmlElementRef(name = "RecognitionManagerCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> recognitionManagerCode;
    @XmlElement(name = "TrainingComponentCode", required = true, nillable = true)
    protected String trainingComponentCode;

    /**
     * Gets the value of the dataManagerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataManagerCode() {
        return dataManagerCode;
    }

    /**
     * Sets the value of the dataManagerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataManagerCode(String value) {
        this.dataManagerCode = value;
    }

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setEffectiveDate(JAXBElement<XMLGregorianCalendar> value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the recognitionManagerCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRecognitionManagerCode() {
        return recognitionManagerCode;
    }

    /**
     * Sets the value of the recognitionManagerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRecognitionManagerCode(JAXBElement<String> value) {
        this.recognitionManagerCode = value;
    }

    /**
     * Gets the value of the trainingComponentCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrainingComponentCode() {
        return trainingComponentCode;
    }

    /**
     * Sets the value of the trainingComponentCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrainingComponentCode(String value) {
        this.trainingComponentCode = value;
    }

}
