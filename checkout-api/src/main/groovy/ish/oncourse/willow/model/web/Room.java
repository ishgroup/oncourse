package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.web.Site;

public class Room  {
  
    private String name = null;
    private Site site = null;

    /**
     * Room name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Room name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Site relation
     * @return site
     */
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
       this.site = site;
    }

    public Room site(Site site) {
      this.site = site;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Room {\n");
      
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    site: ").append(toIndentedString(site)).append("\n");
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

