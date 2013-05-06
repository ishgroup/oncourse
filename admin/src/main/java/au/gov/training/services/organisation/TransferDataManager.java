
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transferDataManagerRequest" type="{http://training.gov.au/services/}TransferDataManagerRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transferDataManagerRequest"
})
@XmlRootElement(name = "TransferDataManager")
public class TransferDataManager {

    @XmlElementRef(name = "transferDataManagerRequest", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TransferDataManagerRequest> transferDataManagerRequest;

    /**
     * Gets the value of the transferDataManagerRequest property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TransferDataManagerRequest }{@code >}
     *     
     */
    public JAXBElement<TransferDataManagerRequest> getTransferDataManagerRequest() {
        return transferDataManagerRequest;
    }

    /**
     * Sets the value of the transferDataManagerRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TransferDataManagerRequest }{@code >}
     *     
     */
    public void setTransferDataManagerRequest(JAXBElement<TransferDataManagerRequest> value) {
        this.transferDataManagerRequest = value;
    }

}
