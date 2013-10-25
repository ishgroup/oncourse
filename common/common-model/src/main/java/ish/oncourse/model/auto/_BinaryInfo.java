package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.AttachmentInfoVisibility;
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
    public static final String FILE_PATH_PROPERTY = "filePath";
    public static final String FILE_UUID_PROPERTY = "fileUUID";
    public static final String MIME_TYPE_PROPERTY = "mimeType";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String PIXEL_HEIGHT_PROPERTY = "pixelHeight";
    public static final String PIXEL_WIDTH_PROPERTY = "pixelWidth";
    public static final String REFERENCE_NUMBER_PROPERTY = "referenceNumber";
    public static final String WEB_VISIBLE_PROPERTY = "webVisible";
    public static final String BINARY_INFO_RELATIONS_PROPERTY = "binaryInfoRelations";
    public static final String COLLEGE_PROPERTY = "college";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setByteSize(Long byteSize) {
        writeProperty(BYTE_SIZE_PROPERTY, byteSize);
    }
    public Long getByteSize() {
        return (Long)readProperty(BYTE_SIZE_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setFilePath(String filePath) {
        writeProperty(FILE_PATH_PROPERTY, filePath);
    }
    public String getFilePath() {
        return (String)readProperty(FILE_PATH_PROPERTY);
    }

    public void setFileUUID(String fileUUID) {
        writeProperty(FILE_UUID_PROPERTY, fileUUID);
    }
    public String getFileUUID() {
        return (String)readProperty(FILE_UUID_PROPERTY);
    }

    public void setMimeType(String mimeType) {
        writeProperty(MIME_TYPE_PROPERTY, mimeType);
    }
    public String getMimeType() {
        return (String)readProperty(MIME_TYPE_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void setPixelHeight(Integer pixelHeight) {
        writeProperty(PIXEL_HEIGHT_PROPERTY, pixelHeight);
    }
    public Integer getPixelHeight() {
        return (Integer)readProperty(PIXEL_HEIGHT_PROPERTY);
    }

    public void setPixelWidth(Integer pixelWidth) {
        writeProperty(PIXEL_WIDTH_PROPERTY, pixelWidth);
    }
    public Integer getPixelWidth() {
        return (Integer)readProperty(PIXEL_WIDTH_PROPERTY);
    }

    public void setReferenceNumber(Integer referenceNumber) {
        writeProperty(REFERENCE_NUMBER_PROPERTY, referenceNumber);
    }
    public Integer getReferenceNumber() {
        return (Integer)readProperty(REFERENCE_NUMBER_PROPERTY);
    }

    public void setWebVisible(AttachmentInfoVisibility webVisible) {
        writeProperty(WEB_VISIBLE_PROPERTY, webVisible);
    }
    public AttachmentInfoVisibility getWebVisible() {
        return (AttachmentInfoVisibility)readProperty(WEB_VISIBLE_PROPERTY);
    }

    public void addToBinaryInfoRelations(BinaryInfoRelation obj) {
        addToManyTarget(BINARY_INFO_RELATIONS_PROPERTY, obj, true);
    }
    public void removeFromBinaryInfoRelations(BinaryInfoRelation obj) {
        removeToManyTarget(BINARY_INFO_RELATIONS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<BinaryInfoRelation> getBinaryInfoRelations() {
        return (List<BinaryInfoRelation>)readProperty(BINARY_INFO_RELATIONS_PROPERTY);
    }


    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


}
