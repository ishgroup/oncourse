package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.WebBlockTag;
import ish.oncourse.model.WebNodeTag;

/**
 * Class _Tag was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Tag extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String IS_TAG_GROUP_PROPERTY = "isTagGroup";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String NODE_TYPE_PROPERTY = "nodeType";
    public static final String SHORT_NAME_PROPERTY = "shortName";
    public static final String WEIGHTING_PROPERTY = "weighting";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String PARENT_PROPERTY = "parent";
    public static final String TAG_GROUP_REQUIREMENTS_PROPERTY = "tagGroupRequirements";
    public static final String TAGGABLE_TAGS_PROPERTY = "taggableTags";
    public static final String TAGS_PROPERTY = "tags";
    public static final String WEB_BLOCK_TAGS_PROPERTY = "webBlockTags";
    public static final String WEB_NODE_TAGS_PROPERTY = "webNodeTags";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDetail(String detail) {
        writeProperty("detail", detail);
    }
    public String getDetail() {
        return (String)readProperty("detail");
    }

    public void setDetailTextile(String detailTextile) {
        writeProperty("detailTextile", detailTextile);
    }
    public String getDetailTextile() {
        return (String)readProperty("detailTextile");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
    }

    public void setIsTagGroup(Boolean isTagGroup) {
        writeProperty("isTagGroup", isTagGroup);
    }
    public Boolean getIsTagGroup() {
        return (Boolean)readProperty("isTagGroup");
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty("isWebVisible", isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty("isWebVisible");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setNodeType(Integer nodeType) {
        writeProperty("nodeType", nodeType);
    }
    public Integer getNodeType() {
        return (Integer)readProperty("nodeType");
    }

    public void setShortName(String shortName) {
        writeProperty("shortName", shortName);
    }
    public String getShortName() {
        return (String)readProperty("shortName");
    }

    public void setWeighting(Integer weighting) {
        writeProperty("weighting", weighting);
    }
    public Integer getWeighting() {
        return (Integer)readProperty("weighting");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setParent(Tag parent) {
        setToOneTarget("parent", parent, true);
    }

    public Tag getParent() {
        return (Tag)readProperty("parent");
    }


    public void addToTagGroupRequirements(TagGroupRequirement obj) {
        addToManyTarget("tagGroupRequirements", obj, true);
    }
    public void removeFromTagGroupRequirements(TagGroupRequirement obj) {
        removeToManyTarget("tagGroupRequirements", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TagGroupRequirement> getTagGroupRequirements() {
        return (List<TagGroupRequirement>)readProperty("tagGroupRequirements");
    }


    public void addToTaggableTags(TaggableTag obj) {
        addToManyTarget("taggableTags", obj, true);
    }
    public void removeFromTaggableTags(TaggableTag obj) {
        removeToManyTarget("taggableTags", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TaggableTag> getTaggableTags() {
        return (List<TaggableTag>)readProperty("taggableTags");
    }


    public void addToTags(Tag obj) {
        addToManyTarget("tags", obj, true);
    }
    public void removeFromTags(Tag obj) {
        removeToManyTarget("tags", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Tag> getTags() {
        return (List<Tag>)readProperty("tags");
    }


    public void addToWebBlockTags(WebBlockTag obj) {
        addToManyTarget("webBlockTags", obj, true);
    }
    public void removeFromWebBlockTags(WebBlockTag obj) {
        removeToManyTarget("webBlockTags", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebBlockTag> getWebBlockTags() {
        return (List<WebBlockTag>)readProperty("webBlockTags");
    }


    public void addToWebNodeTags(WebNodeTag obj) {
        addToManyTarget("webNodeTags", obj, true);
    }
    public void removeFromWebNodeTags(WebNodeTag obj) {
        removeToManyTarget("webNodeTags", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebNodeTag> getWebNodeTags() {
        return (List<WebNodeTag>)readProperty("webNodeTags");
    }


}
