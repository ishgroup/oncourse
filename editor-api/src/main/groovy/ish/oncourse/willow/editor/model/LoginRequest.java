package ish.oncourse.willow.editor.model;


public class LoginRequest  {
  
    private String email = null;
    private String password = null;

    /**
     * user email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
       this.email = email;
    }

    public LoginRequest email(String email) {
      this.email = email;
      return this;
    }

    /**
     * User password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
       this.password = password;
    }

    public LoginRequest password(String password) {
      this.password = password;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class LoginRequest {\n");
      
      sb.append("    email: ").append(toIndentedString(email)).append("\n");
      sb.append("    password: ").append(toIndentedString(password)).append("\n");
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

