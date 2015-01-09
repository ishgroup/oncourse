package ish.oncourse.webservices.utils;

import ish.math.MoneyType;
import ish.oncourse.test.InitialContextFactoryMock;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.conn.PoolManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class AbstractUtil {

	private String dataSourceUrl;
	private String user;
	private String password;

	private ServerRuntime cayenneRuntime;

	public DataSource createDataSource() throws SQLException {
		return new PoolManager("com.mysql.jdbc.Driver", dataSourceUrl, 1,5, getUser(),getPassword());
	}

	public void init() throws NamingException, SQLException {
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

	public String getDataSourceUrl() {
		return dataSourceUrl;
	}

	public void setDataSourceUrl(String dataSourceUrl) {
		this.dataSourceUrl = dataSourceUrl;
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

	public ServerRuntime getCayenneRuntime() {
		return cayenneRuntime;
	}
}
