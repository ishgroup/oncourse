package ish.oncourse.willow.model.checkout;

import java.util.ArrayList;
import java.util.List;

public class Application  {
  
    private String contactId = null;
    private String classId = null;
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();

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

    public Application contactId(String contactId) {
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

    public Application classId(String classId) {
      this.classId = classId;
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

    public Application warnings(List<String> warnings) {
      this.warnings = warnings;
      return this;
    }

    public Application addWarningsItem(String warningsItem) {
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

    public Application errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public Application addErrorsItem(String errorsItem) {
      this.errors.add(errorsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Application {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classId: ").append(toIndentedString(classId)).append("\n");
      sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
      sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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

