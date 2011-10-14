
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfTrainingComponentSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTrainingComponentSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TrainingComponentSummary" type="{http://training.gov.au/services/}TrainingComponentSummary" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTrainingComponentSummary", propOrder = {
    "trainingComponentSummary"
})
public class ArrayOfTrainingComponentSummary {

    @XmlElement(name = "TrainingComponentSummary", nillable = true)
    protected List<TrainingComponentSummary> trainingComponentSummary;

    /**
     * Gets the value of the trainingComponentSummary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trainingComponentSummary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrainingComponentSummary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrainingComponentSummary }
     * 
     * 
     */
    public List<TrainingComponentSummary> getTrainingComponentSummary() {
        if (trainingComponentSummary == null) {
            trainingComponentSummary = new ArrayList<TrainingComponentSummary>();
        }
        return this.trainingComponentSummary;
    }

}
