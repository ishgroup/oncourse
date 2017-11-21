package ish.oncourse.willow.editor.model;


public class PageUrl  {
  
    private String link = null;
    private Boolean isDefault = null;

    /**
     * unique link
     * @return link
     */
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
       this.link = link;
    }

    public PageUrl link(String link) {
      this.link = link;
      return this;
    }

    /**
     * has url default
     * @return isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
       this.isDefault = isDefault;
    }

    public PageUrl isDefault(Boolean isDefault) {
      this.isDefault = isDefault;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PageUrl {\n");
      
      sb.append("    link: ").append(toIndentedString(link)).append("\n");
      sb.append("    isDefault: ").append(toIndentedString(isDefault)).append("\n");
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

