package ish.oncourse.server

import com.google.inject.Injector
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.replication.handler.OutboundReplicationHandler
import static java.lang.String.format
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.SchedulerFactory

//import ish.oncourse.server.upgrades.liquibase.LiquibaseJavaContext

class TestAngelServer {
    Object appConfig
    Injector injector
    SchedulerFactory sf
    ImportService importService
    ICayenneService cayenneService

    static void main(String[] args) {
        try {
            TestAngelServer main = new TestAngelServer()
            main.init(args)
            main.runOutboundReplication()
        } finally {
            System.exit(0)
        }
    }

    private void runOutboundReplication() {
        OutboundReplicationHandler handler = injector.getInstance(OutboundReplicationHandler)
        handler.replicate()
    }

    void init(String[] args) {
        //TODO Refactoring to use bootique
//        injector = Guice.createInjector(
//                new DatabaseModule(
//                        new URI(configuration.getDatabaseUri()),
//                        "development",
//                        configuration.getReplicationDisabled(),
//                        true,
//                        configuration.getUseSingleQueryCacheInstance(),
//                        configuration.getQueryCacheSize()))
//
//
//        appConfig = Yaml.newInstance().load(TestAngelServer.class.classLoader.getResourceAsStream("TestAngelServer.yml"))
//        if (appConfig.db.create) {
//            new CreateMysqlSchema(configuration: configuration, appConfig: appConfig).create()
//            DatabaseService databaseService = injector.getInstance(DatabaseService)
//            databaseService.start()
//        }
//
//
//        sf = new StdSchedulerFactory()
//        injector = injector.createChildInjector(new ProductionModule(configuration, sf.getScheduler()))
//        if (appConfig.db.update) {
//            new UpdateDBFunction(injector: injector).update()
//        }
//        importService = injector.getInstance(ImportService)
//        cayenneService = injector.getInstance(ICayenneService)
    }


    static class UpdateDBFunction {
        static final Logger logger = LogManager.getLogger(UpdateDBFunction)

        Injector injector

        void update() {
            //TODO Refactoring
//            DatabaseService dbService = this.injector.getInstance(DatabaseService);
//            LiquibaseJavaContext.fill(this.injector);
//            try {
//                SchemaUpdateService schemaUpdateService = this.injector.getInstance(SchemaUpdateService.class);
//                schemaUpdateService.applySchemaUpdates(dbService.getDBAdapter().createConnection());
//                logger.debug("Database schema up to date...");
//            } catch (DatabaseException e) {
//                String userMessage = e.getMessage();
//                if (userMessage.indexOf("/*") < userMessage.indexOf("*/")) {
//                    userMessage = userMessage.substring(userMessage.indexOf("/*") + 2, userMessage.indexOf("*/"));
//                }
//                logger.error("Database upgrade failed", e);
//                UserInteractionUtil.alertDialog("Database upgrade failed.\n\n" + userMessage);
//
//                throw e;
//            }
        }
    }

    static class CreateMysqlSchema {
        Object appConfig

        void create() {
//            URI uri = URI.create(configuration.databaseUri.substring(5))
//            String dbname = uri.path.substring(1)
//            Sql sql = Sql.newInstance(
//                    format((String) appConfig.db.local.url, 'mysql'),
//                    (String) appConfig.db.local.user,
//                    (String) appConfig.db.local.password,
//                    (String) appConfig.db.local.driver)
//            GroovyRowResult row = sql.rows("show schemas").find { it.Database == dbname }
//            if (!row) {
//                sql.execute(format("CREATE SCHEMA `%s` DEFAULT CHARACTER SET utf8", dbname))
//
//            }
        }
    }
}
