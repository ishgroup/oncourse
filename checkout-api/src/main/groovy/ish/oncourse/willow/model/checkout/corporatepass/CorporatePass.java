package ish.oncourse.willow.model.checkout.corporatepass;


public class CorporatePass  {
  
    private String id = null;
    private String code = null;
    private String message = null;
    private String email = null;
    private String name = null;

    /**
     * Get id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public CorporatePass id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Get code
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
       this.code = code;
    }

    public CorporatePass code(String code) {
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

    public CorporatePass message(String message) {
      this.message = message;
      return this;
    }

    /**
     * Get email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
       this.email = email;
    }

    public CorporatePass email(String email) {
      this.email = email;
      return this;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public CorporatePass name(String name) {
      this.name = name;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CorporatePass {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    code: ").append(toIndentedString(code)).append("\n");
      sb.append("    message: ").append(toIndentedString(message)).append("\n");
      sb.append("    email: ").append(toIndentedString(email)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

