package ish.oncourse.willow.model.web;


public class Site  {
  
    private String name = null;
    private String street = null;
    private String suburb = null;
    private String postcode = null;

    /**
     * Site name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Site name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Site address
     * @return street
     */
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
       this.street = street;
    }

    public Site street(String street) {
      this.street = street;
      return this;
    }

    /**
     * Site suburb
     * @return suburb
     */
    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
       this.suburb = suburb;
    }

    public Site suburb(String suburb) {
      this.suburb = suburb;
      return this;
    }

    /**
     * Site postcode
     * @return postcode
     */
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
       this.postcode = postcode;
    }

    public Site postcode(String postcode) {
      this.postcode = postcode;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Site {\n");
      
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    street: ").append(toIndentedString(street)).append("\n");
      sb.append("    suburb: ").append(toIndentedString(suburb)).append("\n");
      sb.append("    postcode: ").append(toIndentedString(postcode)).append("\n");
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

