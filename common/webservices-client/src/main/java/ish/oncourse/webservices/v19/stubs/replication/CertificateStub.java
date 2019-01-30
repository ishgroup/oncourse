
package ish.oncourse.webservices.v19.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for certificateStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="certificateStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v19.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="certificateNumber" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="fundingSource" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="qualification" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="printedWhen" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="privateNotes" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="publicNotes" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="qualificationId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="revokedWhen" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="studentFirstName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="studentLastName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="issued" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="awarded" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="uniqueCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certificateStub", propOrder = {
    "certificateNumber",
    "endDate",
    "fundingSource",
    "qualification",
    "printedWhen",
    "privateNotes",
    "publicNotes",
    "qualificationId",
    "revokedWhen",
    "studentFirstName",
    "studentLastName",
    "studentId",
    "issued",
    "awarded",
    "uniqueCode"
})
public class CertificateStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long certificateNumber;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer fundingSource;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean qualification;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date printedWhen;
    @XmlElement(required = true)
    protected String privateNotes;
    @XmlElement(required = true)
    protected String publicNotes;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long qualificationId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date revokedWhen;
    @XmlElement(required = true)
    protected String studentFirstName;
    @XmlElement(required = true)
    protected String studentLastName;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long studentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date issued;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date awarded;
    @XmlElement(required = true)
    protected String uniqueCode;

    /**
     * Gets the value of the certificateNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCertificateNumber() {
        return certificateNumber;
    }

    /**
     * Sets the value of the certificateNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificateNumber(Long value) {
        this.certificateNumber = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(Date value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the fundingSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getFundingSource() {
        return fundingSource;
    }

    /**
     * Sets the value of the fundingSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFundingSource(Integer value) {
        this.fundingSource = value;
    }

    /**
     * Gets the value of the qualification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isQualification() {
        return qualification;
    }

    /**
     * Sets the value of the qualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualification(Boolean value) {
        this.qualification = value;
    }

    /**
     * Gets the value of the printedWhen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getPrintedWhen() {
        return printedWhen;
    }

    /**
     * Sets the value of the printedWhen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrintedWhen(Date value) {
        this.printedWhen = value;
    }

    /**
     * Gets the value of the privateNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivateNotes() {
        return privateNotes;
    }

    /**
     * Sets the value of the privateNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivateNotes(String value) {
        this.privateNotes = value;
    }

    /**
     * Gets the value of the publicNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicNotes() {
        return publicNotes;
    }

    /**
     * Sets the value of the publicNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicNotes(String value) {
        this.publicNotes = value;
    }

    /**
     * Gets the value of the qualificationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getQualificationId() {
        return qualificationId;
    }

    /**
     * Sets the value of the qualificationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationId(Long value) {
        this.qualificationId = value;
    }

    /**
     * Gets the value of the revokedWhen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getRevokedWhen() {
        return revokedWhen;
    }

    /**
     * Sets the value of the revokedWhen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevokedWhen(Date value) {
        this.revokedWhen = value;
    }

    /**
     * Gets the value of the studentFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentFirstName() {
        return studentFirstName;
    }

    /**
     * Sets the value of the studentFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentFirstName(String value) {
        this.studentFirstName = value;
    }

    /**
     * Gets the value of the studentLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentLastName() {
        return studentLastName;
    }

    /**
     * Sets the value of the studentLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentLastName(String value) {
        this.studentLastName = value;
    }

    /**
     * Gets the value of the studentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Sets the value of the studentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentId(Long value) {
        this.studentId = value;
    }

    /**
     * Gets the value of the issued property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getIssued() {
        return issued;
    }

    /**
     * Sets the value of the issued property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssued(Date value) {
        this.issued = value;
    }

    /**
     * Gets the value of the awarded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getAwarded() {
        return awarded;
    }

    /**
     * Sets the value of the awarded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAwarded(Date value) {
        this.awarded = value;
    }

    /**
     * Gets the value of the uniqueCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
     * Sets the value of the uniqueCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueCode(String value) {
        this.uniqueCode = value;
    }

}
