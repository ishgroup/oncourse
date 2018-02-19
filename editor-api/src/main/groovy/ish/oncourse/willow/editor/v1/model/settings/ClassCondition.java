package ish.oncourse.willow.editor.v1.model.settings;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets settings.ClassCondition
 */
public enum ClassCondition {
  
  AFTERCLASSSTARTS("afterClassStarts"),
  
  BEFORECLASSSTARTS("beforeClassStarts"),
  
  AFTERCLASSENDS("afterClassEnds"),
  
  BEFORECLASSENDS("beforeClassEnds");

  private String value;

  ClassCondition(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ClassCondition fromValue(String text) {
    for (ClassCondition b : ClassCondition.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

