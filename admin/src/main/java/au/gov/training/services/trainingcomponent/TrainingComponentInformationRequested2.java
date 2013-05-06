
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentInformationRequested2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentInformationRequested2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}TrainingComponentInformationRequested">
 *       &lt;sequence>
 *         &lt;element name="ShowCompanionVolumeLinks" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentInformationRequested2", propOrder = {
    "showCompanionVolumeLinks"
})
public class TrainingComponentInformationRequested2
    extends TrainingComponentInformationRequested
{

    @XmlElement(name = "ShowCompanionVolumeLinks")
    protected Boolean showCompanionVolumeLinks;

    /**
     * Gets the value of the showCompanionVolumeLinks property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowCompanionVolumeLinks() {
        return showCompanionVolumeLinks;
    }

    /**
     * Sets the value of the showCompanionVolumeLinks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowCompanionVolumeLinks(Boolean value) {
        this.showCompanionVolumeLinks = value;
    }

}
