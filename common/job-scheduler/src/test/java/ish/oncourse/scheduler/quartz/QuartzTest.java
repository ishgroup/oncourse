package ish.oncourse.scheduler.quartz;

import ish.oncourse.test.TestContext;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzTest {
    private TestContext context;

    public void init() throws Exception {
        context = new TestContext().shouldCleanTables(true).shouldCreateTables(true).open();

        ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(QuartzTest.class.getClassLoader());

        ResourceAccessor clFO = new ClassLoaderResourceAccessor();
        ResourceAccessor fsFO = new FileSystemResourceAccessor();

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(context.getDS().getConnection()));
        Liquibase liquibase = new Liquibase("liquibase.db.changelog.xml",
                new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);
        liquibase.update("production");
    }

    private JobStoreTX getJobStoreTX() throws Exception {
        DBConnectionManager.getInstance().addConnectionProvider("willow", new WillowConnectionProvider(context.getDS()));
        JobStoreTX jobStore = new JobStoreTX();
        jobStore.setDataSource("willow");
        jobStore.setClusterCheckinInterval(20000);
        jobStore.setIsClustered(true);
        jobStore.setDriverDelegateClass(StdJDBCDelegate.class.getName());
        jobStore.setTablePrefix("QRTZ_");
        return jobStore;
    }

    public static void main(String[] args) throws Exception {
        QuartzTest test = new QuartzTest();
        test.init();

        JobStoreTX jobStoreTX = test.getJobStoreTX();

        DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();

        factory.createScheduler("willow", UUID.randomUUID().toString(),
                new SimpleThreadPool(5, Thread.NORM_PRIORITY), jobStoreTX);

        Scheduler scheduler = factory.getScheduler("willow");
        scheduler.start();

        JobKey jobKey = new JobKey("Job1", "JobGroup1");
        TriggerKey triggerKey = new TriggerKey("Job1Trigger", "TriggerGroup1");

        JobDetail job = scheduler.getJobDetail(jobKey);
        if (job == null)
            job = newJob(Job1.class)
                    .withIdentity(jobKey)
                    .build();


        Trigger trigger = scheduler.getTrigger(triggerKey);
        if (trigger == null)
            trigger = newTrigger()
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1)
                            .repeatForever())
                    .build();

        if (!scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey))
            scheduler.scheduleJob(job, trigger);
    }

    public static class Job1 implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println(context);
        }
    }

    public static class WillowConnectionProvider implements ConnectionProvider {

        private DataSource dataSource;

        public WillowConnectionProvider(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }

        @Override
        public void shutdown() throws SQLException {

        }

        @Override
        public void initialize() throws SQLException {

        }
    }
}
