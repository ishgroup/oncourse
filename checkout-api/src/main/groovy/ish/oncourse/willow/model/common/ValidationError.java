package ish.oncourse.willow.model.common;

import ish.oncourse.willow.model.common.FieldError;
import java.util.ArrayList;
import java.util.List;

public class ValidationError  {
  
    private List<String> formErrors = new ArrayList<String>();
    private List<FieldError> fieldsErrors = new ArrayList<FieldError>();

    /**
     * Global error
     * @return formErrors
     */
    public List<String> getFormErrors() {
        return formErrors;
    }

    public void setFormErrors(List<String> formErrors) {
       this.formErrors = formErrors;
    }

    public ValidationError formErrors(List<String> formErrors) {
      this.formErrors = formErrors;
      return this;
    }

    public ValidationError addFormErrorsItem(String formErrorsItem) {
      this.formErrors.add(formErrorsItem);
      return this;
    }

    /**
     * Array of fields errors, or empty array
     * @return fieldsErrors
     */
    public List<FieldError> getFieldsErrors() {
        return fieldsErrors;
    }

    public void setFieldsErrors(List<FieldError> fieldsErrors) {
       this.fieldsErrors = fieldsErrors;
    }

    public ValidationError fieldsErrors(List<FieldError> fieldsErrors) {
      this.fieldsErrors = fieldsErrors;
      return this;
    }

    public ValidationError addFieldsErrorsItem(FieldError fieldsErrorsItem) {
      this.fieldsErrors.add(fieldsErrorsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ValidationError {\n");
      
      sb.append("    formErrors: ").append(toIndentedString(formErrors)).append("\n");
      sb.append("    fieldsErrors: ").append(toIndentedString(fieldsErrors)).append("\n");
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

