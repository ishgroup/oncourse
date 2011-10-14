package ish.oncourse.ui.components;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Attachments {

	@Inject
	private IBinaryDataService binaryDataService;

	@Parameter
	private String entityIdentifier;

	@Parameter
	private Long entityIdNum;

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
		List<BinaryInfo> allAttachedFiles = binaryDataService.getAttachedFiles(entityIdNum, entityIdentifier, true);
		Expression imageQualifier = BinaryInfo.getImageQualifier();
		attachments = imageQualifier.notExp().filterObjects(allAttachedFiles);
		ArrayList<Long> ids = (ArrayList<Long>) request.getAttribute(BinaryInfo.DISPLAYED_IMAGES_IDS);
		if (ids != null && !ids.isEmpty()) {
			imageQualifier = imageQualifier.andExp(ExpressionFactory.notInDbExp(BinaryInfo.ID_PK_COLUMN, ids));
		}
		attachedImages = imageQualifier.filterObjects(allAttachedFiles);

		return !allAttachedFiles.isEmpty();
	}

}
