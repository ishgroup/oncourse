package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.Application;
import ish.oncourse.willow.model.checkout.Article;
import ish.oncourse.willow.model.checkout.Enrolment;
import ish.oncourse.willow.model.checkout.Membership;
import ish.oncourse.willow.model.checkout.Voucher;
import ish.oncourse.willow.model.checkout.WaitingList;
import java.util.ArrayList;
import java.util.List;

public class ContactNode  {
  
    private String contactId = null;
    private String contactFirstName = null;
    private String contactLastName = null;
    private String contactEmail = null;
    private List<Enrolment> enrolments = new ArrayList<Enrolment>();
    private List<Application> applications = new ArrayList<Application>();
    private List<Article> articles = new ArrayList<Article>();
    private List<Membership> memberships = new ArrayList<Membership>();
    private List<Voucher> vouchers = new ArrayList<Voucher>();
    private List<WaitingList> waitingLists = new ArrayList<WaitingList>();
    private List<String> suggestedCourseIds = new ArrayList<String>();
    private List<String> suggestedProductIds = new ArrayList<String>();

    /**
     * Get contactId
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public ContactNode contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Get contactFirstName
     * @return contactFirstName
     */
    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
       this.contactFirstName = contactFirstName;
    }

    public ContactNode contactFirstName(String contactFirstName) {
      this.contactFirstName = contactFirstName;
      return this;
    }

    /**
     * Get contactLastName
     * @return contactLastName
     */
    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
       this.contactLastName = contactLastName;
    }

    public ContactNode contactLastName(String contactLastName) {
      this.contactLastName = contactLastName;
      return this;
    }

    /**
     * Get contactEmail
     * @return contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
       this.contactEmail = contactEmail;
    }

    public ContactNode contactEmail(String contactEmail) {
      this.contactEmail = contactEmail;
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

    public ContactNode enrolments(List<Enrolment> enrolments) {
      this.enrolments = enrolments;
      return this;
    }

    public ContactNode addEnrolmentsItem(Enrolment enrolmentsItem) {
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

    public ContactNode applications(List<Application> applications) {
      this.applications = applications;
      return this;
    }

    public ContactNode addApplicationsItem(Application applicationsItem) {
      this.applications.add(applicationsItem);
      return this;
    }

    /**
     * Get articles
     * @return articles
     */
    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
       this.articles = articles;
    }

    public ContactNode articles(List<Article> articles) {
      this.articles = articles;
      return this;
    }

    public ContactNode addArticlesItem(Article articlesItem) {
      this.articles.add(articlesItem);
      return this;
    }

    /**
     * Get memberships
     * @return memberships
     */
    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
       this.memberships = memberships;
    }

    public ContactNode memberships(List<Membership> memberships) {
      this.memberships = memberships;
      return this;
    }

    public ContactNode addMembershipsItem(Membership membershipsItem) {
      this.memberships.add(membershipsItem);
      return this;
    }

    /**
     * Get vouchers
     * @return vouchers
     */
    public List<Voucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<Voucher> vouchers) {
       this.vouchers = vouchers;
    }

    public ContactNode vouchers(List<Voucher> vouchers) {
      this.vouchers = vouchers;
      return this;
    }

    public ContactNode addVouchersItem(Voucher vouchersItem) {
      this.vouchers.add(vouchersItem);
      return this;
    }

    /**
     * Get waitingLists
     * @return waitingLists
     */
    public List<WaitingList> getWaitingLists() {
        return waitingLists;
    }

    public void setWaitingLists(List<WaitingList> waitingLists) {
       this.waitingLists = waitingLists;
    }

    public ContactNode waitingLists(List<WaitingList> waitingLists) {
      this.waitingLists = waitingLists;
      return this;
    }

    public ContactNode addWaitingListsItem(WaitingList waitingListsItem) {
      this.waitingLists.add(waitingListsItem);
      return this;
    }

    /**
     * Get suggestedCourseIds
     * @return suggestedCourseIds
     */
    public List<String> getSuggestedCourseIds() {
        return suggestedCourseIds;
    }

    public void setSuggestedCourseIds(List<String> suggestedCourseIds) {
       this.suggestedCourseIds = suggestedCourseIds;
    }

    public ContactNode suggestedCourseIds(List<String> suggestedCourseIds) {
      this.suggestedCourseIds = suggestedCourseIds;
      return this;
    }

    public ContactNode addSuggestedCourseIdsItem(String suggestedCourseIdsItem) {
      this.suggestedCourseIds.add(suggestedCourseIdsItem);
      return this;
    }

    /**
     * Get suggestedProductIds
     * @return suggestedProductIds
     */
    public List<String> getSuggestedProductIds() {
        return suggestedProductIds;
    }

    public void setSuggestedProductIds(List<String> suggestedProductIds) {
       this.suggestedProductIds = suggestedProductIds;
    }

    public ContactNode suggestedProductIds(List<String> suggestedProductIds) {
      this.suggestedProductIds = suggestedProductIds;
      return this;
    }

    public ContactNode addSuggestedProductIdsItem(String suggestedProductIdsItem) {
      this.suggestedProductIds.add(suggestedProductIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactNode {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    contactFirstName: ").append(toIndentedString(contactFirstName)).append("\n");
      sb.append("    contactLastName: ").append(toIndentedString(contactLastName)).append("\n");
      sb.append("    contactEmail: ").append(toIndentedString(contactEmail)).append("\n");
      sb.append("    enrolments: ").append(toIndentedString(enrolments)).append("\n");
      sb.append("    applications: ").append(toIndentedString(applications)).append("\n");
      sb.append("    articles: ").append(toIndentedString(articles)).append("\n");
      sb.append("    memberships: ").append(toIndentedString(memberships)).append("\n");
      sb.append("    vouchers: ").append(toIndentedString(vouchers)).append("\n");
      sb.append("    waitingLists: ").append(toIndentedString(waitingLists)).append("\n");
      sb.append("    suggestedCourseIds: ").append(toIndentedString(suggestedCourseIds)).append("\n");
      sb.append("    suggestedProductIds: ").append(toIndentedString(suggestedProductIds)).append("\n");
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

