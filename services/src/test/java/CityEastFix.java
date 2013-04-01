import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.test.ContextUtils;
import ish.oncourse.test.InitialContextFactoryMock;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.conn.PoolManager;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class CityEastFix {

	private final static String URI = "jdbc:mysql://localhost:3308/w2live_college";

	private ServerRuntime cayenneRuntime;


	public void fix()
	{
		SelectQuery q = new SelectQuery(Enrolment.class);
		q.andQualifier(ExpressionFactory.noMatchDbExp(Enrolment.ANGEL_ID_PROPERTY, null));
		q.andQualifier(ExpressionFactory.matchDbExp(Enrolment.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, 338));
		q.andQualifier(ExpressionFactory.greaterOrEqualDbExp(Enrolment.CREATED_PROPERTY, "2013-03-20 00:00:00"));

		ObjectContext context = ContextUtils.createObjectContext();
		List<Enrolment> list = context.performQuery(q);
		for (Enrolment enrolment : list) {
			System.out.println(enrolment);
		}
	}

	public static void main(String[] args) throws Exception {

		CityEastFix cityEastFix = new CityEastFix();
		cityEastFix.init();
		cityEastFix.fix();
	}

	public static DataSource createDataSource(String name) throws SQLException {
		DataSource dataSource = new PoolManager("com.mysql.jdbc.Driver", URI,1,5, "", "");
		return dataSource;
	}

	private void init() throws NamingException, SQLException {
		cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml");
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());

		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", new InitialContext());

		DataSource oncourse = createDataSource("oncourse");

		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse);
	}
}
