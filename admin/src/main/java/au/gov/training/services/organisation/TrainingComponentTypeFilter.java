
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentTypeFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentTypeFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IncludeAccreditedCourse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeAccreditedCourseModule" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeQualification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeSkillSet" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeTrainingPackage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeUnit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeUnitContextualisation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentTypeFilter", propOrder = {
    "includeAccreditedCourse",
    "includeAccreditedCourseModule",
    "includeQualification",
    "includeSkillSet",
    "includeTrainingPackage",
    "includeUnit",
    "includeUnitContextualisation"
})
public class TrainingComponentTypeFilter {

    @XmlElement(name = "IncludeAccreditedCourse")
    protected Boolean includeAccreditedCourse;
    @XmlElement(name = "IncludeAccreditedCourseModule")
    protected Boolean includeAccreditedCourseModule;
    @XmlElement(name = "IncludeQualification")
    protected Boolean includeQualification;
    @XmlElement(name = "IncludeSkillSet")
    protected Boolean includeSkillSet;
    @XmlElement(name = "IncludeTrainingPackage")
    protected Boolean includeTrainingPackage;
    @XmlElement(name = "IncludeUnit")
    protected Boolean includeUnit;
    @XmlElement(name = "IncludeUnitContextualisation")
    protected Boolean includeUnitContextualisation;

    /**
     * Gets the value of the includeAccreditedCourse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeAccreditedCourse() {
        return includeAccreditedCourse;
    }

    /**
     * Sets the value of the includeAccreditedCourse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeAccreditedCourse(Boolean value) {
        this.includeAccreditedCourse = value;
    }

    /**
     * Gets the value of the includeAccreditedCourseModule property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeAccreditedCourseModule() {
        return includeAccreditedCourseModule;
    }

    /**
     * Sets the value of the includeAccreditedCourseModule property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeAccreditedCourseModule(Boolean value) {
        this.includeAccreditedCourseModule = value;
    }

    /**
     * Gets the value of the includeQualification property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeQualification() {
        return includeQualification;
    }

    /**
     * Sets the value of the includeQualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeQualification(Boolean value) {
        this.includeQualification = value;
    }

    /**
     * Gets the value of the includeSkillSet property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeSkillSet() {
        return includeSkillSet;
    }

    /**
     * Sets the value of the includeSkillSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeSkillSet(Boolean value) {
        this.includeSkillSet = value;
    }

    /**
     * Gets the value of the includeTrainingPackage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeTrainingPackage() {
        return includeTrainingPackage;
    }

    /**
     * Sets the value of the includeTrainingPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeTrainingPackage(Boolean value) {
        this.includeTrainingPackage = value;
    }

    /**
     * Gets the value of the includeUnit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeUnit() {
        return includeUnit;
    }

    /**
     * Sets the value of the includeUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeUnit(Boolean value) {
        this.includeUnit = value;
    }

    /**
     * Gets the value of the includeUnitContextualisation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeUnitContextualisation() {
        return includeUnitContextualisation;
    }

    /**
     * Sets the value of the includeUnitContextualisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeUnitContextualisation(Boolean value) {
        this.includeUnitContextualisation = value;
    }

}
