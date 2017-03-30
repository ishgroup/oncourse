package ish.oncourse.willow.model;

import java.util.ArrayList;
import java.util.List;

public class FieldErrors  {
  
    private String name = null;
    private List<String> errors = new ArrayList<String>();

    /**
     * Name of fiels with error
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public FieldErrors name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Errors for particular field, or empty array
     * @return errors
     */
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
       this.errors = errors;
    }

    public FieldErrors errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public FieldErrors addErrorsItem(String errorsItem) {
      this.errors.add(errorsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class FieldErrors {\n");
      
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

