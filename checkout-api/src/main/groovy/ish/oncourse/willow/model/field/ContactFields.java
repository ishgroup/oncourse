package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.field.FieldHeading;
import java.util.ArrayList;
import java.util.List;

public class ContactFields  {
  
    private String contactId = null;
    private List<FieldHeading> headings = new ArrayList<FieldHeading>();

    /**
     * Contact Id
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public ContactFields contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Related field headings
     * @return headings
     */
    public List<FieldHeading> getHeadings() {
        return headings;
    }

    public void setHeadings(List<FieldHeading> headings) {
       this.headings = headings;
    }

    public ContactFields headings(List<FieldHeading> headings) {
      this.headings = headings;
      return this;
    }

    public ContactFields addHeadingsItem(FieldHeading headingsItem) {
      this.headings.add(headingsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactFields {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    headings: ").append(toIndentedString(headings)).append("\n");
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

