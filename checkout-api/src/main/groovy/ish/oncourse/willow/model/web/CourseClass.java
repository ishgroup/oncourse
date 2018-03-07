package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.web.Course;
import ish.oncourse.willow.model.web.CourseClassPrice;
import ish.oncourse.willow.model.web.Room;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ish.oncourse.util.FormatUtils;

public class CourseClass  {
  
    private String id = null;
    private Course course = null;
    private String code = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatUtils.DATE_FORMAT_ISO8601)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime start = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatUtils.DATE_FORMAT_ISO8601)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime end = null;
    private Boolean hasAvailablePlaces = null;
    private Integer availableEnrolmentPlaces = null;
    private Boolean isFinished = null;
    private Boolean isCancelled = null;
    private Boolean isAllowByApplication = null;
    private Boolean isPaymentGatewayEnabled = null;
    private Boolean distantLearning = null;
    private CourseClassPrice price = null;
    private Room room = null;
    private String timezone = null;

    /**
     * Internal Unique identifier of class
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public CourseClass id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Get course
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
       this.course = course;
    }

    public CourseClass course(Course course) {
      this.course = course;
      return this;
    }

    /**
     * Code of class
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
       this.code = code;
    }

    public CourseClass code(String code) {
      this.code = code;
      return this;
    }

    /**
     * Class start date and time in UTC time zone (+00:00),date format is ISO 8601
     * @return start
     */
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
       this.start = start;
    }

    public CourseClass start(LocalDateTime start) {
      this.start = start;
      return this;
    }

    /**
     * Class start date and time in UTC time zone (+00:00),date format is ISO 8601
     * @return end
     */
    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
       this.end = end;
    }

    public CourseClass end(LocalDateTime end) {
      this.end = end;
      return this;
    }

    /**
     * Available places
     * @return hasAvailablePlaces
     */
    public Boolean getHasAvailablePlaces() {
        return hasAvailablePlaces;
    }

    public void setHasAvailablePlaces(Boolean hasAvailablePlaces) {
       this.hasAvailablePlaces = hasAvailablePlaces;
    }

    public CourseClass hasAvailablePlaces(Boolean hasAvailablePlaces) {
      this.hasAvailablePlaces = hasAvailablePlaces;
      return this;
    }

    /**
     * Number of free places
     * @return availableEnrolmentPlaces
     */
    public Integer getAvailableEnrolmentPlaces() {
        return availableEnrolmentPlaces;
    }

    public void setAvailableEnrolmentPlaces(Integer availableEnrolmentPlaces) {
       this.availableEnrolmentPlaces = availableEnrolmentPlaces;
    }

    public CourseClass availableEnrolmentPlaces(Integer availableEnrolmentPlaces) {
      this.availableEnrolmentPlaces = availableEnrolmentPlaces;
      return this;
    }

    /**
     * Is class finished
     * @return isFinished
     */
    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean isFinished) {
       this.isFinished = isFinished;
    }

    public CourseClass isFinished(Boolean isFinished) {
      this.isFinished = isFinished;
      return this;
    }

    /**
     * Is class cancelled
     * @return isCancelled
     */
    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
       this.isCancelled = isCancelled;
    }

    public CourseClass isCancelled(Boolean isCancelled) {
      this.isCancelled = isCancelled;
      return this;
    }

    /**
     * Allow by application
     * @return isAllowByApplication
     */
    public Boolean getIsAllowByApplication() {
        return isAllowByApplication;
    }

    public void setIsAllowByApplication(Boolean isAllowByApplication) {
       this.isAllowByApplication = isAllowByApplication;
    }

    public CourseClass isAllowByApplication(Boolean isAllowByApplication) {
      this.isAllowByApplication = isAllowByApplication;
      return this;
    }

    /**
     * Is payment gateway enabled
     * @return isPaymentGatewayEnabled
     */
    public Boolean getIsPaymentGatewayEnabled() {
        return isPaymentGatewayEnabled;
    }

    public void setIsPaymentGatewayEnabled(Boolean isPaymentGatewayEnabled) {
       this.isPaymentGatewayEnabled = isPaymentGatewayEnabled;
    }

    public CourseClass isPaymentGatewayEnabled(Boolean isPaymentGatewayEnabled) {
      this.isPaymentGatewayEnabled = isPaymentGatewayEnabled;
      return this;
    }

    /**
     * Self paced class
     * @return distantLearning
     */
    public Boolean getDistantLearning() {
        return distantLearning;
    }

    public void setDistantLearning(Boolean distantLearning) {
       this.distantLearning = distantLearning;
    }

    public CourseClass distantLearning(Boolean distantLearning) {
      this.distantLearning = distantLearning;
      return this;
    }

    /**
     * Prices attached to current course class
     * @return price
     */
    public CourseClassPrice getPrice() {
        return price;
    }

    public void setPrice(CourseClassPrice price) {
       this.price = price;
    }

    public CourseClass price(CourseClassPrice price) {
      this.price = price;
      return this;
    }

    /**
     * Room for the site
     * @return room
     */
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
       this.room = room;
    }

    public CourseClass room(Room room) {
      this.room = room;
      return this;
    }

    /**
     * Timezone for the class
     * @return timezone
     */
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
       this.timezone = timezone;
    }

    public CourseClass timezone(String timezone) {
      this.timezone = timezone;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CourseClass {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    course: ").append(toIndentedString(course)).append("\n");
      sb.append("    code: ").append(toIndentedString(code)).append("\n");
      sb.append("    start: ").append(toIndentedString(start)).append("\n");
      sb.append("    end: ").append(toIndentedString(end)).append("\n");
      sb.append("    hasAvailablePlaces: ").append(toIndentedString(hasAvailablePlaces)).append("\n");
      sb.append("    availableEnrolmentPlaces: ").append(toIndentedString(availableEnrolmentPlaces)).append("\n");
      sb.append("    isFinished: ").append(toIndentedString(isFinished)).append("\n");
      sb.append("    isCancelled: ").append(toIndentedString(isCancelled)).append("\n");
      sb.append("    isAllowByApplication: ").append(toIndentedString(isAllowByApplication)).append("\n");
      sb.append("    isPaymentGatewayEnabled: ").append(toIndentedString(isPaymentGatewayEnabled)).append("\n");
      sb.append("    distantLearning: ").append(toIndentedString(distantLearning)).append("\n");
      sb.append("    price: ").append(toIndentedString(price)).append("\n");
      sb.append("    room: ").append(toIndentedString(room)).append("\n");
      sb.append("    timezone: ").append(toIndentedString(timezone)).append("\n");
      sb.append("}");
      return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(java.lang.Object o) {
      if (o == null) {
        return "null";
      }
      return o.toString().replace("\n", "\n    ");
    }
}

