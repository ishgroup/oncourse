
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfNrtClassificationSchemeResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfNrtClassificationSchemeResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NrtClassificationSchemeResult" type="{http://training.gov.au/services/}NrtClassificationSchemeResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfNrtClassificationSchemeResult", propOrder = {
    "nrtClassificationSchemeResult"
})
public class ArrayOfNrtClassificationSchemeResult {

    @XmlElement(name = "NrtClassificationSchemeResult", nillable = true)
    protected List<NrtClassificationSchemeResult> nrtClassificationSchemeResult;

    /**
     * Gets the value of the nrtClassificationSchemeResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nrtClassificationSchemeResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNrtClassificationSchemeResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NrtClassificationSchemeResult }
     * 
     * 
     */
    public List<NrtClassificationSchemeResult> getNrtClassificationSchemeResult() {
        if (nrtClassificationSchemeResult == null) {
            nrtClassificationSchemeResult = new ArrayList<>();
        }
        return this.nrtClassificationSchemeResult;
    }

}
