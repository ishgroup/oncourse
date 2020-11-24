
package ish.oncourse.webservices.v23.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for qualificationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qualificationStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="anzsco" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="anzsic" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="asco" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fieldOfEducation" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fieldOfStudy" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="isAccreditedCourse" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ishVersion" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="level" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nationalCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="newApprentices" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nominalHours" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="reviewDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="specialisation" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="trainingPackageId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qualificationStub", propOrder = {
    "anzsco",
    "anzsic",
    "asco",
    "fieldOfEducation",
    "fieldOfStudy",
    "isAccreditedCourse",
    "ishVersion",
    "level",
    "nationalCode",
    "newApprentices",
    "nominalHours",
    "reviewDate",
    "specialisation",
    "title",
    "trainingPackageId"
})
public class QualificationStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String anzsco;
    @XmlElement(required = true)
    protected String anzsic;
    @XmlElement(required = true)
    protected String asco;
    @XmlElement(required = true)
    protected String fieldOfEducation;
    @XmlElement(required = true)
    protected String fieldOfStudy;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer isAccreditedCourse;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long ishVersion;
    @XmlElement(required = true)
    protected String level;
    @XmlElement(required = true)
    protected String nationalCode;
    @XmlElement(required = true)
    protected String newApprentices;
    protected Float nominalHours;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date reviewDate;
    @XmlElement(required = true)
    protected String specialisation;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long trainingPackageId;

    /**
     * Gets the value of the anzsco property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnzsco() {
        return anzsco;
    }

    /**
     * Sets the value of the anzsco property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnzsco(String value) {
        this.anzsco = value;
    }

    /**
     * Gets the value of the anzsic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnzsic() {
        return anzsic;
    }

    /**
     * Sets the value of the anzsic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnzsic(String value) {
        this.anzsic = value;
    }

    /**
     * Gets the value of the asco property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsco() {
        return asco;
    }

    /**
     * Sets the value of the asco property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsco(String value) {
        this.asco = value;
    }

    /**
     * Gets the value of the fieldOfEducation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldOfEducation() {
        return fieldOfEducation;
    }

    /**
     * Sets the value of the fieldOfEducation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldOfEducation(String value) {
        this.fieldOfEducation = value;
    }

    /**
     * Gets the value of the fieldOfStudy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    /**
     * Sets the value of the fieldOfStudy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldOfStudy(String value) {
        this.fieldOfStudy = value;
    }

    /**
     * Gets the value of the isAccreditedCourse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getIsAccreditedCourse() {
        return isAccreditedCourse;
    }

    /**
     * Sets the value of the isAccreditedCourse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsAccreditedCourse(Integer value) {
        this.isAccreditedCourse = value;
    }

    /**
     * Gets the value of the ishVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getIshVersion() {
        return ishVersion;
    }

    /**
     * Sets the value of the ishVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIshVersion(Long value) {
        this.ishVersion = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel(String value) {
        this.level = value;
    }

    /**
     * Gets the value of the nationalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationalCode() {
        return nationalCode;
    }

    /**
     * Sets the value of the nationalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationalCode(String value) {
        this.nationalCode = value;
    }

    /**
     * Gets the value of the newApprentices property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewApprentices() {
        return newApprentices;
    }

    /**
     * Sets the value of the newApprentices property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewApprentices(String value) {
        this.newApprentices = value;
    }

    /**
     * Gets the value of the nominalHours property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getNominalHours() {
        return nominalHours;
    }

    /**
     * Sets the value of the nominalHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setNominalHours(Float value) {
        this.nominalHours = value;
    }

    /**
     * Gets the value of the reviewDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getReviewDate() {
        return reviewDate;
    }

    /**
     * Sets the value of the reviewDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReviewDate(Date value) {
        this.reviewDate = value;
    }

    /**
     * Gets the value of the specialisation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialisation() {
        return specialisation;
    }

    /**
     * Sets the value of the specialisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialisation(String value) {
        this.specialisation = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the trainingPackageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTrainingPackageId() {
        return trainingPackageId;
    }

    /**
     * Sets the value of the trainingPackageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrainingPackageId(Long value) {
        this.trainingPackageId = value;
    }

}
