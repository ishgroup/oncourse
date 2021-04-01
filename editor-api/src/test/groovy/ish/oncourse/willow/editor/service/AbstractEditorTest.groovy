package ish.oncourse.willow.editor.service

import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.model.*
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request
import org.junit.After
import org.junit.Before

abstract class AbstractEditorTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService  cayenneService
    protected RequestService requestService
    protected String webSiteKey = 'mammoth'

    private TestContext testContext
    
    @Before
    void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(true).open()
        new LoadDataSet().dataSetFile(dataSetResource).replacements(['[null]':null]).load(testContext.DS)
        cayenneRuntime = ServerRuntime.builder()
                .addConfig('cayenne-oncourse.xml')
                .addModule(new WillowCayenneModuleBuilder().build())
                .build()
        cayenneService = new CayenneService(cayenneRuntime)

        Request request = mockRequest()
        requestService = [getRequest: request, getResponse: null] as RequestService
        CreateDefaultWebSiteStructure.valueOf(webSiteKey, cayenneService.newContext()).create()
    }
    
    private Request mockRequest() {
        Request rq = new Request(null, null) {
            String getServerName() {
                return "${webSiteKey}.oncourse.cc"
            }

            @Override
            String getHeader(String name) {
                if ("X-Site-Key".equals(name)) {
                    return webSiteKey
                }
                return super.getHeader(name)
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
            stagedVersion.siteVersion = 1L
            
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

            WebNode node = WebNodeFunctions.createNewNodeBy(stagedVersion, DEFAULT_HOME_PAGE_NAME, DEFAULT_HOME_PAGE_NAME, 1)
            node.setPublished(true)

            WebContent webContent = context.newObject(WebContent)
            webContent.name = 'New block (1)'
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
