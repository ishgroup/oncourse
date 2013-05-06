
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Organisation2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Organisation2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}Organisation">
 *       &lt;sequence>
 *         &lt;element name="IsLegacyData" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Organisation2", propOrder = {
    "isLegacyData"
})
@XmlSeeAlso({
    Rto3 .class,
    Rto2 .class
})
public class Organisation2
    extends Organisation
{

    @XmlElement(name = "IsLegacyData")
    protected Boolean isLegacyData;

    /**
     * Gets the value of the isLegacyData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsLegacyData() {
        return isLegacyData;
    }

    /**
     * Sets the value of the isLegacyData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsLegacyData(Boolean value) {
        this.isLegacyData = value;
    }

}
