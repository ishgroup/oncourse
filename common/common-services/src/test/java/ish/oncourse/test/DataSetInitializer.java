package ish.oncourse.test;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.InputStream;

public class DataSetInitializer {

    private static void initDataSetBy(String dataSetPath, String jndiName) throws Exception {
        InputStream st = DataSetInitializer.class.getClassLoader().getResourceAsStream(dataSetPath);
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);


        DataSource onDataSource = ServiceTest.getDataSource(jndiName);
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }

    public static void initDataSets(String dataSetPath) throws Exception {
        initDataSetBy(dataSetPath, "jdbc/oncourse");
    }
}
