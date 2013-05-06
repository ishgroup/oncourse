
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
 *         &lt;element name="createCodeRequest" type="{http://training.gov.au/services/}CreateCodeRequest" minOccurs="0"/>
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
    "createCodeRequest"
})
@XmlRootElement(name = "CreateCode")
public class CreateCode {

    @XmlElementRef(name = "createCodeRequest", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<CreateCodeRequest> createCodeRequest;

    /**
     * Gets the value of the createCodeRequest property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CreateCodeRequest }{@code >}
     *     
     */
    public JAXBElement<CreateCodeRequest> getCreateCodeRequest() {
        return createCodeRequest;
    }

    /**
     * Sets the value of the createCodeRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CreateCodeRequest }{@code >}
     *     
     */
    public void setCreateCodeRequest(JAXBElement<CreateCodeRequest> value) {
        this.createCodeRequest = value;
    }

}
