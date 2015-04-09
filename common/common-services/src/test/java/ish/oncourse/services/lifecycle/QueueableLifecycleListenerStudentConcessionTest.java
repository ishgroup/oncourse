package ish.oncourse.services.lifecycle;

import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueueableLifecycleListenerStudentConcessionTest extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);
		InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/studentConcessionDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testLinkStudentWithConcession() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext newContext = cayenneService.newContext();

		SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(newContext);
		Contact contact = builder.createContact();
		Student student = builder.createStudent(contact);

		newContext.commitChanges();

		StudentConcession studentConcession = newContext.newObject(StudentConcession.class);
		College college = student.getCollege();
		studentConcession.setCollege(newContext.localObject(college));

		ConcessionType concessionType = Cayenne.objectForPK(newContext, ConcessionType.class, 1);
		studentConcession.setConcessionType(newContext.localObject(concessionType));

		studentConcession.setConcessionNumber("2");
		studentConcession.setExpiresOn(new Date());
		studentConcession.setStudent(newContext.localObject(student));

		newContext.commitChanges();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);

		ITable actualData = dbUnitConnection.createQueryTable("QueuedTransaction", String.format("select * from QueuedTransaction"));
		assertEquals("Expecting two transactions.", 2, actualData.getRowCount());

		SortedSet<BigInteger> s = new TreeSet<>();

		for (int i = 0; i < actualData.getRowCount(); i++) {
			BigInteger id = (BigInteger) actualData.getValue(i, "id");
			s.add(id);
		}

		newContext = cayenneService.newContext();
		
		System.out.println(String.format("%s - %s", s.first().longValue(), s.last().longValue()));

		QueuedTransaction first = Cayenne.objectForPK(newContext, QueuedTransaction.class, s.first().longValue());
		QueuedTransaction second = Cayenne.objectForPK(newContext, QueuedTransaction.class, s.last().longValue());

		boolean foundStudentConsession = false;
		boolean foundStudent = false;
		
		System.out.println("-----------------------------First Transaction -----------------------------------");
		
		for (QueuedRecord r : first.getQueuedRecords()) {
			System.out.println("EntityIdentifier:" + r.getEntityIdentifier());
			
			if ("StudentConcession".equalsIgnoreCase(r.getEntityIdentifier())) {
				foundStudentConsession = true;
			}
			
			if ("Student".equalsIgnoreCase(r.getEntityIdentifier())) {
				foundStudent = true;
			}
		}
		
		System.out.println("-----------------------------Second Transaction -----------------------------------");
		
		assertTrue("Found student", foundStudent);
		assertTrue("Not found StudentConcession", !foundStudentConsession);

		boolean foundConcessionType = false;
		foundStudent = false;
		
		for (QueuedRecord r : second.getQueuedRecords()) {
			System.out.println("EntityIdentifier:" + r.getEntityIdentifier());
			
			if ("StudentConcession".equalsIgnoreCase(r.getEntityIdentifier())) {
				foundStudentConsession = true;
			}
			
			if ("Student".equalsIgnoreCase(r.getEntityIdentifier())) {
				foundStudent = true;
			}
			
			if ("ConcessionType".equalsIgnoreCase(r.getEntityIdentifier())) {
				foundConcessionType = true;
			}
		}
		
		assertTrue("Found StudentConcession", foundStudentConsession);
		assertTrue("Found Student", foundStudent);
		assertTrue("Found ConcessionType", foundConcessionType);
	}
}
