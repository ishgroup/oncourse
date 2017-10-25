package ish.oncourse.willow.model.web;

import java.util.ArrayList;
import java.util.List;

public class CoursesParams  {
  
    private List<String> coursesIds = new ArrayList<String>();

    /**
     * List of requested courses
     * @return coursesIds
     */
    public List<String> getCoursesIds() {
        return coursesIds;
    }

    public void setCoursesIds(List<String> coursesIds) {
       this.coursesIds = coursesIds;
    }

    public CoursesParams coursesIds(List<String> coursesIds) {
      this.coursesIds = coursesIds;
      return this;
    }

    public CoursesParams addCoursesIdsItem(String coursesIdsItem) {
      this.coursesIds.add(coursesIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CoursesParams {\n");
      
      sb.append("    coursesIds: ").append(toIndentedString(coursesIds)).append("\n");
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

