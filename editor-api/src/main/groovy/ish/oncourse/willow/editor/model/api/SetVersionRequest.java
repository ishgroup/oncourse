package ish.oncourse.willow.editor.model.api;


public class SetVersionRequest  {
  
    private Integer id = null;

    /**
     * Get id
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public SetVersionRequest id(Integer id) {
      this.id = id;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class SetVersionRequest {\n");
      
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

