package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.field.FieldSet;

public class CreateContactParams  {
  
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private FieldSet fieldSet = null;
    private Boolean company = null;

    /**
     * First Name of Contact
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
       this.firstName = firstName;
    }

    public CreateContactParams firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    /**
     * Last Name of Contact
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
       this.lastName = lastName;
    }

    public CreateContactParams lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     * Email of Contact
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
       this.email = email;
    }

    public CreateContactParams email(String email) {
      this.email = email;
      return this;
    }

    /**
     * Get fieldSet
     * @return fieldSet
     */
    public FieldSet getFieldSet() {
        return fieldSet;
    }

    public void setFieldSet(FieldSet fieldSet) {
       this.fieldSet = fieldSet;
    }

    public CreateContactParams fieldSet(FieldSet fieldSet) {
      this.fieldSet = fieldSet;
      return this;
    }

    /**
     * Is company flag
     * @return company
     */
    public Boolean getCompany() {
        return company;
    }

    public void setCompany(Boolean company) {
       this.company = company;
    }

    public CreateContactParams company(Boolean company) {
      this.company = company;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CreateContactParams {\n");
      
      sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
      sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
      sb.append("    email: ").append(toIndentedString(email)).append("\n");
      sb.append("    fieldSet: ").append(toIndentedString(fieldSet)).append("\n");
      sb.append("    company: ").append(toIndentedString(company)).append("\n");
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

