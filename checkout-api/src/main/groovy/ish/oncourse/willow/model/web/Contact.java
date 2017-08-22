package ish.oncourse.willow.model.web;


public class Contact  {
  
    private String id = null;
    private String uniqueIdentifier = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private Boolean company = null;
    private Boolean parentRequired = null;

    /**
     * Internal Unique identifier of course
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public Contact id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Unique identifier of contact
     * @return uniqueIdentifier
     */
    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
       this.uniqueIdentifier = uniqueIdentifier;
    }

    public Contact uniqueIdentifier(String uniqueIdentifier) {
      this.uniqueIdentifier = uniqueIdentifier;
      return this;
    }

    /**
     * first name of contact
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
       this.firstName = firstName;
    }

    public Contact firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    /**
     * last name of contact
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
       this.lastName = lastName;
    }

    public Contact lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     * email of contact
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
       this.email = email;
    }

    public Contact email(String email) {
      this.email = email;
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

    public Contact company(Boolean company) {
      this.company = company;
      return this;
    }

    /**
     * Get parentRequired
     * @return parentRequired
     */
    public Boolean getParentRequired() {
        return parentRequired;
    }

    public void setParentRequired(Boolean parentRequired) {
       this.parentRequired = parentRequired;
    }

    public Contact parentRequired(Boolean parentRequired) {
      this.parentRequired = parentRequired;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Contact {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    uniqueIdentifier: ").append(toIndentedString(uniqueIdentifier)).append("\n");
      sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
      sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
      sb.append("    email: ").append(toIndentedString(email)).append("\n");
      sb.append("    company: ").append(toIndentedString(company)).append("\n");
      sb.append("    parentRequired: ").append(toIndentedString(parentRequired)).append("\n");
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

