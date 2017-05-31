package ish.oncourse.willow.model.web;


public class ContactId  {
  
    private String id = null;
    private Boolean newContact = null;

    /**
     * Contact id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public ContactId id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Indicates that new contact created
     * @return newContact
     */
    public Boolean getNewContact() {
        return newContact;
    }

    public void setNewContact(Boolean newContact) {
       this.newContact = newContact;
    }

    public ContactId newContact(Boolean newContact) {
      this.newContact = newContact;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactId {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    newContact: ").append(toIndentedString(newContact)).append("\n");
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

