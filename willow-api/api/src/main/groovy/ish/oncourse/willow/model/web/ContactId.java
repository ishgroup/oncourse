package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.web.Contact;

public class ContactId  {
  
    private String id = null;
    private Boolean newContact = null;
    private Boolean parentRequired = null;
    private Contact parent = null;

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

    /**
     * Indicates that parent required
     * @return parentRequired
     */
    public Boolean getParentRequired() {
        return parentRequired;
    }

    public void setParentRequired(Boolean parentRequired) {
       this.parentRequired = parentRequired;
    }

    public ContactId parentRequired(Boolean parentRequired) {
      this.parentRequired = parentRequired;
      return this;
    }

    /**
     * Related parant, not null if parent required and parent exist
     * @return parent
     */
    public Contact getParent() {
        return parent;
    }

    public void setParent(Contact parent) {
       this.parent = parent;
    }

    public ContactId parent(Contact parent) {
      this.parent = parent;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactId {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    newContact: ").append(toIndentedString(newContact)).append("\n");
      sb.append("    parentRequired: ").append(toIndentedString(parentRequired)).append("\n");
      sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
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

