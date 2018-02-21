package ish.oncourse.willow.editor.v1.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets VersionStatus
 */
public enum VersionStatus {
  
  PUBLISHED("published"),
  
  DRAFT("draft");

  private String value;

  VersionStatus(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static VersionStatus fromValue(String text) {
    for (VersionStatus b : VersionStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

