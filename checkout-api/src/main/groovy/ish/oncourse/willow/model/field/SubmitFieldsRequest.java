package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.checkout.concession.Concession;
import ish.oncourse.willow.model.field.Field;
import java.util.ArrayList;
import java.util.List;

public class SubmitFieldsRequest  {
  
    private String contactId = null;
    private List<Field> fields = new ArrayList<Field>();
    private Concession concession = null;

    /**
     * Submited contact id
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public SubmitFieldsRequest contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Array of fields
     * @return fields
     */
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
       this.fields = fields;
    }

    public SubmitFieldsRequest fields(List<Field> fields) {
      this.fields = fields;
      return this;
    }

    public SubmitFieldsRequest addFieldsItem(Field fieldsItem) {
      this.fields.add(fieldsItem);
      return this;
    }

    /**
     * Contact's concession
     * @return concession
     */
    public Concession getConcession() {
        return concession;
    }

    public void setConcession(Concession concession) {
       this.concession = concession;
    }

    public SubmitFieldsRequest concession(Concession concession) {
      this.concession = concession;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class SubmitFieldsRequest {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
      sb.append("    concession: ").append(toIndentedString(concession)).append("\n");
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

