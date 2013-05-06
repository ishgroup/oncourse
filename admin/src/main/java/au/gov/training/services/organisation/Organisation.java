
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * <p>Java class for Organisation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Organisation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Codes" type="{http://training.gov.au/services/}ArrayOfOrganisationCode" minOccurs="0"/>
 *         &lt;element name="Contacts" type="{http://training.gov.au/services/}ArrayOfContact" minOccurs="0"/>
 *         &lt;element name="CreatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="DataManagers" type="{http://training.gov.au/services/}ArrayOfDataManagerAssignment" minOccurs="0"/>
 *         &lt;element name="Locations" type="{http://training.gov.au/services/}ArrayOfOrganisationLocation" minOccurs="0"/>
 *         &lt;element name="ResponsibleLegalPersons" type="{http://training.gov.au/services/}ArrayOfResponsibleLegalPerson" minOccurs="0"/>
 *         &lt;element name="TradingNames" type="{http://training.gov.au/services/}ArrayOfTradingName" minOccurs="0"/>
 *         &lt;element name="UpdatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="Urls" type="{http://training.gov.au/services/}ArrayOfUrl" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Organisation", propOrder = {
    "codes",
    "contacts",
    "createdDate",
    "dataManagers",
    "locations",
    "responsibleLegalPersons",
    "tradingNames",
    "updatedDate",
    "urls"
})
@XmlSeeAlso({
    Rto.class,
    Organisation2 .class
})
public class Organisation {

    @XmlElementRef(name = "Codes", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfOrganisationCode> codes;
    @XmlElementRef(name = "Contacts", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfContact> contacts;
    @XmlElement(name = "CreatedDate")
    protected DateTimeOffset createdDate;
    @XmlElementRef(name = "DataManagers", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfDataManagerAssignment> dataManagers;
    @XmlElementRef(name = "Locations", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfOrganisationLocation> locations;
    @XmlElementRef(name = "ResponsibleLegalPersons", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfResponsibleLegalPerson> responsibleLegalPersons;
    @XmlElementRef(name = "TradingNames", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfTradingName> tradingNames;
    @XmlElement(name = "UpdatedDate")
    protected DateTimeOffset updatedDate;
    @XmlElementRef(name = "Urls", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfUrl> urls;

    /**
     * Gets the value of the codes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOrganisationCode }{@code >}
     *     
     */
    public JAXBElement<ArrayOfOrganisationCode> getCodes() {
        return codes;
    }

    /**
     * Sets the value of the codes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOrganisationCode }{@code >}
     *     
     */
    public void setCodes(JAXBElement<ArrayOfOrganisationCode> value) {
        this.codes = value;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfContact }{@code >}
     *     
     */
    public JAXBElement<ArrayOfContact> getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfContact }{@code >}
     *     
     */
    public void setContacts(JAXBElement<ArrayOfContact> value) {
        this.contacts = value;
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeOffset }
     *     
     */
    public DateTimeOffset getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeOffset }
     *     
     */
    public void setCreatedDate(DateTimeOffset value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the dataManagers property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDataManagerAssignment }{@code >}
     *     
     */
    public JAXBElement<ArrayOfDataManagerAssignment> getDataManagers() {
        return dataManagers;
    }

    /**
     * Sets the value of the dataManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDataManagerAssignment }{@code >}
     *     
     */
    public void setDataManagers(JAXBElement<ArrayOfDataManagerAssignment> value) {
        this.dataManagers = value;
    }

    /**
     * Gets the value of the locations property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOrganisationLocation }{@code >}
     *     
     */
    public JAXBElement<ArrayOfOrganisationLocation> getLocations() {
        return locations;
    }

    /**
     * Sets the value of the locations property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOrganisationLocation }{@code >}
     *     
     */
    public void setLocations(JAXBElement<ArrayOfOrganisationLocation> value) {
        this.locations = value;
    }

    /**
     * Gets the value of the responsibleLegalPersons property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfResponsibleLegalPerson }{@code >}
     *     
     */
    public JAXBElement<ArrayOfResponsibleLegalPerson> getResponsibleLegalPersons() {
        return responsibleLegalPersons;
    }

    /**
     * Sets the value of the responsibleLegalPersons property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfResponsibleLegalPerson }{@code >}
     *     
     */
    public void setResponsibleLegalPersons(JAXBElement<ArrayOfResponsibleLegalPerson> value) {
        this.responsibleLegalPersons = value;
    }

    /**
     * Gets the value of the tradingNames property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfTradingName }{@code >}
     *     
     */
    public JAXBElement<ArrayOfTradingName> getTradingNames() {
        return tradingNames;
    }

    /**
     * Sets the value of the tradingNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfTradingName }{@code >}
     *     
     */
    public void setTradingNames(JAXBElement<ArrayOfTradingName> value) {
        this.tradingNames = value;
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

    /**
     * Gets the value of the urls property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfUrl }{@code >}
     *     
     */
    public JAXBElement<ArrayOfUrl> getUrls() {
        return urls;
    }

    /**
     * Sets the value of the urls property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfUrl }{@code >}
     *     
     */
    public void setUrls(JAXBElement<ArrayOfUrl> value) {
        this.urls = value;
    }

}
