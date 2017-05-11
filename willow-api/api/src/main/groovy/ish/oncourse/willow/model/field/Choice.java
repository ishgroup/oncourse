package ish.oncourse.willow.model.field;


public class Choice  {
  
    private String displayName = null;

    /**
     * Display name fo choice
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
       this.displayName = displayName;
    }

    public Choice displayName(String displayName) {
      this.displayName = displayName;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Choice {\n");
      
      sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
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

