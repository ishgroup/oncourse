package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.Application;
import ish.oncourse.willow.model.checkout.Enrolment;
import ish.oncourse.willow.model.checkout.ProductItem;
import ish.oncourse.willow.model.web.Contact;
import java.util.ArrayList;
import java.util.List;

public class PurchaseItems  {
  
    private Contact contact = null;
    private List<Enrolment> enrolments = new ArrayList<Enrolment>();
    private List<Application> applications = new ArrayList<Application>();
    private List<ProductItem> productItems = new ArrayList<ProductItem>();

    /**
     * Get contact
     * @return contact
     */
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
       this.contact = contact;
    }

    public PurchaseItems contact(Contact contact) {
      this.contact = contact;
      return this;
    }

    /**
     * Get enrolments
     * @return enrolments
     */
    public List<Enrolment> getEnrolments() {
        return enrolments;
    }

    public void setEnrolments(List<Enrolment> enrolments) {
       this.enrolments = enrolments;
    }

    public PurchaseItems enrolments(List<Enrolment> enrolments) {
      this.enrolments = enrolments;
      return this;
    }

    public PurchaseItems addEnrolmentsItem(Enrolment enrolmentsItem) {
      this.enrolments.add(enrolmentsItem);
      return this;
    }

    /**
     * Get applications
     * @return applications
     */
    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
       this.applications = applications;
    }

    public PurchaseItems applications(List<Application> applications) {
      this.applications = applications;
      return this;
    }

    public PurchaseItems addApplicationsItem(Application applicationsItem) {
      this.applications.add(applicationsItem);
      return this;
    }

    /**
     * Get productItems
     * @return productItems
     */
    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
       this.productItems = productItems;
    }

    public PurchaseItems productItems(List<ProductItem> productItems) {
      this.productItems = productItems;
      return this;
    }

    public PurchaseItems addProductItemsItem(ProductItem productItemsItem) {
      this.productItems.add(productItemsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PurchaseItems {\n");
      
      sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
      sb.append("    enrolments: ").append(toIndentedString(enrolments)).append("\n");
      sb.append("    applications: ").append(toIndentedString(applications)).append("\n");
      sb.append("    productItems: ").append(toIndentedString(productItems)).append("\n");
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

