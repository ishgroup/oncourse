
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GenericListPropertyOfContactContact8TYN1aE7 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GenericListPropertyOfContactContact8TYN1aE7">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActionOnCollection" type="{http://training.gov.au/services/}ActionOnCollection"/>
 *         &lt;element name="List" type="{http://training.gov.au/services/}ArrayOfContact"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericListPropertyOfContactContact8TYN1aE7", propOrder = {
    "actionOnCollection",
    "list"
})
@XmlSeeAlso({
    ContactList.class
})
public class GenericListPropertyOfContactContact8TYN1AE7 {

    @XmlElement(name = "ActionOnCollection", required = true)
    protected ActionOnCollection actionOnCollection;
    @XmlElement(name = "List", required = true, nillable = true)
    protected ArrayOfContact list;

    /**
     * Gets the value of the actionOnCollection property.
     * 
     * @return
     *     possible object is
     *     {@link ActionOnCollection }
     *     
     */
    public ActionOnCollection getActionOnCollection() {
        return actionOnCollection;
    }

    /**
     * Sets the value of the actionOnCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionOnCollection }
     *     
     */
    public void setActionOnCollection(ActionOnCollection value) {
        this.actionOnCollection = value;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfContact }
     *     
     */
    public ArrayOfContact getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfContact }
     *     
     */
    public void setList(ArrayOfContact value) {
        this.list = value;
    }

}
