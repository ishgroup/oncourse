package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.StudentMembership;
import ish.oncourse.willow.model.checkout.concession.Concession;
import java.util.ArrayList;
import java.util.List;

public class ConcessionsAndMemberships  {
  
    private List<Concession> concessions = new ArrayList<Concession>();
    private List<StudentMembership> memberships = new ArrayList<StudentMembership>();

    /**
     * Get concessions
     * @return concessions
     */
    public List<Concession> getConcessions() {
        return concessions;
    }

    public void setConcessions(List<Concession> concessions) {
       this.concessions = concessions;
    }

    public ConcessionsAndMemberships concessions(List<Concession> concessions) {
      this.concessions = concessions;
      return this;
    }

    public ConcessionsAndMemberships addConcessionsItem(Concession concessionsItem) {
      this.concessions.add(concessionsItem);
      return this;
    }

    /**
     * Get memberships
     * @return memberships
     */
    public List<StudentMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<StudentMembership> memberships) {
       this.memberships = memberships;
    }

    public ConcessionsAndMemberships memberships(List<StudentMembership> memberships) {
      this.memberships = memberships;
      return this;
    }

    public ConcessionsAndMemberships addMembershipsItem(StudentMembership membershipsItem) {
      this.memberships.add(membershipsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ConcessionsAndMemberships {\n");
      
      sb.append("    concessions: ").append(toIndentedString(concessions)).append("\n");
      sb.append("    memberships: ").append(toIndentedString(memberships)).append("\n");
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

