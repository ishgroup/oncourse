
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRecognitionManager complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRecognitionManager">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RecognitionManager" type="{http://training.gov.au/services/}RecognitionManager" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRecognitionManager", propOrder = {
    "recognitionManager"
})
public class ArrayOfRecognitionManager {

    @XmlElement(name = "RecognitionManager", nillable = true)
    protected List<RecognitionManager> recognitionManager;

    /**
     * Gets the value of the recognitionManager property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recognitionManager property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecognitionManager().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecognitionManager }
     * 
     * 
     */
    public List<RecognitionManager> getRecognitionManager() {
        if (recognitionManager == null) {
            recognitionManager = new ArrayList<>();
        }
        return this.recognitionManager;
    }

}
