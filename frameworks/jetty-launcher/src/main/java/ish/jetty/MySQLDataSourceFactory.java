package ish.jetty;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class MySQLDataSourceFactory implements DataSourceFactory {

	public DataSource getDataSource() {
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUrl(LaunchProperties.ISH_DS_URL.getValue());
		dataSource.setUser(LaunchProperties.ISH_DS_USER_NAME.getValue());
		dataSource.setPassword(LaunchProperties.ISH_DS_PASSWORD.getValue());
		return dataSource;
	}

}
