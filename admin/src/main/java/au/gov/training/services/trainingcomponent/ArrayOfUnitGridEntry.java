
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUnitGridEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUnitGridEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UnitGridEntry" type="{http://training.gov.au/services/}UnitGridEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUnitGridEntry", propOrder = {
    "unitGridEntry"
})
public class ArrayOfUnitGridEntry {

    @XmlElement(name = "UnitGridEntry", nillable = true)
    protected List<UnitGridEntry> unitGridEntry;

    /**
     * Gets the value of the unitGridEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unitGridEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnitGridEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnitGridEntry }
     * 
     * 
     */
    public List<UnitGridEntry> getUnitGridEntry() {
        if (unitGridEntry == null) {
            unitGridEntry = new ArrayList<UnitGridEntry>();
        }
        return this.unitGridEntry;
    }

}
