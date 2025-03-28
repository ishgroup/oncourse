/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server;

import com.google.inject.*;
import com.google.inject.name.Names;
import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.cayenne.CayenneModule;
import io.bootique.command.CommandDecorator;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jetty.MappedFilter;
import io.bootique.jetty.MappedServlet;
import io.bootique.jetty.command.ServerCommand;
import ish.oncourse.common.ResourcesUtil;
import ish.oncourse.server.api.servlet.*;
import ish.oncourse.server.db.AngelCayenneModule;
import ish.oncourse.server.integration.EventService;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.integration.PluginsPrefsService;
import ish.oncourse.server.jetty.AngelJettyModule;
import ish.oncourse.server.lifecycle.*;
import ish.oncourse.server.modules.AngelJobFactory;
import ish.oncourse.server.preference.UserPreferenceService;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.scripting.api.EmailService;
import ish.oncourse.server.security.CertificateUpdateWatcher;
import ish.oncourse.server.security.api.IPermissionService;
import ish.oncourse.server.services.AuditService;
import ish.oncourse.server.services.ISystemUserService;
import ish.oncourse.server.servlet.HealthCheckServlet;
import ish.util.Maps;
import org.apache.cayenne.commitlog.CommitLogListener;
import org.apache.cayenne.commitlog.CommitLogModule;
import org.apache.cayenne.commitlog.CommitLogModuleExtender;
import org.apache.cayenne.di.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarFile;

public class AngelModule extends ConfigModule {

    private static final String ROOT_URL_PATTERN = "/*";
    private static final Logger logger = LogManager.getLogger();

    private String angelVersion;

    // locks
    private static final Map<Class<?>, Object> locks = Maps.asMap(new Class<?>[]{CayenneService.class, JarFile.class},
            new Object[]{new Object(), new Object()});

    // api filter
    private static final TypeLiteral<MappedFilter<ApiFilter>> API_FILTER =
            new TypeLiteral<>() {
            };

    // active college filter
    private static final TypeLiteral<MappedFilter<ActiveCollegeFilter>> ACTIVE_COLLEGE_FILTER =
            new TypeLiteral<>() {
            };

    // the simple insecure servlet
    private static final TypeLiteral<MappedServlet<HealthCheckServlet>> HEALTHCHECK_SERVLET =
            new TypeLiteral<>() {
            };

    public static final String ANGEL_VERSION = "angelVersion";


    @Singleton
    @Provides
    ScriptTriggeringCommitListener provideScriptTriggeringCommitListener(Provider<GroovyScriptService> scriptServiceProvider) {
        return new ScriptTriggeringCommitListener(scriptServiceProvider.get());
    }

    @Singleton
    @Provides
    AuditCommitListener provideAuditCommitListener(AuditService auditService, ISystemUserService systemUserService) {
        return new AuditCommitListener(auditService, systemUserService);
    }

    @Singleton
    @Provides
    ClassPublishListener provideClassPublishListener(EventService eventService) {
        return new ClassPublishListener(eventService);
    }

    @Singleton
    @Provides
    PayslipApprovedListener providePayslipApprovedListener(EventService eventService) {
        return new PayslipApprovedListener(eventService);
    }

    @Singleton
    @Provides
    PayslipPaidListener providePayslipPaidListener(EventService eventService) {
        return new PayslipPaidListener(eventService);
    }

    @Singleton
    @Provides
    EventService provideEventService() {
        return new EventService();
    }

    @Singleton
    @Provides
    CommitLogModuleExt provideCommitLogModuleExt(ClassPublishListener classPublishListener,
                                                 PayslipApprovedListener payslipApprovedListener,
                                                 PayslipPaidListener paidListener,
                                                 ScriptTriggeringCommitListener scriptTriggeringCommitListener,
                                                 AuditCommitListener auditCommitListener) {
        return new CommitLogModuleExt(classPublishListener, payslipApprovedListener, paidListener, scriptTriggeringCommitListener, auditCommitListener);
    }

