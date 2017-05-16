package ish.oncourse.willow.model.common;


public class Item  {
  
    private String key = null;
    private String value = null;

    /**
     * Key which will be send back
     * @return key
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
       this.key = key;
    }

    public Item key(String key) {
      this.key = key;
      return this;
    }

    /**
     * Value which user will see
     * @return value
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
       this.value = value;
    }

    public Item value(String value) {
      this.value = value;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Item {\n");
      
      sb.append("    key: ").append(toIndentedString(key)).append("\n");
      sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

