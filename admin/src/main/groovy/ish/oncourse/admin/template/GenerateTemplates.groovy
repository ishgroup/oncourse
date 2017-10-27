package ish.oncourse.admin.template

import ish.math.MoneyType
import ish.oncourse.admin.mock.InitialContextFactoryMock
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.dbcp.BasicDataSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.naming.Context
import javax.naming.InitialContext
import javax.sql.DataSource


class GenerateTemplates {

    static Logger logger = LogManager.getLogger()

    public static void main(String[] args) {
        GenerateTemplates generateTemplates = new GenerateTemplates()
        generateTemplates.initDataSource()

        ServerRuntime cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml")

        for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
            dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType())
        }


        ObjectContext objectContext = cayenneRuntime.newContext()

        List<WebSite> webSites =ObjectSelect.query(WebSite).select(objectContext)

        def webSitesCountMsg = "${webSites.size()} found to process".toString()
        logger.debug(webSitesCountMsg)
        println webSitesCountMsg
        
        webSites.each { WebSite webSite ->
            try {
                new CreateMissingTemplates(context: objectContext, siteId: webSite.id).run()
            } catch (IOException e) {
                def errorMsg = "Error during generating templates for college '${webSite.college.name}' and webSite '${webSite.name}', siteId=${webSite.id}".toString()
                logger.error(errorMsg, e)
                println errorMsg
            }
        }
    }
    


    private void initDataSource() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName())
        
        InitialContextFactoryMock.bind("java:comp/env", new InitialContext())

        DataSource oncourse = createDataSource()
        InitialContextFactoryMock.bind("jdbc/oncourse", oncourse)
        InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse)
    }

    
    //it should be configured before run: database Url, user, password, driver;  
    private DataSource createDataSource() {
        BasicDataSource dataSource = new BasicDataSource()
        dataSource.setDriverClassName("com.mysql.jdbc.Driver")
        //dataSource.setDriverClassName("org.gjt.mm.mysql.Driver")
        dataSource.setUrl("jdbc:mariadb://127.0.0.1:3306/database?useSSL=false&serverTimezone=Australia/Sydney")
        dataSource.setUsername("username")
        dataSource.setPassword("password")
        dataSource.setMaxActive(1)
        return dataSource
    }
}
