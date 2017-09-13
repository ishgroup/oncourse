package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.common.Item;
import ish.oncourse.willow.model.field.DataType;
import java.util.ArrayList;
import java.util.List;

public class Field  {
  
    private String id = null;
    private String key = null;
    private String name = null;
    private String description = null;
    private Boolean mandatory = null;
    private DataType dataType = null;
    private String enumType = null;
    private String value = null;
    private Item itemValue = null;
    private String defaultValue = null;
    private List<Item> enumItems = new ArrayList<Item>();
    private Integer ordering = null;

    /**
     * Field id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public Field id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Property key
     * @return key
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
       this.key = key;
    }

    public Field key(String key) {
      this.key = key;
      return this;
    }

    /**
     * Field name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Field name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Field description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
       this.description = description;
    }

    public Field description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Mandatory flag
     * @return mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
       this.mandatory = mandatory;
    }

    public Field mandatory(Boolean mandatory) {
      this.mandatory = mandatory;
      return this;
    }

    /**
     * Data type of provaded value
     * @return dataType
     */
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
       this.dataType = dataType;
    }

    public Field dataType(DataType dataType) {
      this.dataType = dataType;
      return this;
    }

    /**
     * Enumeration name which is type for field value
     * @return enumType
     */
    public String getEnumType() {
        return enumType;
    }

    public void setEnumType(String enumType) {
       this.enumType = enumType;
    }

    public Field enumType(String enumType) {
      this.enumType = enumType;
      return this;
    }

    /**
     * Value for corresponded property
     * @return value
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
       this.value = value;
    }

    public Field value(String value) {
      this.value = value;
      return this;
    }

    /**
     * Item value for corresponded property
     * @return itemValue
     */
    public Item getItemValue() {
        return itemValue;
    }

    public void setItemValue(Item itemValue) {
       this.itemValue = itemValue;
    }

    public Field itemValue(Item itemValue) {
      this.itemValue = itemValue;
      return this;
    }

    /**
     * Default value for corresponded property
     * @return defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
       this.defaultValue = defaultValue;
    }

    public Field defaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    /**
     * Combobox choices for enumeration types
     * @return enumItems
     */
    public List<Item> getEnumItems() {
        return enumItems;
    }

    public void setEnumItems(List<Item> enumItems) {
       this.enumItems = enumItems;
    }

    public Field enumItems(List<Item> enumItems) {
      this.enumItems = enumItems;
      return this;
    }

    public Field addEnumItemsItem(Item enumItemsItem) {
      this.enumItems.add(enumItemsItem);
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

    public Field ordering(Integer ordering) {
      this.ordering = ordering;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Field {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    key: ").append(toIndentedString(key)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    description: ").append(toIndentedString(description)).append("\n");
      sb.append("    mandatory: ").append(toIndentedString(mandatory)).append("\n");
      sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
      sb.append("    enumType: ").append(toIndentedString(enumType)).append("\n");
      sb.append("    value: ").append(toIndentedString(value)).append("\n");
      sb.append("    itemValue: ").append(toIndentedString(itemValue)).append("\n");
      sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
      sb.append("    enumItems: ").append(toIndentedString(enumItems)).append("\n");
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

