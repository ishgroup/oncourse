package ish.oncourse.admin.services;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.ContextUtil;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.InputStream;

public abstract class AbstractTest extends ServiceTest {
    protected ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        InitialContext context = new InitialContext();
        context.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
        InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);

        initTest("ish.oncourse.admin.services", "", AdminTestModule.class);

        InputStream st = getDataSource();

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);

        ReplacementDataSet rDataSet;
        rDataSet = new ReplacementDataSet(dataSet);
        configDataSet(rDataSet);


        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), rDataSet);
        this.cayenneService = getService(ICayenneService.class);
    }

    protected abstract void configDataSet(ReplacementDataSet rDataSet);

    protected abstract InputStream getDataSource();

}
