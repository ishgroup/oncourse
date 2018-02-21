package ish.oncourse.willow.editor.v1.model;

import ish.oncourse.willow.editor.v1.model.PageUrl;
import java.util.ArrayList;
import java.util.List;

public class Page  {
  
    private Integer id = null;
    private Integer serialNumber = null;
    private String title = null;
    private Integer themeId = null;
    private String content = null;
    private Boolean visible = null;
    private Boolean suppressOnSitemap = null;
    private List<PageUrl> urls = new ArrayList<>();

    /**
     * unique id of the page
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public Page id(Integer id) {
      this.id = id;
      return this;
    }

    /**
     * serial number of the page. Can be used to proceed to page by technical path /page/10
     * @return serialNumber
     */
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
       this.serialNumber = serialNumber;
    }

    public Page serialNumber(Integer serialNumber) {
      this.serialNumber = serialNumber;
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
    public Boolean Visible() {
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
     * Is the page suppressed on sitemap
     * @return suppressOnSitemap
     */
    public Boolean SuppressOnSitemap() {
        return suppressOnSitemap;
    }

    public void setSuppressOnSitemap(Boolean suppressOnSitemap) {
       this.suppressOnSitemap = suppressOnSitemap;
    }

    public Page suppressOnSitemap(Boolean suppressOnSitemap) {
      this.suppressOnSitemap = suppressOnSitemap;
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
      sb.append("    serialNumber: ").append(toIndentedString(serialNumber)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    themeId: ").append(toIndentedString(themeId)).append("\n");
      sb.append("    content: ").append(toIndentedString(content)).append("\n");
      sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
      sb.append("    suppressOnSitemap: ").append(toIndentedString(suppressOnSitemap)).append("\n");
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

