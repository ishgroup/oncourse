
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.deewr_tga.ScopeDecisionType;


/**
 * <p>Java class for Scope2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Scope2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}Scope">
 *       &lt;sequence>
 *         &lt;element name="ScopeDecisionType" type="{http://schemas.datacontract.org/2004/07/Deewr.Tga.Model}ScopeDecisionType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scope2", propOrder = {
    "scopeDecisionType"
})
public class Scope2
    extends Scope
{

    @XmlElement(name = "ScopeDecisionType", required = true)
    protected ScopeDecisionType scopeDecisionType;

    /**
     * Gets the value of the scopeDecisionType property.
     * 
     * @return
     *     possible object is
     *     {@link ScopeDecisionType }
     *     
     */
    public ScopeDecisionType getScopeDecisionType() {
        return scopeDecisionType;
    }

    /**
     * Sets the value of the scopeDecisionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScopeDecisionType }
     *     
     */
    public void setScopeDecisionType(ScopeDecisionType value) {
        this.scopeDecisionType = value;
    }

}
