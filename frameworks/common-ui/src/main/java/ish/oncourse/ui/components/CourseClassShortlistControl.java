package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Arrays;
import java.util.Collection;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassShortlistControl {
	@Parameter
	@Property
	private CourseClass courseClass;
	
	@Inject 
	private IWebSiteService webSiteService;

	@Inject
	private ICookiesService cookiesService;
	
	private Collection<String> shortListedClassesIds;

	@SetupRender
	void beginRender(){
		String[] idsArray = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKEY_KEY);
		if(idsArray!=null){
			shortListedClassesIds = Arrays.asList(idsArray);
		}
	}
	
	/**
	 * @return true if the student is already enrolled in this class
	 */
	public boolean isEnrolled() {
		// TODO get current user
		// Contact contact = myApplication().contactForRequest(
		// context().request() );
		Student student = null;// contact == null ? null : contact.student();
		if (student == null) {
			return false;
		}

		for (Enrolment enrolment : student.getActiveEnrolments()) {
			if (enrolment.getCourseClass().equals(courseClass)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPaymentGatewayEnabled() {
		//commented to show the view of enrollment link, because the current value in db is false
		return true;//webSiteService.getCurrentCollege().getIsWebSitePaymentsEnabled();
	}
	
	public boolean isContainedInShortList(){
		
		if(shortListedClassesIds==null){
			return false;
		}
		return shortListedClassesIds.contains(courseClass.getId().toString());
	}
}
