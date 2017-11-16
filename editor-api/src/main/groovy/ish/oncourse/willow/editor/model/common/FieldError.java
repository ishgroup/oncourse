package ish.oncourse.willow.editor.model.common;


public class FieldError  {
  
    private String name = null;
    private String error = null;

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

    public FieldError name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Errors for particular field, or empty array
     * @return error
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
       this.error = error;
    }

    public FieldError error(String error) {
      this.error = error;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class FieldError {\n");
      
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

