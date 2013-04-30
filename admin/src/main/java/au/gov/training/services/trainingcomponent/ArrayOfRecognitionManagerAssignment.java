
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRecognitionManagerAssignment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRecognitionManagerAssignment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RecognitionManagerAssignment" type="{http://training.gov.au/services/}RecognitionManagerAssignment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRecognitionManagerAssignment", propOrder = {
    "recognitionManagerAssignment"
})
public class ArrayOfRecognitionManagerAssignment {

    @XmlElement(name = "RecognitionManagerAssignment", nillable = true)
    protected List<RecognitionManagerAssignment> recognitionManagerAssignment;

    /**
     * Gets the value of the recognitionManagerAssignment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recognitionManagerAssignment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecognitionManagerAssignment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecognitionManagerAssignment }
     * 
     * 
     */
    public List<RecognitionManagerAssignment> getRecognitionManagerAssignment() {
        if (recognitionManagerAssignment == null) {
            recognitionManagerAssignment = new ArrayList<>();
        }
        return this.recognitionManagerAssignment;
    }

}
