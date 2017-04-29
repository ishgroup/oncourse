package ish.oncourse.willow.model.web;


public class Token  {
  
    private String token = null;

    /**
     * Unique identifier for the user
     * @return token
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
       this.token = token;
    }

    public Token token(String token) {
      this.token = token;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Token {\n");
      
      sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

