
package ish.oncourse.webservices.v4.stubs.replication;

import org.w3._2001.xmlschema.Adapter2;
import org.w3._2001.xmlschema.Adapter4;
import org.w3._2001.xmlschema.Adapter5;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for studentStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="concessionType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="disabilityType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="englishProficiency" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="highestSchoolLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="indigenousStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="overseasClient" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="stillAtSchool" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="labourForceType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="priorEducationCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="yearSchoolCompleted" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="languageId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="countryOfBirthId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="languageHomeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentStub", propOrder = {
    "concessionType",
    "disabilityType",
    "englishProficiency",
    "highestSchoolLevel",
    "indigenousStatus",
    "overseasClient",
    "stillAtSchool",
    "labourForceType",
    "priorEducationCode",
    "yearSchoolCompleted",
    "contactId",
    "languageId",
    "countryOfBirthId",
    "languageHomeId"
})
public class StudentStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer concessionType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer disabilityType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer englishProficiency;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer highestSchoolLevel;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer indigenousStatus;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean overseasClient;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean stillAtSchool;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer labourForceType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer priorEducationCode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter5 .class)
    @XmlSchemaType(name = "int")
    protected Integer yearSchoolCompleted;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long contactId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long languageId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long countryOfBirthId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long languageHomeId;

    /**
     * Gets the value of the concessionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getConcessionType() {
        return concessionType;
    }

    /**
     * Sets the value of the concessionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcessionType(Integer value) {
        this.concessionType = value;
    }

    /**
     * Gets the value of the disabilityType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getDisabilityType() {
        return disabilityType;
    }

    /**
     * Sets the value of the disabilityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabilityType(Integer value) {
        this.disabilityType = value;
    }

    /**
     * Gets the value of the englishProficiency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getEnglishProficiency() {
        return englishProficiency;
    }

    /**
     * Sets the value of the englishProficiency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnglishProficiency(Integer value) {
        this.englishProficiency = value;
    }

    /**
     * Gets the value of the highestSchoolLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getHighestSchoolLevel() {
        return highestSchoolLevel;
    }

    /**
     * Sets the value of the highestSchoolLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHighestSchoolLevel(Integer value) {
        this.highestSchoolLevel = value;
    }

    /**
     * Gets the value of the indigenousStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getIndigenousStatus() {
        return indigenousStatus;
    }

    /**
     * Sets the value of the indigenousStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndigenousStatus(Integer value) {
        this.indigenousStatus = value;
    }

    /**
     * Gets the value of the overseasClient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isOverseasClient() {
        return overseasClient;
    }

    /**
     * Sets the value of the overseasClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOverseasClient(Boolean value) {
        this.overseasClient = value;
    }

    /**
     * Gets the value of the stillAtSchool property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isStillAtSchool() {
        return stillAtSchool;
    }

    /**
     * Sets the value of the stillAtSchool property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStillAtSchool(Boolean value) {
        this.stillAtSchool = value;
    }

    /**
     * Gets the value of the labourForceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getLabourForceType() {
        return labourForceType;
    }

    /**
     * Sets the value of the labourForceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabourForceType(Integer value) {
        this.labourForceType = value;
    }

    /**
     * Gets the value of the priorEducationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getPriorEducationCode() {
        return priorEducationCode;
    }

    /**
     * Sets the value of the priorEducationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorEducationCode(Integer value) {
        this.priorEducationCode = value;
    }

    /**
     * Gets the value of the yearSchoolCompleted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getYearSchoolCompleted() {
        return yearSchoolCompleted;
    }

    /**
     * Sets the value of the yearSchoolCompleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYearSchoolCompleted(Integer value) {
        this.yearSchoolCompleted = value;
    }

    /**
     * Gets the value of the contactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactId() {
        return contactId;
    }

    /**
     * Sets the value of the contactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactId(Long value) {
        this.contactId = value;
    }

    /**
     * Gets the value of the languageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getLanguageId() {
        return languageId;
    }

    /**
     * Sets the value of the languageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguageId(Long value) {
        this.languageId = value;
    }

    /**
     * Gets the value of the countryOfBirthId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCountryOfBirthId() {
        return countryOfBirthId;
    }

    /**
     * Sets the value of the countryOfBirthId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryOfBirthId(Long value) {
        this.countryOfBirthId = value;
    }

    /**
     * Gets the value of the languageHomeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getLanguageHomeId() {
        return languageHomeId;
    }

    /**
     * Sets the value of the languageHomeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguageHomeId(Long value) {
        this.languageHomeId = value;
    }

}
