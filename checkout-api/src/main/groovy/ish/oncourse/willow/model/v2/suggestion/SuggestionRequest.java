package ish.oncourse.willow.model.v2.suggestion;

import java.util.ArrayList;
import java.util.List;

public class SuggestionRequest  {
  
    private List<String> courseIds = new ArrayList<String>();
    private List<String> productIds = new ArrayList<String>();

    /**
     * Get courseIds
     * @return courseIds
     */
    public List<String> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<String> courseIds) {
       this.courseIds = courseIds;
    }

    public SuggestionRequest courseIds(List<String> courseIds) {
      this.courseIds = courseIds;
      return this;
    }

    public SuggestionRequest addCourseIdsItem(String courseIdsItem) {
      this.courseIds.add(courseIdsItem);
      return this;
    }

    /**
     * Get productIds
     * @return productIds
     */
    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
       this.productIds = productIds;
    }

    public SuggestionRequest productIds(List<String> productIds) {
      this.productIds = productIds;
      return this;
    }

    public SuggestionRequest addProductIdsItem(String productIdsItem) {
      this.productIds.add(productIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class SuggestionRequest {\n");
      
      sb.append("    courseIds: ").append(toIndentedString(courseIds)).append("\n");
      sb.append("    productIds: ").append(toIndentedString(productIds)).append("\n");
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

