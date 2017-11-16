package ish.oncourse.willow.editor.model;


public class User  {
  
    private Double id = null;
    private String firstName = null;
    private String lastName = null;

    /**
     * Unique identifier of the user
     * @return id
     */
    public Double getId() {
        return id;
    }

    public void setId(Double id) {
       this.id = id;
    }

    public User id(Double id) {
      this.id = id;
      return this;
    }

    /**
     * First name of the user
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
       this.firstName = firstName;
    }

    public User firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    /**
     * Last name of the user
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
       this.lastName = lastName;
    }

    public User lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class User {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
      sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
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

