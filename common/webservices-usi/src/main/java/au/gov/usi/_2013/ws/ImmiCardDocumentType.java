
package au.gov.usi._2013.ws;

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
 * &lt;complexType name="ImmiCardDocumentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://usi.gov.au/2013/ws}DVSDocumentType">
 *       &lt;sequence>
 *         &lt;element name="ImmiCardNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="9"/>
 *               &lt;maxLength value="9"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
