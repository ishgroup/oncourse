
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Address">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Line1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Line2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Postcode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StateCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StateOverseas" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Suburb" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Address", propOrder = {
    "countryCode",
    "line1",
    "line2",
    "postcode",
    "stateCode",
    "stateOverseas",
    "suburb"
})
public class Address {

    @XmlElementRef(name = "CountryCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> countryCode;
    @XmlElementRef(name = "Line1", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> line1;
    @XmlElementRef(name = "Line2", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> line2;
    @XmlElementRef(name = "Postcode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> postcode;
    @XmlElementRef(name = "StateCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> stateCode;
    @XmlElementRef(name = "StateOverseas", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> stateOverseas;
    @XmlElementRef(name = "Suburb", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> suburb;

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCountryCode(JAXBElement<String> value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the line1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLine1() {
        return line1;
    }

    /**
     * Sets the value of the line1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLine1(JAXBElement<String> value) {
        this.line1 = value;
    }

    /**
     * Gets the value of the line2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLine2() {
        return line2;
    }

    /**
     * Sets the value of the line2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLine2(JAXBElement<String> value) {
        this.line2 = value;
    }

    /**
     * Gets the value of the postcode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPostcode() {
        return postcode;
    }

    /**
     * Sets the value of the postcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPostcode(JAXBElement<String> value) {
        this.postcode = value;
    }

    /**
     * Gets the value of the stateCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStateCode() {
        return stateCode;
    }

    /**
     * Sets the value of the stateCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStateCode(JAXBElement<String> value) {
        this.stateCode = value;
    }

    /**
     * Gets the value of the stateOverseas property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStateOverseas() {
        return stateOverseas;
    }

    /**
     * Sets the value of the stateOverseas property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStateOverseas(JAXBElement<String> value) {
        this.stateOverseas = value;
    }

    /**
     * Gets the value of the suburb property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSuburb() {
        return suburb;
    }

    /**
     * Sets the value of the suburb property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSuburb(JAXBElement<String> value) {
        this.suburb = value;
    }

}
