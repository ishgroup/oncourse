package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.field.ClassHeadings;
import java.util.ArrayList;
import java.util.List;

public class ContactFields  {
  
    private String contactId = null;
    private List<ClassHeadings> classHeadings = new ArrayList<ClassHeadings>();

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
     * Field configurations for each contact's class
     * @return classHeadings
     */
    public List<ClassHeadings> getClassHeadings() {
        return classHeadings;
    }

    public void setClassHeadings(List<ClassHeadings> classHeadings) {
       this.classHeadings = classHeadings;
    }

    public ContactFields classHeadings(List<ClassHeadings> classHeadings) {
      this.classHeadings = classHeadings;
      return this;
    }

    public ContactFields addClassHeadingsItem(ClassHeadings classHeadingsItem) {
      this.classHeadings.add(classHeadingsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactFields {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classHeadings: ").append(toIndentedString(classHeadings)).append("\n");
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

