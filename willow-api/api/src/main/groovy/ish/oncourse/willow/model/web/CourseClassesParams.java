package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.web.ContactParams;
import ish.oncourse.willow.model.web.PromotionParams;
import java.util.ArrayList;
import java.util.List;

public class CourseClassesParams  {
  
    private List<String> courseClassesIds = new ArrayList<String>();
    private List<PromotionParams> promotions = new ArrayList<PromotionParams>();
    private ContactParams contact = null;

    /**
     * List of requested course classes
     * @return courseClassesIds
     */
    public List<String> getCourseClassesIds() {
        return courseClassesIds;
    }

    public void setCourseClassesIds(List<String> courseClassesIds) {
       this.courseClassesIds = courseClassesIds;
    }

    public CourseClassesParams courseClassesIds(List<String> courseClassesIds) {
      this.courseClassesIds = courseClassesIds;
      return this;
    }

    public CourseClassesParams addCourseClassesIdsItem(String courseClassesIdsItem) {
      this.courseClassesIds.add(courseClassesIdsItem);
      return this;
    }

    /**
     * List of applied promotions
     * @return promotions
     */
    public List<PromotionParams> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionParams> promotions) {
       this.promotions = promotions;
    }

    public CourseClassesParams promotions(List<PromotionParams> promotions) {
      this.promotions = promotions;
      return this;
    }

    public CourseClassesParams addPromotionsItem(PromotionParams promotionsItem) {
      this.promotions.add(promotionsItem);
      return this;
    }

    /**
     * Get contact
     * @return contact
     */
    public ContactParams getContact() {
        return contact;
    }

    public void setContact(ContactParams contact) {
       this.contact = contact;
    }

    public CourseClassesParams contact(ContactParams contact) {
      this.contact = contact;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CourseClassesParams {\n");
      
      sb.append("    courseClassesIds: ").append(toIndentedString(courseClassesIds)).append("\n");
      sb.append("    promotions: ").append(toIndentedString(promotions)).append("\n");
      sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
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

