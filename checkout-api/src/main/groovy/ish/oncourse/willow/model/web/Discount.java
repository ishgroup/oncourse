package ish.oncourse.willow.model.web;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ish.oncourse.util.FormatUtils;

public class Discount  {
  
    private String id = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatUtils.DATE_FORMAT_ISO8601)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expiryDate = null;
    private Double discountedFee = null;
    private Double discountValue = null;
    private String title = null;

    /**
     * Internal Unique identifier of discount
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public Discount id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Get expiryDate
     * @return expiryDate
     */
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
       this.expiryDate = expiryDate;
    }

    public Discount expiryDate(LocalDateTime expiryDate) {
      this.expiryDate = expiryDate;
      return this;
    }

    /**
     * Get discountedFee
     * @return discountedFee
     */
    public Double getDiscountedFee() {
        return discountedFee;
    }

    public void setDiscountedFee(Double discountedFee) {
       this.discountedFee = discountedFee;
    }

    public Discount discountedFee(Double discountedFee) {
      this.discountedFee = discountedFee;
      return this;
    }

    /**
     * Get discountValue
     * @return discountValue
     */
    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
       this.discountValue = discountValue;
    }

    public Discount discountValue(Double discountValue) {
      this.discountValue = discountValue;
      return this;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public Discount title(String title) {
      this.title = title;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Discount {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
      sb.append("    discountedFee: ").append(toIndentedString(discountedFee)).append("\n");
      sb.append("    discountValue: ").append(toIndentedString(discountValue)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

