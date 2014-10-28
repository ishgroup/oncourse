
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CertificateOfRegistrationByDescentDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateOfRegistrationByDescentDocumentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://usi.gov.au/2013/ws}DVSDocumentType">
 *       &lt;sequence>
 *         &lt;element name="AcquisitionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateOfRegistrationByDescentDocumentType", propOrder = {
    "acquisitionDate"
})
public class CertificateOfRegistrationByDescentDocumentType
    extends DVSDocumentType
{

    @XmlElement(name = "AcquisitionDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar acquisitionDate;

    /**
     * Gets the value of the acquisitionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Sets the value of the acquisitionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAcquisitionDate(XMLGregorianCalendar value) {
        this.acquisitionDate = value;
    }

}
