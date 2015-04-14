package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationPortTypeTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

public class PaymentServiceImplTest extends ServiceTest {
	private ICayenneService service;
	private College college;
	
	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/paymentDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
		service = getService(ICayenneService.class);
		@SuppressWarnings("unchecked")
		List<College> colleges = service.sharedContext().performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		service.sharedContext().performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		college = colleges.isEmpty() ? null : colleges.get(0);
	}
	
	private College localizeCollege(ObjectContext context) {
		if (college == null) {
			return null;
		} else {
			return (College) context.localObject(college);
		}
	}

}
