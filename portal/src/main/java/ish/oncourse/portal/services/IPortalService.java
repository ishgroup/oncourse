package ish.oncourse.portal.services;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.CourseClassFilter;
import org.apache.cayenne.CayenneDataObject;
import org.apache.tapestry5.json.JSONObject;

import java.util.Date;
import java.util.List;

public interface IPortalService {

    /**
     * @return logged in user
     */
    public Contact getAuthenticatedUser();

    /**
     * @return selected user
     */
    public Contact getContact();

    /**
     * @return last time when the user was logged in
     */
    public Date getLastLoginTime();

    /**
     * @return courseclass session deatils as json object. Contains startDate, endDate fields in EEEE dd MMMMM h:mma fromat.
     * It is used to show session in session roll component
     */
    public JSONObject getSession(Session session);

    /**
     * @return attendencses for the user and the <source>session</source> as json objects.
     * It is used to show attendences information.
     */
    public JSONObject getAttendences(Session session);

    public JSONObject getCalendarEvents();

    public JSONObject getNearesSessionIndex(Integer i);

    public boolean isApproved(CourseClass courseClass);

	/**
	 * return true if contact has valid enrolment for the courseClass
	 */
	boolean hasResult(CourseClass courseClass);

    public List<Outcome> getResultsBy(CourseClass courseClass);

    public List<BinaryInfo> getTutorCommonResources();

	public List<BinaryInfo> getResourcesBy(CourseClass courseClass);

    public List<BinaryInfo> getResources();

    public List<CourseClass> getContactCourseClasses(CourseClassFilter filter);

	public List<PCourseClass> fillCourseClassSessions(CourseClassFilter filter) ;

	public String getUrlBy(CourseClass courseClass);

	public String getUrlBy(Course course);


    /**
     * @return null if the contact is not related to the class
     */
    public CourseClass getCourseClassBy(long id);

    /**
     * @return null if the contact is not related to the invoice
     */
    public Invoice getInvoiceBy(long id);


    /**
     * @return null if the contact is not related to the paymentIn
     */
    public PaymentIn getPaymentInBy(long id);

    public boolean hasResults();

    public List<PaymentIn> getPayments();

    public List<Enrolment> getEnrolments();

    public Notification getNotification();

    /**
     * The method returns true if the <source>object</source> was created after <source>lastLoginTime</source>
     */
    public boolean isNew(CayenneDataObject object);

    /**
     * Returns ContactRelation for authenticated contact where ContactRelationType.delegatedAccessToContact is true.
     * The method is used for "Switch user" functionality.
     */
    public List<Contact> getChildContacts();

    /**
     * Returns true if <source>contact</source> is selected user.
     */
    public boolean isSelectedContact(Contact contact);

    /**
     * Changes selected contact
     */
    public void selectContact(Contact contact);

    public void logout();
}
