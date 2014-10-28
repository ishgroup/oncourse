
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DriversLicenceDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriversLicenceDocumentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://usi.gov.au/2013/ws}DVSDocumentType">
 *       &lt;sequence>
 *         &lt;element name="LicenceNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="State" type="{http://usi.gov.au/2013/ws}StateListType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
