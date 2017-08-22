package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.field.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldHeading  {
  
    private String name = null;
    private String description = null;
    private List<Field> fields = new ArrayList<Field>();
    private Integer ordering = null;

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
     * Heading description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
       this.description = description;
    }

    public FieldHeading description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Fields set related to field heading
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

    /**
     * order position
     * @return ordering
     */
    public Integer getOrdering() {
        return ordering;
    }

    public void setOrdering(Integer ordering) {
       this.ordering = ordering;
    }

    public FieldHeading ordering(Integer ordering) {
      this.ordering = ordering;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class FieldHeading {\n");
      
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    description: ").append(toIndentedString(description)).append("\n");
      sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
      sb.append("    ordering: ").append(toIndentedString(ordering)).append("\n");
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

