package ish.oncourse.willow.editor.model.menuitem;


public class Errors  {
  
    private String title = null;
    private Boolean url = null;

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public Errors title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Get url
     * @return url
     */
    public Boolean getUrl() {
        return url;
    }

    public void setUrl(Boolean url) {
       this.url = url;
    }

    public Errors url(Boolean url) {
      this.url = url;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Errors {\n");
      
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    url: ").append(toIndentedString(url)).append("\n");
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

