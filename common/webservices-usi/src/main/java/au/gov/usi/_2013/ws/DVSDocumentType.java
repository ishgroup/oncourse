
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DVSDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DVSDocumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DVSDocumentType")
@XmlSeeAlso({
    CitizenshipCertificateDocumentType.class,
    DriversLicenceDocumentType.class,
    BirthCertificateDocumentType.class,
    PassportDocumentType.class,
    ImmiCardDocumentType.class,
    MedicareDocumentType.class,
    VisaDocumentType.class,
    CertificateOfRegistrationByDescentDocumentType.class
})
public abstract class DVSDocumentType {


}
