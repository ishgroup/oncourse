
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for assessmentClassTutorStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assessmentClassTutorStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v17.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="assessmentClassId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="tutorId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assessmentClassTutorStub", propOrder = {
    "assessmentClassId",
    "tutorId"
})
public class AssessmentClassTutorStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long assessmentClassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long tutorId;

    /**
     * Gets the value of the assessmentClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getAssessmentClassId() {
        return assessmentClassId;
    }

    /**
     * Sets the value of the assessmentClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssessmentClassId(Long value) {
        this.assessmentClassId = value;
    }

    /**
     * Gets the value of the tutorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTutorId() {
        return tutorId;
    }

    /**
     * Sets the value of the tutorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTutorId(Long value) {
        this.tutorId = value;
    }

}
