package ish.oncourse.willow.editor.model;


public class Block  {
  
    private Double id = null;
    private String title = null;
    private String html = null;

    /**
     * unique id of block
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public Block id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * Title/Name of block
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public Block title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Html source of block
     * @return html
     */
    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
       this.html = html;
    }

    public Block html(String html) {
      this.html = html;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Block {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

