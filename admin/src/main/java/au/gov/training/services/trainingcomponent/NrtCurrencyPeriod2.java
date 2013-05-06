
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NrtCurrencyPeriod2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NrtCurrencyPeriod2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}NrtCurrencyPeriod">
 *       &lt;sequence>
 *         &lt;element name="EndComment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EndReasonCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NrtCurrencyPeriod2", propOrder = {
    "endComment",
    "endReasonCode"
})
public class NrtCurrencyPeriod2
    extends NrtCurrencyPeriod
{

    @XmlElementRef(name = "EndComment", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endComment;
    @XmlElementRef(name = "EndReasonCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endReasonCode;

    /**
     * Gets the value of the endComment property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndComment() {
        return endComment;
    }

    /**
     * Sets the value of the endComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndComment(JAXBElement<String> value) {
        this.endComment = value;
    }

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

}
