package ish.oncourse.willow.editor.v1.model;

import ish.oncourse.willow.editor.v1.model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuItem  {
  
    private Integer id = null;
    private String title = null;
    private String url = null;
    private List<MenuItem> children = new ArrayList<MenuItem>();
    private String error = null;
    private String warning = null;

    /**
     * Unique identifier of menu item
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public MenuItem id(Integer id) {
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
     * Get children
     * @return children
     */
    public List<MenuItem> getChildren() {
        return children;
    }

    public void setChildren(List<MenuItem> children) {
       this.children = children;
    }

    public MenuItem children(List<MenuItem> children) {
      this.children = children;
      return this;
    }

    public MenuItem addChildrenItem(MenuItem childrenItem) {
      this.children.add(childrenItem);
      return this;
    }

    /**
     * Describes reason why menu item cannot be saved. Comes on 400 (Bad request) response
     * @return error
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
       this.error = error;
    }

    public MenuItem error(String error) {
      this.error = error;
      return this;
    }

    /**
     * Any alerts for user. Do not affect on menus saving. Comes on 200 (Success) response
     * @return warning
     */
    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
       this.warning = warning;
    }

    public MenuItem warning(String warning) {
      this.warning = warning;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class MenuItem {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    url: ").append(toIndentedString(url)).append("\n");
      sb.append("    children: ").append(toIndentedString(children)).append("\n");
      sb.append("    error: ").append(toIndentedString(error)).append("\n");
      sb.append("    warning: ").append(toIndentedString(warning)).append("\n");
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

