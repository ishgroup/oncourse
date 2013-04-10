import ish.math.MoneyType;
import ish.oncourse.model.*;
import ish.oncourse.test.InitialContextFactoryMock;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.conn.PoolManager;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CityEastFix {

	//private final static String URI = "jdbc:mysql://localhost:3306/w2live_college?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;characterEncoding=utf8";
	private String uri = "jdbc:mysql://localhost:3306/w2test_college?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;characterEncoding=utf8";

	private ServerRuntime cayenneRuntime;
	private String user;
	private String password;


	public void fixEnrolments()
	{
		SelectQuery q = new SelectQuery(Enrolment.class);
		q.andQualifier(ExpressionFactory.noMatchDbExp(Enrolment.ANGEL_ID_PROPERTY, null));
		q.andQualifier(ExpressionFactory.matchDbExp(Enrolment.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, 338));
		q.andQualifier(ExpressionFactory.greaterOrEqualDbExp(Enrolment.CREATED_PROPERTY, "2013-03-20 00:00:00"));

		ObjectContext context = cayenneRuntime.getContext();
		List<Enrolment> list = context.performQuery(q);
		for (Enrolment enrolment : list) {
			QueuedTransactionCreator creator = new QueuedTransactionCreator();
			creator.setObjectContext(context);
			creator.setMainEntiry(enrolment);
			creator.init();
			
			resetEnrolment(enrolment, creator);
			
			context.commitChanges();
		}
	}

	public void fixContacts()
	{
		SelectQuery q = new SelectQuery(Contact.class);
		q.andQualifier(ExpressionFactory.noMatchDbExp(Enrolment.ANGEL_ID_PROPERTY, null));
		q.andQualifier(ExpressionFactory.matchDbExp(Enrolment.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, 338));
		q.andQualifier(ExpressionFactory.greaterOrEqualDbExp(Enrolment.CREATED_PROPERTY, "2013-03-20 00:00:00"));

		ObjectContext context = cayenneRuntime.getContext();
		List<Contact> list = context.performQuery(q);
		for (Contact contact : list) {
			QueuedTransactionCreator creator = new QueuedTransactionCreator();
			creator.setObjectContext(context);
			creator.setMainEntiry(contact);
			creator.init();

			resetContact(contact, creator);

			context.commitChanges();
		}
	}

	public void fixCourseClasses()
	{
		SelectQuery q = new SelectQuery(CourseClass.class);
		q.andQualifier(ExpressionFactory.noMatchDbExp(Enrolment.ANGEL_ID_PROPERTY, null));
		q.andQualifier(ExpressionFactory.matchDbExp(Enrolment.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, 338));
		q.andQualifier(ExpressionFactory.greaterOrEqualDbExp(Enrolment.CREATED_PROPERTY, "2013-03-20 00:00:00"));

		ObjectContext context = cayenneRuntime.getContext();
		List<CourseClass> list = context.performQuery(q);
		for (CourseClass courseClass : list) {
			QueuedTransactionCreator creator = new QueuedTransactionCreator();
			creator.setObjectContext(context);
			creator.setMainEntiry(courseClass);
			creator.init();

			resetCourseClass(courseClass, creator);

			context.commitChanges();
		}
	}



	public static void main(String[] args) throws Exception {

		CityEastFix cityEastFix = new CityEastFix();
		cityEastFix.setUser(args[0]);
		cityEastFix.setPassword(args[1]);
		if (args.length > 2)
			cityEastFix.setUri(args[2]);
		cityEastFix.init();
		cityEastFix.fixContacts();
		cityEastFix.fixCourseClasses();
		cityEastFix.fixEnrolments();
	}

	private DataSource createDataSource() throws SQLException {
		return new PoolManager("com.mysql.jdbc.Driver", uri, 1,5, getUser(),getPassword());
	}

	private void init() throws NamingException, SQLException {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());

		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", new InitialContext());

		DataSource oncourse = createDataSource();

		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse);

		cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml");

		for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}


	public static class QueuedTransactionCreator
	{
		private QueuedTransaction queuedTransaction;

		private ObjectContext objectContext;
		private Queueable mainEntiry;

		public void init()
		{
			queuedTransaction = getObjectContext().newObject(QueuedTransaction.class);
			queuedTransaction.setCollege(mainEntiry.getCollege());
			queuedTransaction.setTransactionKey("CityEastFix" + mainEntiry.getId());
			queuedTransaction.setCreated(mainEntiry.getCreated());
			queuedTransaction.setModified(mainEntiry.getCreated());
			addEntiry(mainEntiry);
		}
		public void addEntiry(Queueable queueable)
		{
			QueuedRecord record = getObjectContext().newObject(QueuedRecord.class);
			record.setAction(QueuedRecordAction.UPDATE);
			record.setNumberOfAttempts(3);
			record.setCollege(queueable.getCollege());
			record.setEntityIdentifier(queueable.getObjectId().getEntityName());
			record.setEntityWillowId(queueable.getId());
			record.setLastAttemptTimestamp(new Date());
			record.setQueuedTransaction(queuedTransaction);
		}

		public ObjectContext getObjectContext() {
			return objectContext;
		}

		public void setObjectContext(ObjectContext objectContext) {
			this.objectContext = objectContext;
		}

		public Queueable getMainEntiry() {
			return mainEntiry;
		}

		public void setMainEntiry(Queueable mainEntiry) {
			this.mainEntiry = mainEntiry;
		}
	}

	private void resetEnrolment(Enrolment e, QueuedTransactionCreator creator) {
		if (e.getCourseClass() != null) {
			resetCourseClass(e.getCourseClass(), creator);
		}
		
		if (e.getStudent() != null) {
			resetStudent(e.getStudent(), creator);
			
			if (e.getCourseClass() != null) {
				for (Session s : e.getCourseClass().getSessions()) {
					resetAttendance(e.getAttendanceForSessionAndStudent(s, e.getStudent()), creator);
				}
			}
		}
		
		for (Outcome o : e.getOutcomes()) {
			resetOutcome(o, creator);
		}
		for (InvoiceLine invoiceLine : e.getInvoiceLines()) {
			resetInvoiceLine(invoiceLine, creator);
		}
		
		e.setAngelId(null);
		creator.addEntiry(e);
	}
	
	private void resetInvoiceLine(InvoiceLine il, QueuedTransactionCreator creator) {
		if (il.getInvoice() != null) {
			resetInvoice(il.getInvoice(), creator);
		}
		
		for (InvoiceLineDiscount ild : il.getInvoiceLineDiscounts()) {
			resetInvoiceLineDiscount(ild, creator);
		}
		
		il.setAngelId(null);
		creator.addEntiry(il);
	}
	
	private void resetInvoice(Invoice i, QueuedTransactionCreator creator) {
		
		// if the invoice was reset through another invoice line then no need in doing it again
		if (i.getAngelId() == null) {
			return;
		}
		
		if (i.getContact() != null) {
			resetContact(i.getContact(), creator);
		}
		
		for (PaymentInLine pil : i.getPaymentInLines()) {
			resetPaymentInLine(pil, creator);
		}
		
		i.setAngelId(null);
		creator.addEntiry(i);
	}
	
	private void resetPaymentInLine(PaymentInLine pil, QueuedTransactionCreator creator) {
		if (pil.getPaymentIn() != null) {
			resetPaymentIn(pil.getPaymentIn(), creator);
		}
		
		pil.setAngelId(null);
		creator.addEntiry(pil);
	}
	
	private void resetPaymentIn(PaymentIn p, QueuedTransactionCreator creator) {
		if (p.getContact() != null) {
			resetContact(p.getContact(), creator);
		}
		
		p.setAngelId(null);
		creator.addEntiry(p);
	}
	
	private void resetInvoiceLineDiscount(InvoiceLineDiscount ild, QueuedTransactionCreator creator) {
		if (ild.getDiscount() != null) {
			resetDiscount(ild.getDiscount(), creator);
		}
		
		ild.setAngelId(null);
		creator.addEntiry(ild);
	}
	
	private void resetDiscount(Discount d, QueuedTransactionCreator creator) {
		d.setAngelId(null);
		creator.addEntiry(d);
	}
	
	private void resetOutcome(Outcome o, QueuedTransactionCreator creator) {
		o.setAngelId(null);
		creator.addEntiry(o);
	}
	
	private void resetCourseClass(CourseClass courseClass, QueuedTransactionCreator creator) {
		if (courseClass.getCourse() != null) {
			resetCourse(courseClass.getCourse(), creator);
		}
		
		for (Session s : courseClass.getSessions()) {
			resetSession(s, creator);
		}
		
		if (courseClass.getRoom() != null) {
			resetRoom(courseClass.getRoom(), creator);
		}
		
		for (TutorRole role : courseClass.getTutorRoles()) {
			resetTutorRole(role, creator);
		}
		
		courseClass.setAngelId(null);
		creator.addEntiry(courseClass);
	}
	
	private void resetAttendance(Attendance a, QueuedTransactionCreator creator) {
		a.getObjectContext().deleteObjects(a);
	}
	
	private void resetCourse(Course course, QueuedTransactionCreator creator) {
		course.setAngelId(null);
		creator.addEntiry(course);
	}
	
	private void resetRoom(Room room, QueuedTransactionCreator creator) {
		if (room.getSite() != null) {
			resetSite(room.getSite(), creator);
		}
		
		room.setAngelId(null);
		creator.addEntiry(room);
	}
	
	private void resetSite(Site site, QueuedTransactionCreator creator) {
		site.setAngelId(null);
		creator.addEntiry(site);
	}
	
	private void resetSession(Session s, QueuedTransactionCreator creator) {
		if (s.getRoom() != null) {
			resetRoom(s.getRoom(), creator);
		}
		
		for (SessionModule sm : s.getSessionModules()) {
			resetSessionModule(sm, creator);
		}
		
		for (SessionTutor st : s.getSessionTutors()) {
			resetSessionTutor(st, creator);
		}
		
		s.setAngelId(null);
		creator.addEntiry(s);
	}
	
	private void resetSessionModule(SessionModule sm, QueuedTransactionCreator creator) {
		sm.setAngelId(null);
		creator.addEntiry(sm);
	}
	
	private void resetSessionTutor(SessionTutor st, QueuedTransactionCreator creator) {
		if (st.getTutor() != null) {
			resetTutor(st.getTutor(), creator);
		}
		
		st.setAngelId(null);
		creator.addEntiry(st);
	}
	
	private void resetTutorRole(TutorRole role, QueuedTransactionCreator creator) {
		if (role.getTutor() != null) {
			resetTutor(role.getTutor(), creator);
		}
		
		role.setAngelId(null);
		creator.addEntiry(role);
	}
	
	private void resetTutor(Tutor tutor, QueuedTransactionCreator creator) {
		if (tutor.getContact() != null) {
			resetContact(tutor.getContact(), creator);
		}
		
		tutor.setAngelId(null);
		creator.addEntiry(tutor);
	}
	
	private void resetStudent(Student student, QueuedTransactionCreator creator) {
		if (student.getContact() != null) {
			resetContact(student.getContact(), creator);
		}
		
		student.setAngelId(null);
		creator.addEntiry(student);
	}
	
	private void resetContact(Contact contact, QueuedTransactionCreator creator) {
		if (contact.getStudent() != null) {
			contact.getStudent().setAngelId(null);
		}
		
		if (contact.getTutor() != null) {
			contact.getTutor().setAngelId(null);
		}
		
		contact.setAngelId(null);
		creator.addEntiry(contact);
	}
}
