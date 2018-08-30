
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for surveyStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="surveyStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v17.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="courseScore" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="tutorScore" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="venueScore" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="enrolmentId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="visibility" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="testimonial" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="netPromoterScore" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "surveyStub", propOrder = {
    "comment",
    "courseScore",
    "tutorScore",
    "venueScore",
    "enrolmentId",
    "visibility",
    "testimonial",
    "netPromoterScore"
})
public class SurveyStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String comment;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer courseScore;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer tutorScore;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer venueScore;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long enrolmentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer visibility;
    @XmlElement(required = true)
    protected String testimonial;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer netPromoterScore;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the courseScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCourseScore() {
        return courseScore;
    }

    /**
     * Sets the value of the courseScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseScore(Integer value) {
        this.courseScore = value;
    }

    /**
     * Gets the value of the tutorScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getTutorScore() {
        return tutorScore;
    }

    /**
     * Sets the value of the tutorScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTutorScore(Integer value) {
        this.tutorScore = value;
    }

    /**
     * Gets the value of the venueScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getVenueScore() {
        return venueScore;
    }

    /**
     * Sets the value of the venueScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVenueScore(Integer value) {
        this.venueScore = value;
    }

    /**
     * Gets the value of the enrolmentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getEnrolmentId() {
        return enrolmentId;
    }

    /**
     * Sets the value of the enrolmentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrolmentId(Long value) {
        this.enrolmentId = value;
    }

    /**
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(Integer value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the testimonial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestimonial() {
        return testimonial;
    }

    /**
     * Sets the value of the testimonial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestimonial(String value) {
        this.testimonial = value;
    }

    /**
     * Gets the value of the netPromoterScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getNetPromoterScore() {
        return netPromoterScore;
    }

    /**
     * Sets the value of the netPromoterScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetPromoterScore(Integer value) {
        this.netPromoterScore = value;
    }

}
