
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Classification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Classification">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractDto">
 *       &lt;sequence>
 *         &lt;element name="PurposeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SchemeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ValueCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Classification", propOrder = {
    "purposeCode",
    "schemeCode",
    "valueCode"
})
public class Classification
    extends AbstractDto
{

    @XmlElement(name = "PurposeCode", required = true, nillable = true)
    protected String purposeCode;
    @XmlElement(name = "SchemeCode", required = true, nillable = true)
    protected String schemeCode;
    @XmlElement(name = "ValueCode", required = true, nillable = true)
    protected String valueCode;

    /**
     * Gets the value of the purposeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurposeCode() {
        return purposeCode;
    }

    /**
     * Sets the value of the purposeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurposeCode(String value) {
        this.purposeCode = value;
    }

    /**
     * Gets the value of the schemeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemeCode() {
        return schemeCode;
    }

    /**
     * Sets the value of the schemeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemeCode(String value) {
        this.schemeCode = value;
    }

    /**
     * Gets the value of the valueCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueCode() {
        return valueCode;
    }

    /**
     * Sets the value of the valueCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueCode(String value) {
        this.valueCode = value;
    }

}
