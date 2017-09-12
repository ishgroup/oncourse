package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.field.FieldHeading;
import ish.oncourse.willow.model.web.CourseClassPrice;
import java.util.ArrayList;
import java.util.List;

public class Enrolment  {
  
    private String contactId = null;
    private String classId = null;
    private CourseClassPrice price = null;
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private Boolean selected = null;
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

    public Enrolment contactId(String contactId) {
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

    public Enrolment classId(String classId) {
      this.classId = classId;
      return this;
    }

    /**
     * Get price
     * @return price
     */
    public CourseClassPrice getPrice() {
        return price;
    }

    public void setPrice(CourseClassPrice price) {
       this.price = price;
    }

    public Enrolment price(CourseClassPrice price) {
      this.price = price;
      return this;
    }

    /**
     * Get warnings
     * @return warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
       this.warnings = warnings;
    }

    public Enrolment warnings(List<String> warnings) {
      this.warnings = warnings;
      return this;
    }

    public Enrolment addWarningsItem(String warningsItem) {
      this.warnings.add(warningsItem);
      return this;
    }

    /**
     * Get errors
     * @return errors
     */
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
       this.errors = errors;
    }

    public Enrolment errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public Enrolment addErrorsItem(String errorsItem) {
      this.errors.add(errorsItem);
      return this;
    }

    /**
     * Get selected
     * @return selected
     */
    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
       this.selected = selected;
    }

    public Enrolment selected(Boolean selected) {
      this.selected = selected;
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

    public Enrolment fieldHeadings(List<FieldHeading> fieldHeadings) {
      this.fieldHeadings = fieldHeadings;
      return this;
    }

    public Enrolment addFieldHeadingsItem(FieldHeading fieldHeadingsItem) {
      this.fieldHeadings.add(fieldHeadingsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Enrolment {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classId: ").append(toIndentedString(classId)).append("\n");
      sb.append("    price: ").append(toIndentedString(price)).append("\n");
      sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
      sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
      sb.append("    selected: ").append(toIndentedString(selected)).append("\n");
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

