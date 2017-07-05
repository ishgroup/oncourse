package ish.oncourse.willow.model.checkout.concession;


public class ConcessionType  {
  
    private String id = null;
    private String name = null;
    private Boolean hasExpireDate = null;
    private Boolean hasNumber = null;

    /**
     * Get id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public ConcessionType id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public ConcessionType name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Get hasExpireDate
     * @return hasExpireDate
     */
    public Boolean getHasExpireDate() {
        return hasExpireDate;
    }

    public void setHasExpireDate(Boolean hasExpireDate) {
       this.hasExpireDate = hasExpireDate;
    }

    public ConcessionType hasExpireDate(Boolean hasExpireDate) {
      this.hasExpireDate = hasExpireDate;
      return this;
    }

    /**
     * Get hasNumber
     * @return hasNumber
     */
    public Boolean getHasNumber() {
        return hasNumber;
    }

    public void setHasNumber(Boolean hasNumber) {
       this.hasNumber = hasNumber;
    }

    public ConcessionType hasNumber(Boolean hasNumber) {
      this.hasNumber = hasNumber;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ConcessionType {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    hasExpireDate: ").append(toIndentedString(hasExpireDate)).append("\n");
      sb.append("    hasNumber: ").append(toIndentedString(hasNumber)).append("\n");
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

