
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
 *         &lt;element name="deleteRequest" type="{http://training.gov.au/services/}DeleteRequest" minOccurs="0"/>
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
    "deleteRequest"
})
@XmlRootElement(name = "Delete")
public class Delete {

    @XmlElementRef(name = "deleteRequest", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<DeleteRequest> deleteRequest;

    /**
     * Gets the value of the deleteRequest property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DeleteRequest }{@code >}
     *     
     */
    public JAXBElement<DeleteRequest> getDeleteRequest() {
        return deleteRequest;
    }

    /**
     * Sets the value of the deleteRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DeleteRequest }{@code >}
     *     
     */
    public void setDeleteRequest(JAXBElement<DeleteRequest> value) {
        this.deleteRequest = value;
    }

}
