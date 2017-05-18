package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.PurchaseItem;
import java.util.List;

public class Application extends PurchaseItem {
  
    private String classId = null;

    /**
     * Get classId
     * @return classId
     */
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
       this.classId = classId;
    }

    public Application classId(String classId) {
      this.classId = classId;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Application {\n");
      sb.append("    ").append(toIndentedString(super.toString())).append("\n");
      sb.append("    classId: ").append(toIndentedString(classId)).append("\n");
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

