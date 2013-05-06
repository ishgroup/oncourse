
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RtoRestriction2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RtoRestriction2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}RtoRestriction">
 *       &lt;sequence>
 *         &lt;element name="RestrictionTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RtoRestriction2", propOrder = {
    "restrictionTypeCode"
})
public class RtoRestriction2
    extends RtoRestriction
{

    @XmlElement(name = "RestrictionTypeCode", required = true, nillable = true)
    protected String restrictionTypeCode;

    /**
     * Gets the value of the restrictionTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestrictionTypeCode() {
        return restrictionTypeCode;
    }

    /**
     * Sets the value of the restrictionTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestrictionTypeCode(String value) {
        this.restrictionTypeCode = value;
    }

}
