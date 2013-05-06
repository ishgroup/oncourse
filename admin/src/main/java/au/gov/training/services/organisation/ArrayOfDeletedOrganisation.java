
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfDeletedOrganisation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfDeletedOrganisation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DeletedOrganisation" type="{http://training.gov.au/services/}DeletedOrganisation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDeletedOrganisation", propOrder = {
    "deletedOrganisation"
})
public class ArrayOfDeletedOrganisation {

    @XmlElement(name = "DeletedOrganisation", nillable = true)
    protected List<DeletedOrganisation> deletedOrganisation;

    /**
     * Gets the value of the deletedOrganisation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deletedOrganisation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeletedOrganisation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeletedOrganisation }
     * 
     * 
     */
    public List<DeletedOrganisation> getDeletedOrganisation() {
        if (deletedOrganisation == null) {
            deletedOrganisation = new ArrayList<DeletedOrganisation>();
        }
        return this.deletedOrganisation;
    }

}
