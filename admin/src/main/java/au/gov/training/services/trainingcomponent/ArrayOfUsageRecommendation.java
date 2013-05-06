
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUsageRecommendation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUsageRecommendation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UsageRecommendation" type="{http://training.gov.au/services/}UsageRecommendation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUsageRecommendation", propOrder = {
    "usageRecommendation"
})
public class ArrayOfUsageRecommendation {

    @XmlElement(name = "UsageRecommendation", nillable = true)
    protected List<UsageRecommendation> usageRecommendation;

    /**
     * Gets the value of the usageRecommendation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usageRecommendation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsageRecommendation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UsageRecommendation }
     * 
     * 
     */
    public List<UsageRecommendation> getUsageRecommendation() {
        if (usageRecommendation == null) {
            usageRecommendation = new ArrayList<UsageRecommendation>();
        }
        return this.usageRecommendation;
    }

}
