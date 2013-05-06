
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponent2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponent2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}TrainingComponent">
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
@XmlType(name = "TrainingComponent2", propOrder = {
    "isLegacyData"
})
@XmlSeeAlso({
    TrainingComponent3 .class
})
public class TrainingComponent2
    extends TrainingComponent
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
