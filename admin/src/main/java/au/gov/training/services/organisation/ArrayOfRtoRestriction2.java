
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRtoRestriction2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRtoRestriction2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RtoRestriction2" type="{http://training.gov.au/services/}RtoRestriction2" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRtoRestriction2", propOrder = {
    "rtoRestriction2"
})
public class ArrayOfRtoRestriction2 {

    @XmlElement(name = "RtoRestriction2", nillable = true)
    protected List<RtoRestriction2> rtoRestriction2;

    /**
     * Gets the value of the rtoRestriction2 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rtoRestriction2 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRtoRestriction2().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RtoRestriction2 }
     * 
     * 
     */
    public List<RtoRestriction2> getRtoRestriction2() {
        if (rtoRestriction2 == null) {
            rtoRestriction2 = new ArrayList<RtoRestriction2>();
        }
        return this.rtoRestriction2;
    }

}
