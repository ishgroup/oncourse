package ish.oncourse.services.content

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentComparator
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

class BlocksForRegionKey {
    
    private WebNodeType webNodeType
    private RegionKey regionKey
    private WebSiteVersion version

    private BlocksForRegionKey(){}
    
    static BlocksForRegionKey valueOf(WebNodeType webNodeType, RegionKey regionKey, WebSiteVersion version) {
        BlocksForRegionKey extractor = new BlocksForRegionKey()
        extractor.webNodeType = webNodeType
        extractor.regionKey = regionKey
        extractor.version = version
        extractor
    }

    SortedSet<WebContent> get() {
        assert webNodeType != null

        if (webNodeType.objectId.temporary && RegionKey.unassigned != regionKey) {
            throw new IllegalArgumentException('Illegal params for WebContentService#getBlocksForRegionKey() call')
        }

        ObjectSelect<WebContent> query = ((ObjectSelect.query(WebContent) 
                & WebContent.WEB_SITE_VERSION.eq(version))
                & WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.WEB_NODE).isNull())


        List<WebContentVisibility> webContentVisibilities = webNodeType.webContentVisibilities

        List<Long> nodeIds = new ArrayList<>(webContentVisibilities.size())
        List<Long> visibilityIds = new ArrayList<>(webContentVisibilities.size())

        webContentVisibilities.each {
            visibilityIds << it.id
            nodeIds << it.id
        }

        Expression regionKeyQualifier
        if (regionKey && regionKey != RegionKey.unassigned) {
            regionKeyQualifier = ExpressionFactory.inDbExp(WebContent.WEB_CONTENT_VISIBILITIES.name + "+."
                            + WebContentVisibility.ID_PK_COLUMN, visibilityIds)
                    .andExp(WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.REGION_KEY).eq(regionKey))
                    
        } else {
            regionKeyQualifier = ExpressionFactory.notInDbExp(
                    WebContent.ID_PK_COLUMN, nodeIds)
                    .orExp(ExpressionFactory
                    .matchDbExp(WebContent.WEB_CONTENT_VISIBILITIES.name
                    + "+." + WebContentVisibility.ID_PK_COLUMN, null))
        }

        query = query & regionKeyQualifier
        TreeSet<WebContent> treeSet = new TreeSet<>(new WebContentComparator(webNodeType))
        treeSet.addAll(query.select(webNodeType.objectContext))
        return treeSet
    }
}
