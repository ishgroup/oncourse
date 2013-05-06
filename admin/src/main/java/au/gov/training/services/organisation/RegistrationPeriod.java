
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegistrationPeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegistrationPeriod">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractDto">
 *       &lt;sequence>
 *         &lt;element name="EndReasonCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EndReasonComments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Exerciser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LegalAuthority" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistrationPeriod", propOrder = {
    "endReasonCode",
    "endReasonComments",
    "exerciser",
    "legalAuthority"
})
public class RegistrationPeriod
    extends AbstractDto
{

    @XmlElementRef(name = "EndReasonCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endReasonCode;
    @XmlElementRef(name = "EndReasonComments", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endReasonComments;
    @XmlElementRef(name = "Exerciser", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> exerciser;
    @XmlElementRef(name = "LegalAuthority", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> legalAuthority;

    /**
     * Gets the value of the endReasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndReasonCode() {
        return endReasonCode;
    }

    /**
     * Sets the value of the endReasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndReasonCode(JAXBElement<String> value) {
        this.endReasonCode = value;
    }

    /**
     * Gets the value of the endReasonComments property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndReasonComments() {
        return endReasonComments;
    }

    /**
     * Sets the value of the endReasonComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndReasonComments(JAXBElement<String> value) {
        this.endReasonComments = value;
    }

    /**
     * Gets the value of the exerciser property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExerciser() {
        return exerciser;
    }

    /**
     * Sets the value of the exerciser property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExerciser(JAXBElement<String> value) {
        this.exerciser = value;
    }

    /**
     * Gets the value of the legalAuthority property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLegalAuthority() {
        return legalAuthority;
    }

    /**
     * Sets the value of the legalAuthority property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLegalAuthority(JAXBElement<String> value) {
        this.legalAuthority = value;
    }

}
