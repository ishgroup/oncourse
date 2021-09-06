
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DriversLicenceDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriversLicenceDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://usi.gov.au/2020/ws}DVSDocumentType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LicenceNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="State" type="{http://usi.gov.au/2020/ws}StateListType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DriversLicenceDocumentType", propOrder = {
    "licenceNumber",
    "state"
})
public class DriversLicenceDocumentType
    extends DVSDocumentType
{

    @XmlElement(name = "LicenceNumber", required = true)
    protected String licenceNumber;
    @XmlElement(name = "State", required = true)
    @XmlSchemaType(name = "string")
    protected StateListType state;

    /**
     * Gets the value of the licenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenceNumber() {
        return licenceNumber;
    }

    /**
     * Sets the value of the licenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenceNumber(String value) {
        this.licenceNumber = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link StateListType }
     *     
     */
    public StateListType getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateListType }
     *     
     */
    public void setState(StateListType value) {
        this.state = value;
    }

}
