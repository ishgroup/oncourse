package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.College;

/**
 * Class _BinaryInfo was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _BinaryInfo extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String BYTE_SIZE_PROPERTY = "byteSize";
    public static final String CREATED_PROPERTY = "created";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String MIME_TYPE_PROPERTY = "mimeType";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String PIXEL_HEIGHT_PROPERTY = "pixelHeight";
    public static final String PIXEL_WIDTH_PROPERTY = "pixelWidth";
    public static final String REFERENCE_NUMBER_PROPERTY = "referenceNumber";
    public static final String BINARY_INFO_RELATIONS_PROPERTY = "binaryInfoRelations";
    public static final String COLLEGE_PROPERTY = "college";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setByteSize(Long byteSize) {
        writeProperty("byteSize", byteSize);
    }
    public Long getByteSize() {
        return (Long)readProperty("byteSize");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty("isWebVisible", isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty("isWebVisible");
    }

    public void setMimeType(String mimeType) {
        writeProperty("mimeType", mimeType);
    }
    public String getMimeType() {
        return (String)readProperty("mimeType");
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

    public void setPixelHeight(Integer pixelHeight) {
        writeProperty("pixelHeight", pixelHeight);
    }
    public Integer getPixelHeight() {
        return (Integer)readProperty("pixelHeight");
    }

    public void setPixelWidth(Integer pixelWidth) {
        writeProperty("pixelWidth", pixelWidth);
    }
    public Integer getPixelWidth() {
        return (Integer)readProperty("pixelWidth");
    }

    public void setReferenceNumber(Integer referenceNumber) {
        writeProperty("referenceNumber", referenceNumber);
    }
    public Integer getReferenceNumber() {
        return (Integer)readProperty("referenceNumber");
    }

    public void addToBinaryInfoRelations(BinaryInfoRelation obj) {
        addToManyTarget("binaryInfoRelations", obj, true);
    }
    public void removeFromBinaryInfoRelations(BinaryInfoRelation obj) {
        removeToManyTarget("binaryInfoRelations", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<BinaryInfoRelation> getBinaryInfoRelations() {
        return (List<BinaryInfoRelation>)readProperty("binaryInfoRelations");
    }


    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


}
