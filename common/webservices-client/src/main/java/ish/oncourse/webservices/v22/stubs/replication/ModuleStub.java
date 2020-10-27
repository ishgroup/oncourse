
package ish.oncourse.webservices.v22.stubs.replication;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for moduleStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moduleStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v21.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="creditPoints" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="disciplineCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="expiryDays" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fieldOfEducation" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ishVersion" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="nationalCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
@XmlType(name = "moduleStub", propOrder = {
    "creditPoints",
    "disciplineCode",
    "expiryDays",
    "fieldOfEducation",
    "type",
    "ishVersion",
    "nationalCode",
    "specialisation",
    "title",
    "trainingPackageId"
})
public class ModuleStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected BigDecimal creditPoints;
    @XmlElement(required = true)
    protected String disciplineCode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer expiryDays;
    @XmlElement(required = true)
    protected String fieldOfEducation;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer type;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long ishVersion;
    @XmlElement(required = true)
    protected String nationalCode;
    @XmlElement(required = true)
    protected String specialisation;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long trainingPackageId;

    /**
     * Gets the value of the creditPoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCreditPoints() {
        return creditPoints;
    }

    /**
     * Sets the value of the creditPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCreditPoints(BigDecimal value) {
        this.creditPoints = value;
    }

    /**
     * Gets the value of the disciplineCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisciplineCode() {
        return disciplineCode;
    }

    /**
     * Sets the value of the disciplineCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisciplineCode(String value) {
        this.disciplineCode = value;
    }

    /**
     * Gets the value of the expiryDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getExpiryDays() {
        return expiryDays;
    }

    /**
     * Sets the value of the expiryDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDays(Integer value) {
        this.expiryDays = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(Integer value) {
        this.type = value;
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
