package ish.oncourse.willow.editor.service

import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cayenne.WillowApiCayenneModule
import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebMenu
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

abstract class AbstractEditorTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService  cayenneService
    protected RequestService requestService
    protected String webSiteKey = 'mammoth'

    private TestContext testContext
    
    @Before
    void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(false).open()
        new LoadDataSet().dataSetFile(dataSetResource).replacements(['[null]':null]).load(testContext.DS)
        cayenneRuntime = new ServerRuntime('cayenne-oncourse.xml', new WillowApiCayenneModule())
        cayenneService = new CayenneService(cayenneRuntime)

        Request request = mockRequest()
        requestService = [getRequest: request, getResponse: null] as RequestService
        CreateDefaultWebSiteStructure.valueOf(webSiteKey, cayenneService.newContext()).create()
    }
    
    private Request mockRequest() {
        new Request(null, null) {
            String getServerName() {
                return "${webSiteKey}.oncourse.cc"
            }
        }
    }

    @After
    void after() {
        cayenneRuntime.shutdown()
        testContext.close()
    }

    protected String getDataSetResource() {
        return 'ish/oncourse/willow/editor/service/EditorApiDataSet.xml'
    }


    static class CreateDefaultWebSiteStructure {
        final static String DEFAULT_HOME_PAGE_NAME = 'Home page'
        
        private String siteKey
        private ObjectContext context

        void create() {
            
            WebSite webSite = ObjectSelect.query(WebSite).where(WebSite.SITE_KEY.eq(siteKey)).selectFirst(context)

            WebSiteVersion stagedVersion = context.newObject(WebSiteVersion)
            stagedVersion.setWebSite(webSite)
            
            Date now = new Date()
            WebSiteLayout webSiteLayout = context.newObject(WebSiteLayout)
            webSiteLayout.setLayoutKey(WebNodeType.DEFAULT_LAYOUT_KEY)
            webSiteLayout.setWebSiteVersion(stagedVersion)

            WebNodeType page = context.newObject(WebNodeType)
            page.setName(WebNodeType.PAGE)
            page.setCreated(now)
            page.setModified(now)
            page.setWebSiteLayout(webSiteLayout)
            page.setWebSiteVersion(stagedVersion)

            WebNode node = WebNodeFunctions.createNewNodeBy(stagedVersion, page, DEFAULT_HOME_PAGE_NAME, DEFAULT_HOME_PAGE_NAME, 1)
            node.setPublished(true)

            WebContent webContent = context.newObject(WebContent)
            webContent.name = 'block'
            webContent.webSiteVersion = stagedVersion
            webContent.content = 'Content'
            webContent.contentTextile = 'Content'


            WebContentVisibility webContentVisibility = context.newObject(WebContentVisibility)
            webContentVisibility.webNodeType = page
            webContentVisibility.webContent = webContent
            webContentVisibility.regionKey = RegionKey.footer
            webContentVisibility.weight = 1

            WebMenu menu = context.newObject(WebMenu)
            menu.setName('Home')
            menu.setCreated(now)
            menu.setModified(now)
            menu.setWebSiteVersion(stagedVersion)
            menu.setWeight(1)
            menu.setWebNode(node)

            context.commitChanges()

            WebUrlAlias urlAlias = context.newObject(WebUrlAlias)
            urlAlias.setWebSiteVersion(stagedVersion)
            urlAlias.setUrlPath('/')
            urlAlias.setWebNode(node)
            urlAlias.setDefault(true)
            context.commitChanges()
        }
        
        private CreateDefaultWebSiteStructure(){}

        static CreateDefaultWebSiteStructure valueOf(String siteKey, ObjectContext context) {
            CreateDefaultWebSiteStructure result = new CreateDefaultWebSiteStructure()
            result.context = context
            result.siteKey = siteKey
            return result
        }

    }
}
