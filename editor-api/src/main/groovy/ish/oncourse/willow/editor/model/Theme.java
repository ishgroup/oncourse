package ish.oncourse.willow.editor.model;

import ish.oncourse.willow.editor.model.ThemeSchema;

public class Theme  {
  
    private Double id = null;
    private String title = null;
    private Double layoutId = null;
    private ThemeSchema schema = null;

    /**
     * Unique identifier of theme
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public Theme id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * title of theme
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public Theme title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Unique identifier of for theme
     * @return layoutId
     */
    public Double getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Double layoutId) {
       this.layoutId = layoutId;
    }

    public Theme layoutId(Double layoutId) {
      this.layoutId = layoutId;
      return this;
    }

    /**
     * Get schema
     * @return schema
     */
    public ThemeSchema getSchema() {
        return schema;
    }

    public void setSchema(ThemeSchema schema) {
       this.schema = schema;
    }

    public Theme schema(ThemeSchema schema) {
      this.schema = schema;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Theme {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    layoutId: ").append(toIndentedString(layoutId)).append("\n");
      sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
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

