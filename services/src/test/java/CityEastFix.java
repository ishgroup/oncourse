import ish.oncourse.model.*;
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
import java.util.Date;
import java.util.List;

public class CityEastFix {

	//private final static String URI = "jdbc:mysql://localhost:3306/w2live_college?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;characterEncoding=utf8";
	private String uri = "jdbc:mysql://localhost:3306/w2test_college?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;characterEncoding=utf8";

	private ServerRuntime cayenneRuntime;
	private String user;
	private String password;


	public void fix()
	{
		SelectQuery q = new SelectQuery(Enrolment.class);
		q.andQualifier(ExpressionFactory.noMatchDbExp(Enrolment.ANGEL_ID_PROPERTY, null));
		q.andQualifier(ExpressionFactory.matchDbExp(Enrolment.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, 338));
		q.andQualifier(ExpressionFactory.greaterOrEqualDbExp(Enrolment.CREATED_PROPERTY, "2013-03-20 00:00:00"));

		ObjectContext context = ContextUtils.createObjectContext();
		List<Enrolment> list = context.performQuery(q);
		for (Enrolment enrolment : list) {
			QueuedTransactionCreator creator = new QueuedTransactionCreator();
			creator.setObjectContext(context);
			creator.setMainEntiry(enrolment);
			creator.init();
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
		cityEastFix.fix();
	}

	private DataSource createDataSource() throws SQLException {
		return new PoolManager("com.mysql.jdbc.Driver", uri, 1,5, getUser(),getPassword());
	}

	private void init() throws NamingException, SQLException {
		cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml");
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());

		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", new InitialContext());

		DataSource oncourse = createDataSource();

		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse);
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
			queuedTransaction.setCreated(new Date());
			queuedTransaction.setModified(new Date());
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


}
