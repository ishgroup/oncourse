
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfReleaseCompanionVolumeLink complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfReleaseCompanionVolumeLink">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReleaseCompanionVolumeLink" type="{http://training.gov.au/services/}ReleaseCompanionVolumeLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfReleaseCompanionVolumeLink", propOrder = {
    "releaseCompanionVolumeLink"
})
public class ArrayOfReleaseCompanionVolumeLink {

    @XmlElement(name = "ReleaseCompanionVolumeLink", nillable = true)
    protected List<ReleaseCompanionVolumeLink> releaseCompanionVolumeLink;

    /**
     * Gets the value of the releaseCompanionVolumeLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the releaseCompanionVolumeLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReleaseCompanionVolumeLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReleaseCompanionVolumeLink }
     * 
     * 
     */
    public List<ReleaseCompanionVolumeLink> getReleaseCompanionVolumeLink() {
        if (releaseCompanionVolumeLink == null) {
            releaseCompanionVolumeLink = new ArrayList<ReleaseCompanionVolumeLink>();
        }
        return this.releaseCompanionVolumeLink;
    }

}
