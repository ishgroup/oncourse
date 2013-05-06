
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfDeletedTrainingComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfDeletedTrainingComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DeletedTrainingComponent" type="{http://training.gov.au/services/}DeletedTrainingComponent" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDeletedTrainingComponent", propOrder = {
    "deletedTrainingComponent"
})
public class ArrayOfDeletedTrainingComponent {

    @XmlElement(name = "DeletedTrainingComponent", nillable = true)
    protected List<DeletedTrainingComponent> deletedTrainingComponent;

    /**
     * Gets the value of the deletedTrainingComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deletedTrainingComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeletedTrainingComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeletedTrainingComponent }
     * 
     * 
     */
    public List<DeletedTrainingComponent> getDeletedTrainingComponent() {
        if (deletedTrainingComponent == null) {
            deletedTrainingComponent = new ArrayList<DeletedTrainingComponent>();
        }
        return this.deletedTrainingComponent;
    }

}
