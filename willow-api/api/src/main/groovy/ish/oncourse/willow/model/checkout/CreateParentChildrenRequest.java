package ish.oncourse.willow.model.checkout;

import java.util.ArrayList;
import java.util.List;

public class CreateParentChildrenRequest  {
  
    private String parentId = null;
    private List<String> childrenIds = new ArrayList<String>();

    /**
     * Get parentId
     * @return parentId
     */
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
       this.parentId = parentId;
    }

    public CreateParentChildrenRequest parentId(String parentId) {
      this.parentId = parentId;
      return this;
    }

    /**
     * Get childrenIds
     * @return childrenIds
     */
    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
       this.childrenIds = childrenIds;
    }

    public CreateParentChildrenRequest childrenIds(List<String> childrenIds) {
      this.childrenIds = childrenIds;
      return this;
    }

    public CreateParentChildrenRequest addChildrenIdsItem(String childrenIdsItem) {
      this.childrenIds.add(childrenIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CreateParentChildrenRequest {\n");
      
      sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
      sb.append("    childrenIds: ").append(toIndentedString(childrenIds)).append("\n");
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

