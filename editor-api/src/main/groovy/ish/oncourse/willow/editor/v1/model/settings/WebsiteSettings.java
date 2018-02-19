package ish.oncourse.willow.editor.v1.model.settings;

import ish.oncourse.willow.editor.v1.model.settings.ClassAge;

public class WebsiteSettings  {
  
    private Boolean enableSocialMedia = null;
    private String addThisId = null;
    private Boolean enableForCourse = null;
    private Boolean enableForWebpage = null;
    private ClassAge classAge = null;

    /**
     * Get enableSocialMedia
     * @return enableSocialMedia
     */
    public Boolean getEnableSocialMedia() {
        return enableSocialMedia;
    }

    public void setEnableSocialMedia(Boolean enableSocialMedia) {
       this.enableSocialMedia = enableSocialMedia;
    }

    public WebsiteSettings enableSocialMedia(Boolean enableSocialMedia) {
      this.enableSocialMedia = enableSocialMedia;
      return this;
    }

    /**
     * Get addThisId
     * @return addThisId
     */
    public String getAddThisId() {
        return addThisId;
    }

    public void setAddThisId(String addThisId) {
       this.addThisId = addThisId;
    }

    public WebsiteSettings addThisId(String addThisId) {
      this.addThisId = addThisId;
      return this;
    }

    /**
     * Get enableForCourse
     * @return enableForCourse
     */
    public Boolean getEnableForCourse() {
        return enableForCourse;
    }

    public void setEnableForCourse(Boolean enableForCourse) {
       this.enableForCourse = enableForCourse;
    }

    public WebsiteSettings enableForCourse(Boolean enableForCourse) {
      this.enableForCourse = enableForCourse;
      return this;
    }

    /**
     * Get enableForWebpage
     * @return enableForWebpage
     */
    public Boolean getEnableForWebpage() {
        return enableForWebpage;
    }

    public void setEnableForWebpage(Boolean enableForWebpage) {
       this.enableForWebpage = enableForWebpage;
    }

    public WebsiteSettings enableForWebpage(Boolean enableForWebpage) {
      this.enableForWebpage = enableForWebpage;
      return this;
    }

    /**
     * Get classAge
     * @return classAge
     */
    public ClassAge getClassAge() {
        return classAge;
    }

    public void setClassAge(ClassAge classAge) {
       this.classAge = classAge;
    }

    public WebsiteSettings classAge(ClassAge classAge) {
      this.classAge = classAge;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class WebsiteSettings {\n");
      
      sb.append("    enableSocialMedia: ").append(toIndentedString(enableSocialMedia)).append("\n");
      sb.append("    addThisId: ").append(toIndentedString(addThisId)).append("\n");
      sb.append("    enableForCourse: ").append(toIndentedString(enableForCourse)).append("\n");
      sb.append("    enableForWebpage: ").append(toIndentedString(enableForWebpage)).append("\n");
      sb.append("    classAge: ").append(toIndentedString(classAge)).append("\n");
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

