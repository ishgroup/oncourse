package ish.oncourse.willow.model.common;


public class Error  {
  
    private Integer code = null;
    private String message = null;
    private String fields = null;

    /**
     * Get code
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
       this.code = code;
    }

    public Error code(Integer code) {
      this.code = code;
      return this;
    }

    /**
     * Get message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
       this.message = message;
    }

    public Error message(String message) {
      this.message = message;
      return this;
    }

    /**
     * Get fields
     * @return fields
     */
    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
       this.fields = fields;
    }

    public Error fields(String fields) {
      this.fields = fields;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Error {\n");
      
      sb.append("    code: ").append(toIndentedString(code)).append("\n");
      sb.append("    message: ").append(toIndentedString(message)).append("\n");
      sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
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

