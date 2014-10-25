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
     * @return courseclass session deatils as json object:
     *  id,
     *  startDate : EEEE dd MMMMM h:mma,
     *  endDate : EEEE dd MMMMM h:mma,
     *  attendances:
     *  {
     *      id:
     *      studentId:
     *      type:
     *  }
     *
     * It is used to show session in session roll component
     */
    public JSONObject getJSONSession(Session session);

    public JSONObject getCalendarEvents();

    public JSONObject getNearesSessionIndex(Integer i);

    public boolean isApproved(CourseClass courseClass);

	/**
	 * return true if contact has valid enrolment for the courseClass
	 */
	boolean hasResult(CourseClass courseClass);

    public List<Outcome> getResultsBy(CourseClass courseClass);

    public List<Document> getTutorCommonResources();

	public List<Document> getResourcesBy(CourseClass courseClass);

    public List<Document> getResources();

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

    /**
     * returns survey for selected student and for the <code>courseClass</code>
     */
    Survey getStudentSurveyFor(CourseClass courseClass);

    /**
     * create new survey object for selected student and for the <code>courseClass</code>
     */
    Survey createStudentSurveyFor(CourseClass courseClass);

    /**
     * serializes the <code>Survey</code> to json object.
     */
    JSONObject getJSONSurvey(Survey survey);

    /**
     * returns average survey for the <code>courseClass</code>
     */
    Survey getAverageSurveyFor(CourseClass courseClass);

	/**
	 * Return URL to profile picture for contact
	 * check profile pictype in onCourse system at first
	 * else finde avatar on gravatar servise
	 * 
	 * returns ico-student-default.png (see portal resourses) by default
	 */
	public String getProfilePicturePath(Contact contact);
}
