package ish.oncourse.willow.model.field;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets field.DataType
 */
public enum DataType {
  
  STRING("string"),
  
  BOOLEAN("boolean"),
  
  DATE("date"),
  
  DATETIME("dateTime"),
  
  INTEGER("integer"),
  
  COUNTRY("country"),
  
  LANGUAGE("language"),
  
  ENUM("enum"),
  
  EMAIL("email"),
  
  SUBURB("suburb"),
  
  POSTCODE("postcode"),
  
  PHONE("phone");

  private String value;

  DataType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static DataType fromValue(String text) {
    for (DataType b : DataType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

