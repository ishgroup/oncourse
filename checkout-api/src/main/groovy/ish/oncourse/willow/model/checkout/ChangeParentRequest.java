package ish.oncourse.willow.model.checkout;


public class ChangeParentRequest  {
  
    private String parentId = null;
    private String childId = null;

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

    public ChangeParentRequest parentId(String parentId) {
      this.parentId = parentId;
      return this;
    }

    /**
     * Get childId
     * @return childId
     */
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
       this.childId = childId;
    }

    public ChangeParentRequest childId(String childId) {
      this.childId = childId;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ChangeParentRequest {\n");
      
      sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
      sb.append("    childId: ").append(toIndentedString(childId)).append("\n");
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

