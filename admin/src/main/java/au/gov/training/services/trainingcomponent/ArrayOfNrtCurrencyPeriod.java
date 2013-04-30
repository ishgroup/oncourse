
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfNrtCurrencyPeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfNrtCurrencyPeriod">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NrtCurrencyPeriod" type="{http://training.gov.au/services/}NrtCurrencyPeriod" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfNrtCurrencyPeriod", propOrder = {
    "nrtCurrencyPeriod"
})
public class ArrayOfNrtCurrencyPeriod {

    @XmlElement(name = "NrtCurrencyPeriod", nillable = true)
    protected List<NrtCurrencyPeriod> nrtCurrencyPeriod;

    /**
     * Gets the value of the nrtCurrencyPeriod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nrtCurrencyPeriod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNrtCurrencyPeriod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NrtCurrencyPeriod }
     * 
     * 
     */
    public List<NrtCurrencyPeriod> getNrtCurrencyPeriod() {
        if (nrtCurrencyPeriod == null) {
            nrtCurrencyPeriod = new ArrayList<>();
        }
        return this.nrtCurrencyPeriod;
    }

}
