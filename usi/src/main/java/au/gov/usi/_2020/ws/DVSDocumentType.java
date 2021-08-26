
package au.gov.usi._2020.ws;

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
 * &lt;complexType name="DVSDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DVSDocumentType")
@XmlSeeAlso({
    BirthCertificateDocumentType.class,
    CertificateOfRegistrationByDescentDocumentType.class,
    CitizenshipCertificateDocumentType.class,
    DriversLicenceDocumentType.class,
    ImmiCardDocumentType.class,
    MedicareDocumentType.class,
    CentrelinkCardType.class,
    PassportDocumentType.class,
    VisaDocumentType.class
})
public abstract class DVSDocumentType {


}
