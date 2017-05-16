package ish.oncourse.willow.model.field;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets field.FieldSet
 */
public enum FieldSet {
  
  ENROLMENT("enrolment"),
  
  WAITINGLIST("waitinglist"),
  
  MAILINGLIST("mailinglist");

  private String value;

  FieldSet(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static FieldSet fromValue(String text) {
    for (FieldSet b : FieldSet.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

