
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRegistrationManagerAssignment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRegistrationManagerAssignment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RegistrationManagerAssignment" type="{http://training.gov.au/services/}RegistrationManagerAssignment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRegistrationManagerAssignment", propOrder = {
    "registrationManagerAssignment"
})
public class ArrayOfRegistrationManagerAssignment {

    @XmlElement(name = "RegistrationManagerAssignment", nillable = true)
    protected List<RegistrationManagerAssignment> registrationManagerAssignment;

    /**
     * Gets the value of the registrationManagerAssignment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registrationManagerAssignment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistrationManagerAssignment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistrationManagerAssignment }
     * 
     * 
     */
    public List<RegistrationManagerAssignment> getRegistrationManagerAssignment() {
        if (registrationManagerAssignment == null) {
            registrationManagerAssignment = new ArrayList<RegistrationManagerAssignment>();
        }
        return this.registrationManagerAssignment;
    }

}
