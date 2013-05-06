
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * <p>Java class for DeletedTrainingComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeletedTrainingComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NationalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Operation" type="{http://training.gov.au/services/}DeleteOperation" minOccurs="0"/>
 *         &lt;element name="UpdatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeletedTrainingComponent", propOrder = {
    "nationalCode",
    "operation",
    "updatedDate"
})
public class DeletedTrainingComponent {

    @XmlElementRef(name = "NationalCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nationalCode;
    @XmlElement(name = "Operation")
    protected DeleteOperation operation;
    @XmlElement(name = "UpdatedDate")
    protected DateTimeOffset updatedDate;

    /**
     * Gets the value of the nationalCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNationalCode() {
        return nationalCode;
    }

    /**
     * Sets the value of the nationalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNationalCode(JAXBElement<String> value) {
        this.nationalCode = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteOperation }
     *     
     */
    public DeleteOperation getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteOperation }
     *     
     */
    public void setOperation(DeleteOperation value) {
        this.operation = value;
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

}
