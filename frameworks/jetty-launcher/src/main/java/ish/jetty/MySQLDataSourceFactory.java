package ish.jetty;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class MySQLDataSourceFactory implements DataSourceFactory {

	public DataSource getDataSource(String dataSourceName) {
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		LaunchProperties dataSourceProperties=LaunchProperties.getByValue(dataSourceName);
		if(dataSourceProperties!=null){
			switch (dataSourceProperties) {
			case ISH_DS_ONCOURSE:
				dataSource.setUrl(LaunchProperties.ISH_DS_ONCOURSE_URL.getValue());
				break;
			case ISH_DS_ONCOURSE_BINARY:
				dataSource.setUrl(LaunchProperties.ISH_DS_ONCOURSE_BINARY_URL.getValue());
				break;
			case ISH_DS_ONCOURSE_REFERENCE:
				dataSource.setUrl(LaunchProperties.ISH_DS_ONCOURSE_REFERENCE_URL.getValue());
				break;
			default:
				dataSource.setUrl(LaunchProperties.ISH_DS_ONCOURSE_URL.getValue());
			}
		}
		
		dataSource.setUser(LaunchProperties.ISH_DS_USER_NAME.getValue());
		dataSource.setPassword(LaunchProperties.ISH_DS_PASSWORD.getValue());
		return dataSource;
	}

}
