package ish.oncourse.willow.model.checkout.waitinglist;

import ish.oncourse.willow.model.field.FieldHeading;
import java.util.ArrayList;
import java.util.List;

public class WaitingListRequest  {
  
    private String contactId = null;
    private String classId = null;
    private String comments = null;
    private Double numberOfStudents = null;
    private List<FieldHeading> fieldHeadings = new ArrayList<FieldHeading>();

    /**
     * Get contactId
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public WaitingListRequest contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Get classId
     * @return classId
     */
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
       this.classId = classId;
    }

    public WaitingListRequest classId(String classId) {
      this.classId = classId;
      return this;
    }

    /**
     * Get comments
     * @return comments
     */
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
       this.comments = comments;
    }

    public WaitingListRequest comments(String comments) {
      this.comments = comments;
      return this;
    }

    /**
     * Get numberOfStudents
     * @return numberOfStudents
     */
    public Double getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Double numberOfStudents) {
       this.numberOfStudents = numberOfStudents;
    }

    public WaitingListRequest numberOfStudents(Double numberOfStudents) {
      this.numberOfStudents = numberOfStudents;
      return this;
    }

    /**
     * Get fieldHeadings
     * @return fieldHeadings
     */
    public List<FieldHeading> getFieldHeadings() {
        return fieldHeadings;
    }

    public void setFieldHeadings(List<FieldHeading> fieldHeadings) {
       this.fieldHeadings = fieldHeadings;
    }

    public WaitingListRequest fieldHeadings(List<FieldHeading> fieldHeadings) {
      this.fieldHeadings = fieldHeadings;
      return this;
    }

    public WaitingListRequest addFieldHeadingsItem(FieldHeading fieldHeadingsItem) {
      this.fieldHeadings.add(fieldHeadingsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class WaitingListRequest {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classId: ").append(toIndentedString(classId)).append("\n");
      sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
      sb.append("    numberOfStudents: ").append(toIndentedString(numberOfStudents)).append("\n");
      sb.append("    fieldHeadings: ").append(toIndentedString(fieldHeadings)).append("\n");
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

