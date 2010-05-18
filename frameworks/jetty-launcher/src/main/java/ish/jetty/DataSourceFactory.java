package ish.jetty;

import javax.sql.DataSource;

public interface DataSourceFactory {

	DataSource getDataSource();
}
