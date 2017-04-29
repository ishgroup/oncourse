package ish.oncourse.willow.model.web;


public class ContactParams  {
  
    private String id = null;

    /**
     * Internal Unique identifier of course
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public ContactParams id(String id) {
      this.id = id;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactParams {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

