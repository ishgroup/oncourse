package ish.oncourse.willow.editor.v1.model;


public class Block  {
  
    private Integer id = null;
    private String title = null;
    private String content = null;

    /**
     * Unique identifier of block
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public Block id(Integer id) {
      this.id = id;
      return this;
    }

    /**
     * Unique name of block
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
     * Rich text content
     * @return content
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
       this.content = content;
    }

    public Block content(String content) {
      this.content = content;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Block {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

