
package ish.oncourse.webservices.v6.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for studentConcessionStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentConcessionStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="authorisationExpiresOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="authorisedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="concessionNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expiresOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="concessionTypeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentConcessionStub", propOrder = {
    "authorisationExpiresOn",
    "authorisedOn",
    "concessionNumber",
    "expiresOn",
    "timeZone",
    "concessionTypeId",
    "studentId"
})
public class StudentConcessionStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date authorisationExpiresOn;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date authorisedOn;
    @XmlElement(required = true)
    protected String concessionNumber;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date expiresOn;
    @XmlElement(required = true)
    protected String timeZone;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long concessionTypeId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long studentId;

    /**
     * Gets the value of the authorisationExpiresOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getAuthorisationExpiresOn() {
        return authorisationExpiresOn;
    }

    /**
     * Sets the value of the authorisationExpiresOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorisationExpiresOn(Date value) {
        this.authorisationExpiresOn = value;
    }

    /**
     * Gets the value of the authorisedOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getAuthorisedOn() {
        return authorisedOn;
    }

    /**
     * Sets the value of the authorisedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorisedOn(Date value) {
        this.authorisedOn = value;
    }

    /**
     * Gets the value of the concessionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConcessionNumber() {
        return concessionNumber;
    }

    /**
     * Sets the value of the concessionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcessionNumber(String value) {
        this.concessionNumber = value;
    }

    /**
     * Gets the value of the expiresOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getExpiresOn() {
        return expiresOn;
    }

    /**
     * Sets the value of the expiresOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiresOn(Date value) {
        this.expiresOn = value;
    }

    /**
     * Gets the value of the timeZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the value of the timeZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeZone(String value) {
        this.timeZone = value;
    }

    /**
     * Gets the value of the concessionTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getConcessionTypeId() {
        return concessionTypeId;
    }

    /**
     * Sets the value of the concessionTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcessionTypeId(Long value) {
        this.concessionTypeId = value;
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

}
