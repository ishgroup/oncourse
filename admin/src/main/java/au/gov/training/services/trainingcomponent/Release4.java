
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Release4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Release4">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}Release3">
 *       &lt;sequence>
 *         &lt;element name="CompanionVolumeLinks" type="{http://training.gov.au/services/}ArrayOfReleaseCompanionVolumeLink" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Release4", propOrder = {
    "companionVolumeLinks"
})
public class Release4
    extends Release3
{

    @XmlElementRef(name = "CompanionVolumeLinks", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfReleaseCompanionVolumeLink> companionVolumeLinks;

    /**
     * Gets the value of the companionVolumeLinks property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfReleaseCompanionVolumeLink }{@code >}
     *     
     */
    public JAXBElement<ArrayOfReleaseCompanionVolumeLink> getCompanionVolumeLinks() {
        return companionVolumeLinks;
    }

    /**
     * Sets the value of the companionVolumeLinks property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfReleaseCompanionVolumeLink }{@code >}
     *     
     */
    public void setCompanionVolumeLinks(JAXBElement<ArrayOfReleaseCompanionVolumeLink> value) {
        this.companionVolumeLinks = value;
    }

}
