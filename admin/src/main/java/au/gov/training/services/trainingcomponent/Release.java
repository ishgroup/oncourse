
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Release complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Release">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ApprovalProcess" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Components" type="{http://training.gov.au/services/}ArrayOfReleaseComponent" minOccurs="0"/>
 *         &lt;element name="Currency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Files" type="{http://training.gov.au/services/}ArrayOfReleaseFile" minOccurs="0"/>
 *         &lt;element name="IscApprovalDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="MinisterialAgreementDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="NqcEndorsementDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="ReleaseDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="ReleaseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UnitGrid" type="{http://training.gov.au/services/}ArrayOfUnitGridEntry" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Release", propOrder = {
    "approvalProcess",
    "components",
    "currency",
    "files",
    "iscApprovalDate",
    "ministerialAgreementDate",
    "nqcEndorsementDate",
    "releaseDate",
    "releaseNumber",
    "unitGrid"
})
@XmlSeeAlso({
    Release3 .class
})
public class Release {

    @XmlElementRef(name = "ApprovalProcess", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> approvalProcess;
    @XmlElementRef(name = "Components", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfReleaseComponent> components;
    @XmlElementRef(name = "Currency", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> currency;
    @XmlElementRef(name = "Files", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfReleaseFile> files;
    @XmlElementRef(name = "IscApprovalDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> iscApprovalDate;
    @XmlElementRef(name = "MinisterialAgreementDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> ministerialAgreementDate;
    @XmlElementRef(name = "NqcEndorsementDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> nqcEndorsementDate;
    @XmlElement(name = "ReleaseDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar releaseDate;
    @XmlElementRef(name = "ReleaseNumber", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> releaseNumber;
    @XmlElementRef(name = "UnitGrid", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfUnitGridEntry> unitGrid;

    /**
     * Gets the value of the approvalProcess property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getApprovalProcess() {
        return approvalProcess;
    }

    /**
     * Sets the value of the approvalProcess property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setApprovalProcess(JAXBElement<String> value) {
        this.approvalProcess = value;
    }

    /**
     * Gets the value of the components property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfReleaseComponent }{@code >}
     *     
     */
    public JAXBElement<ArrayOfReleaseComponent> getComponents() {
        return components;
    }

    /**
     * Sets the value of the components property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfReleaseComponent }{@code >}
     *     
     */
    public void setComponents(JAXBElement<ArrayOfReleaseComponent> value) {
        this.components = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCurrency(JAXBElement<String> value) {
        this.currency = value;
    }

    /**
     * Gets the value of the files property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfReleaseFile }{@code >}
     *     
     */
    public JAXBElement<ArrayOfReleaseFile> getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfReleaseFile }{@code >}
     *     
     */
    public void setFiles(JAXBElement<ArrayOfReleaseFile> value) {
        this.files = value;
    }

    /**
     * Gets the value of the iscApprovalDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getIscApprovalDate() {
        return iscApprovalDate;
    }

    /**
     * Sets the value of the iscApprovalDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setIscApprovalDate(JAXBElement<XMLGregorianCalendar> value) {
        this.iscApprovalDate = value;
    }

    /**
     * Gets the value of the ministerialAgreementDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getMinisterialAgreementDate() {
        return ministerialAgreementDate;
    }

    /**
     * Sets the value of the ministerialAgreementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setMinisterialAgreementDate(JAXBElement<XMLGregorianCalendar> value) {
        this.ministerialAgreementDate = value;
    }

    /**
     * Gets the value of the nqcEndorsementDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getNqcEndorsementDate() {
        return nqcEndorsementDate;
    }

    /**
     * Sets the value of the nqcEndorsementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setNqcEndorsementDate(JAXBElement<XMLGregorianCalendar> value) {
        this.nqcEndorsementDate = value;
    }

    /**
     * Gets the value of the releaseDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the value of the releaseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReleaseDate(XMLGregorianCalendar value) {
        this.releaseDate = value;
    }

    /**
     * Gets the value of the releaseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReleaseNumber() {
        return releaseNumber;
    }

    /**
     * Sets the value of the releaseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReleaseNumber(JAXBElement<String> value) {
        this.releaseNumber = value;
    }

    /**
     * Gets the value of the unitGrid property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfUnitGridEntry }{@code >}
     *     
     */
    public JAXBElement<ArrayOfUnitGridEntry> getUnitGrid() {
        return unitGrid;
    }

    /**
     * Sets the value of the unitGrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfUnitGridEntry }{@code >}
     *     
     */
    public void setUnitGrid(JAXBElement<ArrayOfUnitGridEntry> value) {
        this.unitGrid = value;
    }

}
