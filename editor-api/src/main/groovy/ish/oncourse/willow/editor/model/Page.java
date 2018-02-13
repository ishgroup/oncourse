package ish.oncourse.willow.editor.model;

import ish.oncourse.willow.editor.model.PageUrl;
import java.util.ArrayList;
import java.util.List;

public class Page  {
  
    private Integer number = null;
    private String title = null;
    private Integer themeId = null;
    private String content = null;
    private Boolean visible = null;
    private Boolean reservedURL = null;
    private List<PageUrl> urls = new ArrayList<PageUrl>();

    /**
     * page number
     * @return number
     */
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
       this.number = number;
    }

    public Page number(Integer number) {
      this.number = number;
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
     * theme id used for the page
     * @return themeId
     */
    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
       this.themeId = themeId;
    }

    public Page themeId(Integer themeId) {
      this.themeId = themeId;
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

    public Page content(String content) {
      this.content = content;
      return this;
    }

    /**
     * Is the page visible
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
     * Indicates that page is a special system page and content is not editable
     * @return reservedURL
     */
    public Boolean getReservedURL() {
        return reservedURL;
    }

    public void setReservedURL(Boolean reservedURL) {
       this.reservedURL = reservedURL;
    }

    public Page reservedURL(Boolean reservedURL) {
      this.reservedURL = reservedURL;
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
      
      sb.append("    number: ").append(toIndentedString(number)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    themeId: ").append(toIndentedString(themeId)).append("\n");
      sb.append("    content: ").append(toIndentedString(content)).append("\n");
      sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
      sb.append("    reservedURL: ").append(toIndentedString(reservedURL)).append("\n");
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

