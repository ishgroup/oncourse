package ish.oncourse.willow.model.web.product;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets web.product.Type
 */
public enum Type {
  
  ARTICLE("ARTICLE"),
  
  MEMBERSHIP("MEMBERSHIP"),
  
  VOUCHER("VOUCHER");

  private String value;

  Type(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Type fromValue(String text) {
    for (Type b : Type.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

