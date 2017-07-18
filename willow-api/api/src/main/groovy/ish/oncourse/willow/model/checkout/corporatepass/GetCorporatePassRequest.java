package ish.oncourse.willow.model.checkout.corporatepass;

import java.util.ArrayList;
import java.util.List;

public class GetCorporatePassRequest  {
  
    private String code = null;
    private List<String> classIds = new ArrayList<String>();
    private List<String> productIds = new ArrayList<String>();

    /**
     * Get code
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
       this.code = code;
    }

    public GetCorporatePassRequest code(String code) {
      this.code = code;
      return this;
    }

    /**
     * Get classIds
     * @return classIds
     */
    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
       this.classIds = classIds;
    }

    public GetCorporatePassRequest classIds(List<String> classIds) {
      this.classIds = classIds;
      return this;
    }

    public GetCorporatePassRequest addClassIdsItem(String classIdsItem) {
      this.classIds.add(classIdsItem);
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

    public GetCorporatePassRequest productIds(List<String> productIds) {
      this.productIds = productIds;
      return this;
    }

    public GetCorporatePassRequest addProductIdsItem(String productIdsItem) {
      this.productIds.add(productIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class GetCorporatePassRequest {\n");
      
      sb.append("    code: ").append(toIndentedString(code)).append("\n");
      sb.append("    classIds: ").append(toIndentedString(classIds)).append("\n");
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

