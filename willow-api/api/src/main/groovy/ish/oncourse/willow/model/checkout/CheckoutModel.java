package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.Amount;
import ish.oncourse.willow.model.checkout.ContactNode;
import ish.oncourse.willow.model.common.CommonError;
import java.util.ArrayList;
import java.util.List;

public class CheckoutModel  {
  
    private CommonError error = null;
    private List<ContactNode> contactNodes = new ArrayList<ContactNode>();
    private Amount amount = null;
    private String payerId = null;

    /**
     * Get error
     * @return error
     */
    public CommonError getError() {
        return error;
    }

    public void setError(CommonError error) {
       this.error = error;
    }

    public CheckoutModel error(CommonError error) {
      this.error = error;
      return this;
    }

    /**
     * Get contactNodes
     * @return contactNodes
     */
    public List<ContactNode> getContactNodes() {
        return contactNodes;
    }

    public void setContactNodes(List<ContactNode> contactNodes) {
       this.contactNodes = contactNodes;
    }

    public CheckoutModel contactNodes(List<ContactNode> contactNodes) {
      this.contactNodes = contactNodes;
      return this;
    }

    public CheckoutModel addContactNodesItem(ContactNode contactNodesItem) {
      this.contactNodes.add(contactNodesItem);
      return this;
    }

    /**
     * Get amount
     * @return amount
     */
    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
       this.amount = amount;
    }

    public CheckoutModel amount(Amount amount) {
      this.amount = amount;
      return this;
    }

    /**
     * Get payerId
     * @return payerId
     */
    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
       this.payerId = payerId;
    }

    public CheckoutModel payerId(String payerId) {
      this.payerId = payerId;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CheckoutModel {\n");
      
      sb.append("    error: ").append(toIndentedString(error)).append("\n");
      sb.append("    contactNodes: ").append(toIndentedString(contactNodes)).append("\n");
      sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
      sb.append("    payerId: ").append(toIndentedString(payerId)).append("\n");
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

