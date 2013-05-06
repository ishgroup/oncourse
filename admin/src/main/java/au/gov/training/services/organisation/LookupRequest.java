
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LookupRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LookupRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LookupName" type="{http://training.gov.au/services/}LookupName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookupRequest", propOrder = {
    "lookupName"
})
public class LookupRequest {

    @XmlElement(name = "LookupName", required = true)
    protected LookupName lookupName;

    /**
     * Gets the value of the lookupName property.
     * 
     * @return
     *     possible object is
     *     {@link LookupName }
     *     
     */
    public LookupName getLookupName() {
        return lookupName;
    }

    /**
     * Sets the value of the lookupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link LookupName }
     *     
     */
    public void setLookupName(LookupName value) {
        this.lookupName = value;
    }

}
