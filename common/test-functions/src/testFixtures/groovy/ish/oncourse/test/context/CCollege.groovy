package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.oncourse.model.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class CCollege {
    ObjectContext objectContext
    College college
    Faker faker = DataContext.faker

    List<CProduct> products = new LinkedList<>()
    List<CCourse> courses = new LinkedList<>()
    List<Tag> tags = new LinkedList<>()
    List<CSite> sites = new LinkedList<>()

    List<CWebSite> webSites = new LinkedList<>()


    Closure<Tag> tagByName = { name ->
        college.tags.stream().filter({
            it.name.equalsIgnoreCase(name)
        }).findFirst().get()
    }

    @Deprecated
    Tag tag(String name, boolean webVisible = true) {
        return newTag(name, webVisible)
    }

    Tag newTag(String name, boolean webVisible = true) {
        Tag tag = objectContext.newObject(Tag)
        tag.name = name
        tag.shortName = name
        tag.isWebVisible = webVisible
        tag.isTagGroup = false
        tag.college = objectContext.localObject(college)
        objectContext.commitChanges()
        tags.add(tag)
        tag
    }

    void addTag(String parent, String... child) {
        child.each {
            addTag(tagByName(parent),
                    tagByName(it))
        }
    }


    void addTag(Tag parent, Tag... child) {
        parent.isTagGroup = true
        child.each {
            it.parent = parent
            objectContext.commitChanges()
        }
    }

    CCourse newCourse(String name) {
        newCourse(name, name.toUpperCase())
    }

    CCollege timeZone(String timeZone) {
        college.timeZone = timeZone
        this
    }

    CCourse newCourse(String name, String code) {
        CCourse course = CCourse.instance(objectContext, college, name, code)
        courses.add(course)
        objectContext.commitChanges()
        course
    }

    void tagCourse(String courseCode, String tagName) {
        tagCourse(courses.find { it.course.code == courseCode }.course, tags.find { it.name == tagName })
    }

    void tagCourse(Course course, Tag tag) {
        Taggable taggable = objectContext.newObject(Taggable)
        taggable.college = course.college
        taggable.entityWillowId = course.id
        taggable.entityIdentifier = Course.class.simpleName

        TaggableTag taggableTag = objectContext.newObject(TaggableTag)
        taggableTag.college = course.college
        taggableTag.taggable = taggable
        taggableTag.tag = objectContext.localObject(tag)
        objectContext.commitChanges()
    }

    CProduct newProduct(String name) {
        ArticleProduct product = objectContext.newObject(ArticleProduct)
        product.college = this.college
        product.name = name
        product.created = new Date()
        product.modified = new Date()
        product.description = faker.commerce().productName()
        product.isWebVisible = true
        product.sku = faker.commerce().promotionCode()
        product.notes = faker.commerce().material()
        product.isOnSale = true
        objectContext.commitChanges()
        CProduct result = new CProduct().with {
            it.product = product
            it
        }
        return result
    }


    CWebSite newWebSite() {
        newWebSite(null)
    }

    CWebSite newWebSite(String siteKey) {
        WebSite webSite = objectContext.newObject(WebSite)
        webSite.college = this.college
        webSite.name = faker.company().name()
        webSite.siteKey = siteKey ? siteKey : webSite.name.substring(0, 3).toLowerCase()
        webSite.created = new Date()
        webSite.modified = new Date()

        WebSiteVersion webSiteVersion = objectContext.newObject(WebSiteVersion.class)
        webSiteVersion.setSiteVersion(1)
        webSiteVersion.setWebSite(webSite)

        Date now = new Date()
        WebSiteLayout webSiteLayout = objectContext.newObject(WebSiteLayout.class)
        webSiteLayout.setLayoutKey(WebNodeType.DEFAULT_LAYOUT_KEY)
        webSiteLayout.setWebSiteVersion(webSiteVersion)

        WebNodeType page = objectContext.newObject(WebNodeType.class)
        page.setName(WebNodeType.PAGE)
        page.setCreated(now)
        page.setModified(now)
        page.setWebSiteLayout(webSiteLayout)
        page.setWebSiteVersion(webSiteVersion)

        WebNode webNode = objectContext.newObject(WebNode.class)
        webNode.setName("Home")
        webNode.setWebSiteVersion(webSiteVersion)
        webNode.setNodeNumber(1)

        webNode.setWebNodeType(page)
        webNode.setPublished(true)

        WebContent webContent = objectContext.newObject(WebContent.class)
        webContent.setWebSiteVersion(webSiteVersion)
        webContent.setContentTextile("")
        webContent.setContent("")

        WebContentVisibility webContentVisibility = objectContext.newObject(WebContentVisibility.class)
        webContentVisibility.setWebNode(webNode)
        webContentVisibility.setRegionKey(RegionKey.content)
        webContentVisibility.setWebContent(webContent)

        WebMenu menu = objectContext.newObject(WebMenu.class)
        menu.setName("Home")
        menu.setCreated(now)
        menu.setModified(now)
        menu.setWebSiteVersion(webSiteVersion)
        menu.setWeight(1)
        menu.setWebNode(webNode)


        WebUrlAlias urlAlias = objectContext.newObject(WebUrlAlias.class)
        urlAlias.setWebSiteVersion(webSiteVersion)
        urlAlias.setUrlPath("/")
        urlAlias.setWebNode(webNode)
        urlAlias.setDefault(true)

        WillowUser willowUser = objectContext.newObject(WillowUser)
        willowUser.firstName = "Andrei"
        willowUser.lastName = "Koira"
        willowUser.email = "pervoliner@gmai.com"
        willowUser.password = "password"

        objectContext.commitChanges()
        return new CWebSite(webSite: webSite)
    }

    CSite newSite() {
        CSite cSite = CSite.instance(this.objectContext, this.college)
        cSite.withNewRoom(this.faker.address().buildingNumber())
        sites.add(cSite)
        return cSite
    }

    CCollege load() {
        ObjectSelect.query(Tag).where(Tag.COLLEGE.eq(college)).select(objectContext).forEach { tags.add(it) }
        ObjectSelect.query(Course).where(Course.COLLEGE.eq(college)).select(objectContext).forEach {
            courses.add(new CCourse(course: it, objectContext: objectContext).load())
        }
        ObjectSelect.query(Site).where(Course.COLLEGE.eq(college)).select(objectContext).forEach { s ->
            sites.add(new CSite().with {
                it.site = s
                it.objectContext = this.objectContext
                it
            }.load())
        }
        return this
    }
}
