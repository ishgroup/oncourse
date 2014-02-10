package ish.oncourse.ui.components;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

public class Attachments {

    @Inject
    private IBinaryDataService binaryDataService;

    /**
     * We can use this parameter to render attacments for more than one entity.
     * For example when we render attacments for romm we need to render attaments for site
     */
    @Parameter
    private String[] entityIdentifiers;

    @Parameter
    private String[] entityIdNums;

    @Parameter
    private String entityIdentifier;

    @Parameter
    private String entityIdNum;

    @Property
    private List<BinaryInfo> attachedImages;

    @Property
    private BinaryInfo image;

    @Property
    private List<BinaryInfo> attachments;

    @Property
    private BinaryInfo attachment;

    @Inject
    private Request request;

    @SetupRender
    boolean beforeRender() {
        List<BinaryInfo> allAttachedFiles = findAllBinaryInfos();
        Expression imageQualifier = BinaryInfo.getImageQualifier();
        attachments = imageQualifier.notExp().filterObjects(allAttachedFiles);
        ArrayList<Long> ids = (ArrayList<Long>) request.getAttribute(BinaryInfo.DISPLAYED_IMAGES_IDS);
        if (ids != null && !ids.isEmpty()) {
            imageQualifier = imageQualifier.andExp(ExpressionFactory.notInDbExp(BinaryInfo.ID_PK_COLUMN, ids));
        }
        attachedImages = imageQualifier.filterObjects(allAttachedFiles);

        return !allAttachedFiles.isEmpty();
    }

    private List<BinaryInfo> findAllBinaryInfos() {

        if (entityIdentifiers == null)
        {
            entityIdentifiers = new String[]{entityIdentifier};
            entityIdNums = new String[]{entityIdNum};
        }

        ArrayList<BinaryInfo> result = new ArrayList<>();
        for (int i = 0; i < entityIdentifiers.length; i++) {
            String identifier = entityIdentifiers[i];
            Long id = Long.valueOf(entityIdNums[i]);
            result.addAll(binaryDataService.getAttachedFiles(id, identifier, true));
        }
        return result;
    }


    public boolean isHasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }
	
	public String getAttachmentUrl() {
		return binaryDataService.getUrl(attachment);
	}
	
	public String getImageUrl() {
		return binaryDataService.getUrl(image);
	}

}
