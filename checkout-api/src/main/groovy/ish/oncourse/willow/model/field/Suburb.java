package ish.oncourse.willow.model.field;


public class Suburb  {
  
    private String state = null;
    private String suburb = null;
    private String postcode = null;

    /**
     * State that corresponds certain location
     * @return state
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
       this.state = state;
    }

    public Suburb state(String state) {
      this.state = state;
      return this;
    }

    /**
     * Suburb that corresponds certain location
     * @return suburb
     */
    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
       this.suburb = suburb;
    }

    public Suburb suburb(String suburb) {
      this.suburb = suburb;
      return this;
    }

    /**
     * Postcode that corresponds certain location
     * @return postcode
     */
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
       this.postcode = postcode;
    }

    public Suburb postcode(String postcode) {
      this.postcode = postcode;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Suburb {\n");
      
      sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

