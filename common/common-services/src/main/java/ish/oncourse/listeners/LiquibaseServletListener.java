package ish.oncourse.listeners;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Connection;

public class LiquibaseServletListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger();
    private final static String CHANGELOG = "liquibase.db.changelog.xml";
    private final static String CONTEXT = "production";

    private DataSource dataSource;

    public LiquibaseServletListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try (Connection connection = dataSource.getConnection()) {
            Thread currentThread = Thread.currentThread();
            ClassLoader contextClassLoader = currentThread.getContextClassLoader();
            ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

            ResourceAccessor clFO = new ClassLoaderResourceAccessor();
            ResourceAccessor fsFO = new FileSystemResourceAccessor();

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(CHANGELOG, new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);

            liquibase.update(CONTEXT);
        } catch (Exception ex) {
            logger.error("Liquibase update failed with error.", ex);
        }
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }
}
