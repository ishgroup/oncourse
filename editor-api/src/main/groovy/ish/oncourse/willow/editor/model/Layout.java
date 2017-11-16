package ish.oncourse.willow.editor.model;


public class Layout  {
  
    private Double id = null;
    private String layoutKey = null;

    /**
     * unique id of the layout
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public Layout id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * key/name of the layout
     * @return layoutKey
     */
    public String getLayoutKey() {
        return layoutKey;
    }

    public void setLayoutKey(String layoutKey) {
       this.layoutKey = layoutKey;
    }

    public Layout layoutKey(String layoutKey) {
      this.layoutKey = layoutKey;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Layout {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    layoutKey: ").append(toIndentedString(layoutKey)).append("\n");
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

