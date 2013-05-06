
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * <p>Java class for DeletedSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeletedSearchRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractPageRequest">
 *       &lt;sequence>
 *         &lt;element name="EndDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="StartDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeletedSearchRequest", propOrder = {
    "endDate",
    "startDate"
})
public class DeletedSearchRequest
    extends AbstractPageRequest
{

    @XmlElementRef(name = "EndDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<DateTimeOffset> endDate;
    @XmlElementRef(name = "StartDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<DateTimeOffset> startDate;

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}
     *     
     */
    public JAXBElement<DateTimeOffset> getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}
     *     
     */
    public void setEndDate(JAXBElement<DateTimeOffset> value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}
     *     
     */
    public JAXBElement<DateTimeOffset> getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}
     *     
     */
    public void setStartDate(JAXBElement<DateTimeOffset> value) {
        this.startDate = value;
    }

}
