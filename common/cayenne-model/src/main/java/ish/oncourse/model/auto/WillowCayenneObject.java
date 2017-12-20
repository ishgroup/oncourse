package ish.oncourse.model.auto;

import org.apache.cayenne.CayenneDataObject;

public abstract class WillowCayenneObject extends CayenneDataObject {

    @Override
    public boolean equals(Object object) {
        return this == object || (object instanceof WillowCayenneObject && this.getObjectId().equals(((WillowCayenneObject) object).objectId));
    }

    @Override
    public int hashCode() { return this.getObjectId() != null ? this.getObjectId().hashCode(): super.hashCode(); }
}
