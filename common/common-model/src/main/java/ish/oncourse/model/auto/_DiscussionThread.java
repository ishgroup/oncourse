package ish.oncourse.model.auto;

import org.apache.cayenne.CayenneDataObject;

/**
 * Class _DiscussionThread was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DiscussionThread extends CayenneDataObject {

    public static final String TOPIC_PROPERTY = "topic";

    public static final String ID_PK_COLUMN = "id";

    public void setTopic(String topic) {
        writeProperty(TOPIC_PROPERTY, topic);
    }
    public String getTopic() {
        return (String)readProperty(TOPIC_PROPERTY);
    }

}
