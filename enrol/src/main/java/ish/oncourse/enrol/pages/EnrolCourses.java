package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Student;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.IPreferenceService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class EnrolCourses {

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IConcessionsService concessionsService;

	@Inject
	private IPreferenceService preferenceService;

	@Inject
	private Request request;
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	@Property
	private List<CourseClass> classesToEnrol;

	@Property
	private CourseClass courseClass;

	/**
	 * studentsSet.allObjects.@sort.contact.fullName
	 */
	@Property
	private List<Student> students;

	@Property
	private Student student;

	@Property
	private int studentIndex;
	
	@Property
	private int courseClassIndex;
	
	@Property
	@Parameter
	private boolean hadPreviousPaymentFailure;

	@Persist
	private Enrolment[][] enrolments;
	
	@Persist
	@Property
	private PaymentIn payment;
	
	@Persist
	@Property
	private Invoice invoice;
	
	@Property
	private ObjectContext context;

	@SetupRender
	void beforeRender() {
		context = cayenneService.newContext();

		
		String[] orderedClassesIds = cookiesService
				.getCookieCollectionValue("shortlist");
		if (orderedClassesIds != null && orderedClassesIds.length != 0) {
			classesToEnrol = courseClassService.loadByIds(orderedClassesIds);
			List<Ordering> orderings = new ArrayList<Ordering>();
			orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "."
					+ Course.CODE_PROPERTY, SortOrder.ASCENDING));
			orderings.add(new Ordering(CourseClass.CODE_PROPERTY,
					SortOrder.DESCENDING));
			Ordering.orderList(classesToEnrol, orderings);
		}

		students = (List<Student>) request.getSession(true).getAttribute(
				"shortlistStudents");
		if(classesToEnrol!=null&&students!=null){
			payment = context.newObject(PaymentIn.class);
			invoice = context.newObject(Invoice.class);
			enrolments=new Enrolment[students.size()][classesToEnrol.size()];
			for(int i=0;i<students.size();i++){
				for(int j=0;j<classesToEnrol.size();j++){
					enrolments[i][j]=context.newObject(Enrolment.class);
					College currentCollege = webSiteService.getCurrentCollege();
					College college = (College) context.localObject(
							currentCollege.getObjectId(), currentCollege);
					enrolments[i][j].setCollege(college);
					Student student = (Student) context.localObject(students.get(i).getObjectId(), students.get(i));
					CourseClass courseClass =(CourseClass) context.localObject( classesToEnrol.get(j).getObjectId(),classesToEnrol.get(j));
					if(!enrolments[i][j].isDuplicated(student)&&courseClass.isHasAvailableEnrolmentPlaces()){
						enrolments[i][j].setStudent(student);
						enrolments[i][j].setCourseClass(courseClass);
					}
				}
			}
			
		}
		
	}

	public boolean isShowConcessionsArea() {
		return (!concessionsService.getActiveConcessionTypes().isEmpty())
				&& Boolean.valueOf(preferenceService.getPreferenceByKey(
						"feature.concessionsInEnrolment").getValueString());
	}

	public String getCoursesListLink() {
		return "http://" + request.getServerName() + "/courses";
	}

	public int getEnrolmentIndex(){
		return studentIndex * classesToEnrol.size() + courseClassIndex; 
	}

	public Enrolment getEnrolment(){
		return enrolments[studentIndex][courseClassIndex];
	}
	public void enrolmentsUpdated() {
		System.out.println("Hello from updated enrolments");
		// TODO Auto-generated method stub
		
	}
}
