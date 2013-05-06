
package au.gov.training.services.organisation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfClassificationPurpose complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfClassificationPurpose">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClassificationPurpose" type="{http://training.gov.au/services/}ClassificationPurpose" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfClassificationPurpose", propOrder = {
    "classificationPurpose"
})
public class ArrayOfClassificationPurpose {

    @XmlElement(name = "ClassificationPurpose", nillable = true)
    protected List<ClassificationPurpose> classificationPurpose;

    /**
     * Gets the value of the classificationPurpose property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classificationPurpose property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassificationPurpose().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassificationPurpose }
     * 
     * 
     */
    public List<ClassificationPurpose> getClassificationPurpose() {
        if (classificationPurpose == null) {
            classificationPurpose = new ArrayList<ClassificationPurpose>();
        }
        return this.classificationPurpose;
    }

}
