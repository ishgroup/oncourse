
package ish.oncourse.webservices.v16.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for assessmentClassStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assessmentClassStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v16.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="assessmentId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="courseClassId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="dueDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="releaseDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assessmentClassStub", propOrder = {
    "assessmentId",
    "courseClassId",
    "dueDate",
    "releaseDate"
})
public class AssessmentClassStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long assessmentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long courseClassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dueDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date releaseDate;

    /**
     * Gets the value of the assessmentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getAssessmentId() {
        return assessmentId;
    }

    /**
     * Sets the value of the assessmentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssessmentId(Long value) {
        this.assessmentId = value;
    }

    /**
     * Gets the value of the courseClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCourseClassId() {
        return courseClassId;
    }

    /**
     * Sets the value of the courseClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseClassId(Long value) {
        this.courseClassId = value;
    }

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDueDate(Date value) {
        this.dueDate = value;
    }

    /**
     * Gets the value of the releaseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the value of the releaseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReleaseDate(Date value) {
        this.releaseDate = value;
    }

}
