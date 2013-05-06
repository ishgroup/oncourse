
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Mapping" type="{http://training.gov.au/services/}Mapping" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfMapping", propOrder = {
    "mapping"
})
public class ArrayOfMapping {

    @XmlElement(name = "Mapping", nillable = true)
    protected List<Mapping> mapping;

    /**
     * Gets the value of the mapping property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mapping property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapping().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mapping }
     * 
     * 
     */
    public List<Mapping> getMapping() {
        if (mapping == null) {
            mapping = new ArrayList<Mapping>();
        }
        return this.mapping;
    }

}
