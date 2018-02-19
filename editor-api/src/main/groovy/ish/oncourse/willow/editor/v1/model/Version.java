package ish.oncourse.willow.editor.v1.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ish.oncourse.util.FormatUtils;

public class Version  {
  
    private Integer id = null;
    private Boolean published = null;
    private String author = null;
    private Integer changes = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatUtils.DATE_FORMAT_ISO8601)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime = null;

    /**
     * unique id of version
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public Version id(Integer id) {
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
     * minimum: 0
     * @return changes
     */
    public Integer getChanges() {
        return changes;
    }

    public void setChanges(Integer changes) {
       this.changes = changes;
    }

    public Version changes(Integer changes) {
      this.changes = changes;
      return this;
    }

    /**
     * date and time when version was published
     * @return datetime
     */
    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
       this.datetime = datetime;
    }

    public Version datetime(LocalDateTime datetime) {
      this.datetime = datetime;
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
      sb.append("    datetime: ").append(toIndentedString(datetime)).append("\n");
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

