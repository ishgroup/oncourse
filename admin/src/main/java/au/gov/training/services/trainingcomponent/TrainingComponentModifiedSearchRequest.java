
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * <p>Java class for TrainingComponentModifiedSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentModifiedSearchRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractPageRequest">
 *       &lt;sequence>
 *         &lt;element name="DataManagerFilter" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *         &lt;element name="EndDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="StartDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="TrainingComponentTypes" type="{http://training.gov.au/services/}TrainingComponentTypeFilter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentModifiedSearchRequest", propOrder = {
    "dataManagerFilter",
    "endDate",
    "startDate",
    "trainingComponentTypes"
})
public class TrainingComponentModifiedSearchRequest
    extends AbstractPageRequest
{

    @XmlElementRef(name = "DataManagerFilter", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> dataManagerFilter;
    @XmlElementRef(name = "EndDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<DateTimeOffset> endDate;
    @XmlElementRef(name = "StartDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<DateTimeOffset> startDate;
    @XmlElementRef(name = "TrainingComponentTypes", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TrainingComponentTypeFilter> trainingComponentTypes;

    /**
     * Gets the value of the dataManagerFilter property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getDataManagerFilter() {
        return dataManagerFilter;
    }

    /**
     * Sets the value of the dataManagerFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setDataManagerFilter(JAXBElement<ArrayOfstring> value) {
        this.dataManagerFilter = value;
    }

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

    /**
     * Gets the value of the trainingComponentTypes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}
     *     
     */
    public JAXBElement<TrainingComponentTypeFilter> getTrainingComponentTypes() {
        return trainingComponentTypes;
    }

    /**
     * Sets the value of the trainingComponentTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}
     *     
     */
    public void setTrainingComponentTypes(JAXBElement<TrainingComponentTypeFilter> value) {
        this.trainingComponentTypes = value;
    }

}
