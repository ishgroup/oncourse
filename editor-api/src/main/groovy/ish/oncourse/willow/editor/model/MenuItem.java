package ish.oncourse.willow.editor.model;

import ish.oncourse.willow.editor.model.menuitem.Errors;

public class MenuItem  {
  
    private Double id = null;
    private String title = null;
    private String url = null;
    private Boolean expanded = null;
    private Errors errors = null;

    /**
     * Unique identifier of menu item
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public MenuItem id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * title of menu item
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public MenuItem title(String title) {
      this.title = title;
      return this;
    }

    /**
     * url for menu link
     * @return url
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
       this.url = url;
    }

    public MenuItem url(String url) {
      this.url = url;
      return this;
    }

    /**
     * Get expanded
     * @return expanded
     */
    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
       this.expanded = expanded;
    }

    public MenuItem expanded(Boolean expanded) {
      this.expanded = expanded;
      return this;
    }

    /**
     * Get errors
     * @return errors
     */
    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
       this.errors = errors;
    }

    public MenuItem errors(Errors errors) {
      this.errors = errors;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class MenuItem {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    url: ").append(toIndentedString(url)).append("\n");
      sb.append("    expanded: ").append(toIndentedString(expanded)).append("\n");
      sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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

