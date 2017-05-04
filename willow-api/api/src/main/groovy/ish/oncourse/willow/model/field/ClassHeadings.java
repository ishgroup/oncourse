package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.field.FieldHeading;
import java.util.ArrayList;
import java.util.List;

public class ClassHeadings  {
  
    private String classId = null;
    private List<FieldHeading> headings = new ArrayList<FieldHeading>();
    private FieldHeading dummyHeading = null;

    /**
     * Reffered class id
     * @return classId
     */
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
       this.classId = classId;
    }

    public ClassHeadings classId(String classId) {
      this.classId = classId;
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

    public ClassHeadings headings(List<FieldHeading> headings) {
      this.headings = headings;
      return this;
    }

    public ClassHeadings addHeadingsItem(FieldHeading headingsItem) {
      this.headings.add(headingsItem);
      return this;
    }

    /**
     * Dummy headin filled by fields that not linked to any real Field Heading
     * @return dummyHeading
     */
    public FieldHeading getDummyHeading() {
        return dummyHeading;
    }

    public void setDummyHeading(FieldHeading dummyHeading) {
       this.dummyHeading = dummyHeading;
    }

    public ClassHeadings dummyHeading(FieldHeading dummyHeading) {
      this.dummyHeading = dummyHeading;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ClassHeadings {\n");
      
      sb.append("    classId: ").append(toIndentedString(classId)).append("\n");
      sb.append("    headings: ").append(toIndentedString(headings)).append("\n");
      sb.append("    dummyHeading: ").append(toIndentedString(dummyHeading)).append("\n");
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

