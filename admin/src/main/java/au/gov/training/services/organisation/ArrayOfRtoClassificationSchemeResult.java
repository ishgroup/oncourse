
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRtoClassificationSchemeResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRtoClassificationSchemeResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RtoClassificationSchemeResult" type="{http://training.gov.au/services/}RtoClassificationSchemeResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRtoClassificationSchemeResult", propOrder = {
    "rtoClassificationSchemeResult"
})
public class ArrayOfRtoClassificationSchemeResult {

    @XmlElement(name = "RtoClassificationSchemeResult", nillable = true)
    protected List<RtoClassificationSchemeResult> rtoClassificationSchemeResult;

    /**
     * Gets the value of the rtoClassificationSchemeResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rtoClassificationSchemeResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRtoClassificationSchemeResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RtoClassificationSchemeResult }
     * 
     * 
     */
    public List<RtoClassificationSchemeResult> getRtoClassificationSchemeResult() {
        if (rtoClassificationSchemeResult == null) {
            rtoClassificationSchemeResult = new ArrayList<RtoClassificationSchemeResult>();
        }
        return this.rtoClassificationSchemeResult;
    }

}
