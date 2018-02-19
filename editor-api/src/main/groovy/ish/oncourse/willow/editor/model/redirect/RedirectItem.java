package ish.oncourse.willow.editor.model.redirect;


public class RedirectItem  {
  
    private String from = null;
    private String to = null;

    /**
     * Redirect from url
     * @return from
     */
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
       this.from = from;
    }

    public RedirectItem from(String from) {
      this.from = from;
      return this;
    }

    /**
     * Redirect to url
     * @return to
     */
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
       this.to = to;
    }

    public RedirectItem to(String to) {
      this.to = to;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class RedirectItem {\n");
      
      sb.append("    from: ").append(toIndentedString(from)).append("\n");
      sb.append("    to: ").append(toIndentedString(to)).append("\n");
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

