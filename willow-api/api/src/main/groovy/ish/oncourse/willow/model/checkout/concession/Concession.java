package ish.oncourse.willow.model.checkout.concession;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ish.oncourse.util.FormatUtils;

public class Concession  {
  
    private String concessionTypeId = null;
    private String contactId = null;
    private String name = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatUtils.DATE_FORMAT_ISO8601)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expiryDate = null;
    private String number = null;

    /**
     * Get concessionTypeId
     * @return concessionTypeId
     */
    public String getConcessionTypeId() {
        return concessionTypeId;
    }

    public void setConcessionTypeId(String concessionTypeId) {
       this.concessionTypeId = concessionTypeId;
    }

    public Concession concessionTypeId(String concessionTypeId) {
      this.concessionTypeId = concessionTypeId;
      return this;
    }

    /**
     * Get contactId
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public Concession contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Concession name(String name) {
      this.name = name;
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

    public Concession expiryDate(LocalDateTime expiryDate) {
      this.expiryDate = expiryDate;
      return this;
    }

    /**
     * Get number
     * @return number
     */
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
       this.number = number;
    }

    public Concession number(String number) {
      this.number = number;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Concession {\n");
      
      sb.append("    concessionTypeId: ").append(toIndentedString(concessionTypeId)).append("\n");
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
      sb.append("    number: ").append(toIndentedString(number)).append("\n");
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

