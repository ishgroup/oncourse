package ish.oncourse.willow.editor.model;

import ish.oncourse.willow.editor.model.PageUrl;
import java.util.ArrayList;
import java.util.List;

public class Page  {
  
    private Double id = null;
    private String title = null;
    private Double themeId = null;
    private String html = null;
    private Boolean visible = null;
    private List<PageUrl> urls = new ArrayList<PageUrl>();

    /**
     * unique id of the page
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public Page id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * title of the page
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public Page title(String title) {
      this.title = title;
      return this;
    }

    /**
     * theme Id for the page
     * @return themeId
     */
    public Double getThemeId() {
        return themeId;
    }

    public void setThemeId(Double themeId) {
       this.themeId = themeId;
    }

    public Page themeId(Double themeId) {
      this.themeId = themeId;
      return this;
    }

    /**
     * Html source of the page
     * @return html
     */
    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
       this.html = html;
    }

    public Page html(String html) {
      this.html = html;
      return this;
    }

    /**
     * Has page visible
     * @return visible
     */
    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
       this.visible = visible;
    }

    public Page visible(Boolean visible) {
      this.visible = visible;
      return this;
    }

    /**
     * Get urls
     * @return urls
     */
    public List<PageUrl> getUrls() {
        return urls;
    }

    public void setUrls(List<PageUrl> urls) {
       this.urls = urls;
    }

    public Page urls(List<PageUrl> urls) {
      this.urls = urls;
      return this;
    }

    public Page addUrlsItem(PageUrl urlsItem) {
      this.urls.add(urlsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Page {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    themeId: ").append(toIndentedString(themeId)).append("\n");
      sb.append("    html: ").append(toIndentedString(html)).append("\n");
      sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
      sb.append("    urls: ").append(toIndentedString(urls)).append("\n");
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

