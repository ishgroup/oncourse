
package ish.oncourse.webservices.v16.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for studentStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v16.soap.webservices.oncourse.ish/}replicationStub">
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
 *         &lt;element name="chessn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="feeHelpEligible" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="specialNeedsAssistance" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="citizenship" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="specialNeeds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="townOfBirth" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usi" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usiStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "languageHomeId",
    "chessn",
    "feeHelpEligible",
    "specialNeedsAssistance",
    "citizenship",
    "specialNeeds",
    "townOfBirth",
    "usi",
    "usiStatus"
})
public class StudentStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer concessionType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer disabilityType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer englishProficiency;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer highestSchoolLevel;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
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
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer labourForceType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer priorEducationCode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
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
    @XmlElement(required = true)
    protected String chessn;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean feeHelpEligible;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean specialNeedsAssistance;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer citizenship;
    @XmlElement(required = true)
    protected String specialNeeds;
    @XmlElement(required = true)
    protected String townOfBirth;
    @XmlElement(required = true)
    protected String usi;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer usiStatus;

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

    /**
     * Gets the value of the chessn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChessn() {
        return chessn;
    }

    /**
     * Sets the value of the chessn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChessn(String value) {
        this.chessn = value;
    }

    /**
     * Gets the value of the feeHelpEligible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isFeeHelpEligible() {
        return feeHelpEligible;
    }

    /**
     * Sets the value of the feeHelpEligible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeHelpEligible(Boolean value) {
        this.feeHelpEligible = value;
    }

    /**
     * Gets the value of the specialNeedsAssistance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isSpecialNeedsAssistance() {
        return specialNeedsAssistance;
    }

    /**
     * Sets the value of the specialNeedsAssistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialNeedsAssistance(Boolean value) {
        this.specialNeedsAssistance = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenship(Integer value) {
        this.citizenship = value;
    }

    /**
     * Gets the value of the specialNeeds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialNeeds() {
        return specialNeeds;
    }

    /**
     * Sets the value of the specialNeeds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialNeeds(String value) {
        this.specialNeeds = value;
    }

    /**
     * Gets the value of the townOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTownOfBirth() {
        return townOfBirth;
    }

    /**
     * Sets the value of the townOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTownOfBirth(String value) {
        this.townOfBirth = value;
    }

    /**
     * Gets the value of the usi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsi() {
        return usi;
    }

    /**
     * Sets the value of the usi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsi(String value) {
        this.usi = value;
    }

    /**
     * Gets the value of the usiStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getUsiStatus() {
        return usiStatus;
    }

    /**
     * Sets the value of the usiStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsiStatus(Integer value) {
        this.usiStatus = value;
    }

}
