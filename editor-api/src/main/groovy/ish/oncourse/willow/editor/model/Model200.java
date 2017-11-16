package ish.oncourse.willow.editor.model;


public class Model200  {
  
    private String html = null;

    /**
     * Get html
     * @return html
     */
    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
       this.html = html;
    }

    public Model200 html(String html) {
      this.html = html;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Model200 {\n");
      
      sb.append("    html: ").append(toIndentedString(html)).append("\n");
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

