package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.field.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldHeading  {
  
    private String name = null;
    private List<Field> fields = new ArrayList<Field>();

    /**
     * Heading name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public FieldHeading name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Get fields
     * @return fields
     */
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
       this.fields = fields;
    }

    public FieldHeading fields(List<Field> fields) {
      this.fields = fields;
      return this;
    }

    public FieldHeading addFieldsItem(Field fieldsItem) {
      this.fields.add(fieldsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class FieldHeading {\n");
      
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
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

