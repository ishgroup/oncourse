
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImmiCardDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImmiCardDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://usi.gov.au/2020/ws}DVSDocumentType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ImmiCardNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="9"/&gt;
 *               &lt;maxLength value="9"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImmiCardDocumentType", propOrder = {
    "immiCardNumber"
})
public class ImmiCardDocumentType
    extends DVSDocumentType
{

    @XmlElement(name = "ImmiCardNumber", required = true)
    protected String immiCardNumber;

    /**
     * Gets the value of the immiCardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImmiCardNumber() {
        return immiCardNumber;
    }

    /**
     * Sets the value of the immiCardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImmiCardNumber(String value) {
        this.immiCardNumber = value;
    }

}
