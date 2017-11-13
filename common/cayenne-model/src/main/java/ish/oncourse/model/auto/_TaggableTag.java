package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;

/**
 * Class _TaggableTag was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _TaggableTag extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String TAG_PROPERTY = "tag";
    public static final String TAGGABLE_PROPERTY = "taggable";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Tag> TAG = Property.create("tag", Tag.class);
    public static final Property<Taggable> TAGGABLE = Property.create("taggable", Taggable.class);

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

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setTag(Tag tag) {
        setToOneTarget("tag", tag, true);
    }

    public Tag getTag() {
        return (Tag)readProperty("tag");
    }


    public void setTaggable(Taggable taggable) {
        setToOneTarget("taggable", taggable, true);
    }

    public Taggable getTaggable() {
        return (Taggable)readProperty("taggable");
    }


}
