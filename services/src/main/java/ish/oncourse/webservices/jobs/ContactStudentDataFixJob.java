package ish.oncourse.webservices.jobs;

import java.util.List;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class ContactStudentDataFixJob implements Job {
	
	private static final Logger logger = Logger.getLogger(ContactStudentDataFixJob.class);
	
	private final ICayenneService cayenneService;
	
	public ContactStudentDataFixJob(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
	}

	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		String sql = "select c1.id as id from Contact c1 inner join Contact c2 on (c1.studentId = c2.studentId and c1.collegeId = c2.collegeId and c1.id > c2.id)";
		SQLTemplate query = new SQLTemplate(Contact.class, sql);
		query.setFetchingDataRows(true);
		
		List<DataRow> rows = context.performQuery(query);
		
		logger.info(String.format("The number of duplicated students: %s.", rows.size()));
		
		if (!rows.isEmpty()) {
			for (DataRow row: rows) {
				Long id = (Long) row.get("id");
				Contact contact = (Contact) Cayenne.objectForPK(context, Contact.class, id);
				if (contact != null) {
					Student newStudent = context.newObject(Student.class);
					newStudent.setCollege(contact.getCollege());
					newStudent.setContact(contact);
					context.commitChanges();
				}
			}
		}
		
		
		logger.info("ContactStudentDataFixJob finished.");
	}
}
