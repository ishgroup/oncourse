
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidationFault complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidationFault">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Errors" type="{http://training.gov.au/services/}ArrayOfValidationError" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidationFault", propOrder = {
    "errors"
})
public class ValidationFault {

    @XmlElementRef(name = "Errors", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfValidationError> errors;

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfValidationError }{@code >}
     *     
     */
    public JAXBElement<ArrayOfValidationError> getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfValidationError }{@code >}
     *     
     */
    public void setErrors(JAXBElement<ArrayOfValidationError> value) {
        this.errors = value;
    }

}
