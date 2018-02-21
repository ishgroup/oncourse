package ish.oncourse.willow.editor.v1.model;

import ish.oncourse.willow.editor.v1.model.RedirectItem;
import java.util.ArrayList;
import java.util.List;

public class Redirects  {
  
    private List<RedirectItem> rules = new ArrayList<>();

    /**
     * Get rules
     * @return rules
     */
    public List<RedirectItem> getRules() {
        return rules;
    }

    public void setRules(List<RedirectItem> rules) {
       this.rules = rules;
    }

    public Redirects rules(List<RedirectItem> rules) {
      this.rules = rules;
      return this;
    }

    public Redirects addRulesItem(RedirectItem rulesItem) {
      this.rules.add(rulesItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Redirects {\n");
      
      sb.append("    rules: ").append(toIndentedString(rules)).append("\n");
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

