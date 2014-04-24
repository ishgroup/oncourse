
package ish.oncourse.webservices.v6.stubs.replication;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;
import org.w3._2001.xmlschema.Adapter2;
import org.w3._2001.xmlschema.Adapter3;
import org.w3._2001.xmlschema.Adapter4;


/**
 * <p>Java class for courseClassStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="courseClassStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="cancelled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="countOfSessions" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="deliveryMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="detail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="detailTextile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="feeExGst" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="feeGst" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="webVisible" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="materials" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialsTextile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maximumPlaces" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="minimumPlaces" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="minutesPerSession" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sessionDetail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sessionDetailTextile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="startingMinutePerSession" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="courseId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="roomId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="maximumDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="expectedHours" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="distantLearningCourse" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="feeHelpClass" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="attendanceType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="reportingPeriod" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="censusDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fullTimeLoad" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "courseClassStub", propOrder = {
    "cancelled",
    "code",
    "countOfSessions",
    "deliveryMode",
    "detail",
    "detailTextile",
    "endDate",
    "feeExGst",
    "feeGst",
    "webVisible",
    "materials",
    "materialsTextile",
    "maximumPlaces",
    "minimumPlaces",
    "minutesPerSession",
    "sessionDetail",
    "sessionDetailTextile",
    "startDate",
    "startingMinutePerSession",
    "timeZone",
    "courseId",
    "roomId",
    "maximumDays",
    "expectedHours",
    "distantLearningCourse",
    "feeHelpClass",
    "attendanceType",
    "reportingPeriod",
    "censusDate",
    "fullTimeLoad"
})
public class CourseClassStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean cancelled;
    @XmlElement(required = true)
    protected String code;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer countOfSessions;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer deliveryMode;
    @XmlElement(required = true)
    protected String detail;
    @XmlElement(required = true)
    protected String detailTextile;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;
    @XmlElement(required = true)
    protected BigDecimal feeExGst;
    @XmlElement(required = true)
    protected BigDecimal feeGst;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean webVisible;
    @XmlElement(required = true)
    protected String materials;
    @XmlElement(required = true)
    protected String materialsTextile;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer maximumPlaces;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer minimumPlaces;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer minutesPerSession;
    @XmlElement(required = true)
    protected String sessionDetail;
    @XmlElement(required = true)
    protected String sessionDetailTextile;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer startingMinutePerSession;
    @XmlElement(required = true)
    protected String timeZone;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long courseId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long roomId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer maximumDays;
    @XmlElement(required = true)
    protected BigDecimal expectedHours;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean distantLearningCourse;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean feeHelpClass;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer attendanceType;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "int")
    protected Integer reportingPeriod;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date censusDate;
    @XmlElement(required = true)
    protected String fullTimeLoad;

    /**
     * Gets the value of the cancelled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the value of the cancelled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancelled(Boolean value) {
        this.cancelled = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the countOfSessions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCountOfSessions() {
        return countOfSessions;
    }

    /**
     * Sets the value of the countOfSessions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountOfSessions(Integer value) {
        this.countOfSessions = value;
    }

    /**
     * Gets the value of the deliveryMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getDeliveryMode() {
        return deliveryMode;
    }

    /**
     * Sets the value of the deliveryMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryMode(Integer value) {
        this.deliveryMode = value;
    }

    /**
     * Gets the value of the detail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetail(String value) {
        this.detail = value;
    }

    /**
     * Gets the value of the detailTextile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetailTextile() {
        return detailTextile;
    }

    /**
     * Sets the value of the detailTextile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetailTextile(String value) {
        this.detailTextile = value;
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
     * Gets the value of the feeExGst property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFeeExGst() {
        return feeExGst;
    }

    /**
     * Sets the value of the feeExGst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFeeExGst(BigDecimal value) {
        this.feeExGst = value;
    }

    /**
     * Gets the value of the feeGst property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFeeGst() {
        return feeGst;
    }

    /**
     * Sets the value of the feeGst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFeeGst(BigDecimal value) {
        this.feeGst = value;
    }

    /**
     * Gets the value of the webVisible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isWebVisible() {
        return webVisible;
    }

    /**
     * Sets the value of the webVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebVisible(Boolean value) {
        this.webVisible = value;
    }

    /**
     * Gets the value of the materials property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterials() {
        return materials;
    }

    /**
     * Sets the value of the materials property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterials(String value) {
        this.materials = value;
    }

    /**
     * Gets the value of the materialsTextile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialsTextile() {
        return materialsTextile;
    }

    /**
     * Sets the value of the materialsTextile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialsTextile(String value) {
        this.materialsTextile = value;
    }

    /**
     * Gets the value of the maximumPlaces property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getMaximumPlaces() {
        return maximumPlaces;
    }

    /**
     * Sets the value of the maximumPlaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumPlaces(Integer value) {
        this.maximumPlaces = value;
    }

    /**
     * Gets the value of the minimumPlaces property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getMinimumPlaces() {
        return minimumPlaces;
    }

    /**
     * Sets the value of the minimumPlaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimumPlaces(Integer value) {
        this.minimumPlaces = value;
    }

    /**
     * Gets the value of the minutesPerSession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getMinutesPerSession() {
        return minutesPerSession;
    }

    /**
     * Sets the value of the minutesPerSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinutesPerSession(Integer value) {
        this.minutesPerSession = value;
    }

    /**
     * Gets the value of the sessionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionDetail() {
        return sessionDetail;
    }

    /**
     * Sets the value of the sessionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionDetail(String value) {
        this.sessionDetail = value;
    }

    /**
     * Gets the value of the sessionDetailTextile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionDetailTextile() {
        return sessionDetailTextile;
    }

    /**
     * Sets the value of the sessionDetailTextile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionDetailTextile(String value) {
        this.sessionDetailTextile = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(Date value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the startingMinutePerSession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getStartingMinutePerSession() {
        return startingMinutePerSession;
    }

    /**
     * Sets the value of the startingMinutePerSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartingMinutePerSession(Integer value) {
        this.startingMinutePerSession = value;
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
     * Gets the value of the courseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseId(Long value) {
        this.courseId = value;
    }

    /**
     * Gets the value of the roomId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getRoomId() {
        return roomId;
    }

    /**
     * Sets the value of the roomId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomId(Long value) {
        this.roomId = value;
    }

    /**
     * Gets the value of the maximumDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getMaximumDays() {
        return maximumDays;
    }

    /**
     * Sets the value of the maximumDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumDays(Integer value) {
        this.maximumDays = value;
    }

    /**
     * Gets the value of the expectedHours property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getExpectedHours() {
        return expectedHours;
    }

    /**
     * Sets the value of the expectedHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setExpectedHours(BigDecimal value) {
        this.expectedHours = value;
    }

    /**
     * Gets the value of the distantLearningCourse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isDistantLearningCourse() {
        return distantLearningCourse;
    }

    /**
     * Sets the value of the distantLearningCourse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistantLearningCourse(Boolean value) {
        this.distantLearningCourse = value;
    }

    /**
     * Gets the value of the feeHelpClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isFeeHelpClass() {
        return feeHelpClass;
    }

    /**
     * Sets the value of the feeHelpClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeHelpClass(Boolean value) {
        this.feeHelpClass = value;
    }

    /**
     * Gets the value of the attendanceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getAttendanceType() {
        return attendanceType;
    }

    /**
     * Sets the value of the attendanceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttendanceType(Integer value) {
        this.attendanceType = value;
    }

    /**
     * Gets the value of the reportingPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getReportingPeriod() {
        return reportingPeriod;
    }

    /**
     * Sets the value of the reportingPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportingPeriod(Integer value) {
        this.reportingPeriod = value;
    }

    /**
     * Gets the value of the censusDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getCensusDate() {
        return censusDate;
    }

    /**
     * Sets the value of the censusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCensusDate(Date value) {
        this.censusDate = value;
    }

    /**
     * Gets the value of the fullTimeLoad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullTimeLoad() {
        return fullTimeLoad;
    }

    /**
     * Sets the value of the fullTimeLoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullTimeLoad(String value) {
        this.fullTimeLoad = value;
    }

}
