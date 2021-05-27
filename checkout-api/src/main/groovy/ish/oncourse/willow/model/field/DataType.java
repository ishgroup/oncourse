package ish.oncourse.willow.model.field;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets field.DataType
 */
public enum DataType {
  
  STRING("STRING"),
  
  LONG_STRING("LONG_STRING"),
  
  PATTERN_TEXT("PATTERN_TEXT"),
  
  BOOLEAN("BOOLEAN"),
  
  DATE("DATE"),
  
  DATETIME("DATETIME"),
  
  INTEGER("INTEGER"),
  
  COUNTRY("COUNTRY"),
  
  LANGUAGE("LANGUAGE"),
  
  ENUM("ENUM"),
  
  EMAIL("EMAIL"),
  
  SUBURB("SUBURB"),
  
  POSTCODE("POSTCODE"),
  
  PHONE("PHONE"),
  
  CHOICE("CHOICE"),
  
  TAGGROUP_S("TAGGROUP_S"),
  
  TAGGROUP_M("TAGGROUP_M"),
  
  MAILINGLIST("MAILINGLIST"),
  
  MONEY("MONEY"),
  
  URL("URL");

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

