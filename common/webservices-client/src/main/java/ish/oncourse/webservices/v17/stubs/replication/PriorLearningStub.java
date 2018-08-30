
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for priorLearningStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="priorLearningStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v17.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="externalRef" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="outcomeIdTrainingOrg" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="qualificationId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "priorLearningStub", propOrder = {
    "externalRef",
    "notes",
    "outcomeIdTrainingOrg",
    "qualificationId",
    "studentId",
    "title"
})
public class PriorLearningStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String externalRef;
    @XmlElement(required = true)
    protected String notes;
    @XmlElement(required = true)
    protected String outcomeIdTrainingOrg;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long qualificationId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long studentId;
    @XmlElement(required = true)
    protected String title;

    /**
     * Gets the value of the externalRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalRef() {
        return externalRef;
    }

    /**
     * Sets the value of the externalRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalRef(String value) {
        this.externalRef = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the outcomeIdTrainingOrg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutcomeIdTrainingOrg() {
        return outcomeIdTrainingOrg;
    }

    /**
     * Sets the value of the outcomeIdTrainingOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutcomeIdTrainingOrg(String value) {
        this.outcomeIdTrainingOrg = value;
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

}
