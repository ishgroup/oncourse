
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfOrganisationSearchResultItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfOrganisationSearchResultItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrganisationSearchResultItem" type="{http://training.gov.au/services/}OrganisationSearchResultItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfOrganisationSearchResultItem", propOrder = {
    "organisationSearchResultItem"
})
public class ArrayOfOrganisationSearchResultItem {

    @XmlElement(name = "OrganisationSearchResultItem", nillable = true)
    protected List<OrganisationSearchResultItem> organisationSearchResultItem;

    /**
     * Gets the value of the organisationSearchResultItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the organisationSearchResultItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrganisationSearchResultItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrganisationSearchResultItem }
     * 
     * 
     */
    public List<OrganisationSearchResultItem> getOrganisationSearchResultItem() {
        if (organisationSearchResultItem == null) {
            organisationSearchResultItem = new ArrayList<OrganisationSearchResultItem>();
        }
        return this.organisationSearchResultItem;
    }

}
