package ish.oncourse.willow.editor.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ish.oncourse.util.FormatUtils;

public class Version  {
  
    private Double id = null;
    private Boolean published = null;
    private String author = null;
    private Double changes = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatUtils.DATE_FORMAT_ISO8601)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date = null;

    /**
     * unique id of version
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public Version id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * has version published
     * @return published
     */
    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
       this.published = published;
    }

    public Version published(Boolean published) {
      this.published = published;
      return this;
    }

    /**
     * First and last name of the user who published version
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
       this.author = author;
    }

    public Version author(String author) {
      this.author = author;
      return this;
    }

    /**
     * count of changes from previous version
     * @return changes
     */
    public Double getChanges() {
        return changes;
    }

    public void setChanges(Double changes) {
       this.changes = changes;
    }

    public Version changes(Double changes) {
      this.changes = changes;
      return this;
    }

    /**
     * date when version was published
     * @return date
     */
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
       this.date = date;
    }

    public Version date(LocalDateTime date) {
      this.date = date;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Version {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    published: ").append(toIndentedString(published)).append("\n");
      sb.append("    author: ").append(toIndentedString(author)).append("\n");
      sb.append("    changes: ").append(toIndentedString(changes)).append("\n");
      sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

