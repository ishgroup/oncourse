package ish.oncourse.willow.model.checkout.payment;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets checkout.payment.PaymentStatus
 */
public enum PaymentStatus {
  
  IN_PROGRESS("in progress"),
  
  FAILED("failed"),
  
  SUCCESSFUL("successful"),
  
  ERROR("error");

  private String value;

  PaymentStatus(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PaymentStatus fromValue(String text) {
    for (PaymentStatus b : PaymentStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

