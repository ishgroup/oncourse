package ish.oncourse.portal.services;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.CourseClassFilter;
import org.apache.tapestry5.json.JSONObject;

import java.util.Date;
import java.util.List;

public interface IPortalService {

    public Contact getContact();

    public Date getLastLoginTime();

    public JSONObject getSession(Session session);

    public JSONObject getAttendences(Session session);

    public JSONObject getCalendarEvents();

    public JSONObject getNearesSessionIndex(Integer i);

    public boolean isApproved(CourseClass courseClass);

    public boolean isHistoryEnabled();

	/**
	 * return true if contact has valid enrolment on the courseClass
	 * @param courseClass
	 * @return
	 */
	boolean hasResult(CourseClass courseClass);

	public List<BinaryInfo> getTutorCommonResources();

	public List<BinaryInfo> getResourcesBy(CourseClass courseClass);

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

    /**
     * @return null if the contact is not related to the mailingList
     */
    public Tag getMailingList(long id);

    public boolean hasResults();

    public int getNewResultsCount();
}
