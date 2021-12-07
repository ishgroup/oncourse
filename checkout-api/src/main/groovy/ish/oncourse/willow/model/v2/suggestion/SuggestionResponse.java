package ish.oncourse.willow.model.v2.suggestion;

import java.util.ArrayList;
import java.util.List;

public class SuggestionResponse  {
  
    private List<String> courseClasses = new ArrayList<String>();
    private List<String> products = new ArrayList<String>();

    /**
     * Get courseClasses
     * @return courseClasses
     */
    public List<String> getCourseClasses() {
        return courseClasses;
    }

    public void setCourseClasses(List<String> courseClasses) {
       this.courseClasses = courseClasses;
    }

    public SuggestionResponse courseClasses(List<String> courseClasses) {
      this.courseClasses = courseClasses;
      return this;
    }

    public SuggestionResponse addCourseClassesItem(String courseClassesItem) {
      this.courseClasses.add(courseClassesItem);
      return this;
    }

    /**
     * Get products
     * @return products
     */
    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
       this.products = products;
    }

    public SuggestionResponse products(List<String> products) {
      this.products = products;
      return this;
    }

    public SuggestionResponse addProductsItem(String productsItem) {
      this.products.add(productsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class SuggestionResponse {\n");
      
      sb.append("    courseClasses: ").append(toIndentedString(courseClasses)).append("\n");
      sb.append("    products: ").append(toIndentedString(products)).append("\n");
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

