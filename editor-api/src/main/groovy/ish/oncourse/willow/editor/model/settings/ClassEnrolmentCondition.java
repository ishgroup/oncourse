package ish.oncourse.willow.editor.model.settings;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets settings.ClassEnrolmentCondition
 */
public enum ClassEnrolmentCondition {
  
  AFTERCLASSSTARTS("afterClassStarts"),
  
  BEFORECLASSSTARTS("beforeClassStarts"),
  
  BEFORECLASSENDS("beforeClassEnds");

  private String value;

  ClassEnrolmentCondition(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ClassEnrolmentCondition fromValue(String text) {
    for (ClassEnrolmentCondition b : ClassEnrolmentCondition.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