    @Override
    public void configure(Binder binder) {

        //decorate --server (run jetty server) command with --angel: run liquibase and etc. before run jetty
        BQCoreModule.extend(binder)
                .addCommand(AngelCommand.class)
                .addCommand(DataPopulationCommand.class)
                .addCommand(SanityCheckCommand.class)
                .decorateCommand(ServerCommand.class,
                        CommandDecorator.builder()
                                .beforeRun(AngelCommand.class)
                                .alsoRun(DataPopulationCommand.class)
                                .alsoRun(SanityCheckCommand.class)
                                .build());

        CayenneModule.extend(binder)
                .addModule(AngelCayenneModule.class)
                .addModule(CommitLogModuleExt.class);


        AngelJettyModule.extend(binder)
                .addMappedFilter(ACTIVE_COLLEGE_FILTER)
                .addMappedFilter(API_FILTER)
                .addMappedServlet(HEALTHCHECK_SERVLET)
                .addServlet(new ResourceServlet(),"resources", ROOT_URL_PATTERN);

        binder.bind(ISessionManager.class).to(SessionManager.class).in(Scopes.SINGLETON);
        binder.bind(CertificateUpdateWatcher.class).in(Scopes.SINGLETON);
        binder.bind(ICayenneService.class).to(CayenneService.class).in(Scopes.SINGLETON);
        binder.bind(PreferenceController.class);
        binder.bind(PluginsPrefsService.class);
        binder.bind(UserPreferenceService.class);
        binder.bind(String.class).annotatedWith(Names.named(ANGEL_VERSION)).toInstance(getVersion());
        binder.bind(EmailService.class).in(Scopes.SINGLETON);
        binder.bind(PluginService.class).in(Scopes.SINGLETON);
        PluginService.configurePlugin(binder);
    }

    @Singleton
    @Provides
    MappedFilter<ApiFilter> createApiFilter(Injector injector) {
        final Set<String> paths = new HashSet<>();
        paths.add("/a/*");

        return new MappedFilter<>(
                new ApiFilter(
                        injector.getInstance(AuditService.class),
                        injector.getInstance(ICayenneService.class),
                        injector.getInstance(IPermissionService.class)
                ),

                paths,
                ApiFilter.class.getSimpleName(),
                0);
    }

    @Singleton
    @Provides
    MappedFilter<ActiveCollegeFilter> createActiveCollegeFilter(Injector injector) {
        final Set<String> paths = new HashSet<>();
        paths.add("/*");

        return new MappedFilter<>(
                new ActiveCollegeFilter(injector.getInstance(PreferenceController.class)),
                paths,
                ActiveCollegeFilter.class.getSimpleName(),
                -1);
    }

    @Singleton
    @Provides
    MappedServlet<HealthCheckServlet> createHealthCheckServlet() {
        return new MappedServlet<>(new HealthCheckServlet(), Collections.singleton(HealthCheckServlet.PATH), HealthCheckServlet.class.getSimpleName());
    }

    @Singleton
    @Provides
    AngelServerFactory createAngelServerFactory(ConfigurationFactory configFactory) {
        return configFactory.config(AngelServerFactory.class, configPrefix);
    }

    @Singleton
    @Provides
    Scheduler createScheduler(Injector injector) {
        // disable check for quartz updates on startup
        System.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        // reduce default thread priority for jobs from 5 to 3
        System.setProperty("org.quartz.threadPool.threadPriority", "3");
        System.setProperty("org.quartz.jobStore.misfireThreshold", "14400000");

        // provide connection via Cayenne datasource
        DBConnectionManager.getInstance().addConnectionProvider("quarz-cayenne-ds", new CayenneConnectionProvider(injector));
        // configure DB job store suitable for the cluster
        System.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        System.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        System.setProperty("org.quartz.jobStore.isClustered", "true");
        System.setProperty("org.quartz.jobStore.dataSource", "quarz-cayenne-ds");
        System.setProperty("org.quartz.jobStore.dontSetAutoCommitFalse", "false");
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = sf.getScheduler();
            //the code initializes last replication time value to avoid Exception on getting
            scheduler.setJobFactory(new AngelJobFactory(injector));
        } catch (SchedulerException e) {
            logger.catching(e);
        }

        return scheduler;
    }

    /**
     * Get the SVN build number
     *
     * @return the build number or NULL if invalid
     */
    protected String getVersion() {
        if (this.angelVersion == null) {
            synchronized (locks.get(JarFile.class)) {

                this.angelVersion = ResourcesUtil.getReleaseVersionString();

            }
        }
        return this.angelVersion;
    }

    static class CommitLogModuleExt implements Module {


        private CommitLogListener[] listeners;

        CommitLogModuleExt(CommitLogListener... listeners) {
            this.listeners = listeners;
        }

        @Override
        public void configure(org.apache.cayenne.di.Binder binder) {
            var ref = new Object() {
                CommitLogModuleExtender extender = CommitLogModule.extend();
            };

            Arrays.asList(listeners).forEach(listener ->
                    ref.extender = ref.extender.addListener(listener)
            );

            ref.extender.module().configure(binder);
        }
    }

    private static class CayenneConnectionProvider implements ConnectionProvider {
        private final Injector injector;

        public CayenneConnectionProvider(Injector injector) {
            this.injector = injector;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return injector.getInstance(ICayenneService.class).getDataSource().getConnection();
        }

        @Override
        public void shutdown() throws SQLException {
            // noop
        }

        @Override
        public void initialize() throws SQLException {
            // noop
        }
    }
}
