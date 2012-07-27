package ish.oncourse.services.binary;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BinaryDataService implements IBinaryDataService {

	private static final Logger LOGGER = Logger.getLogger(BinaryDataService.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ApplicationStateManager applicationStateManager;

    @Inject
    private IFileStorageAssetService fileStorageAssetService;

	@Override
	public BinaryInfo getBinaryInfoById(Object id) {
		Expression qualifier = getCollegeQualifier().andExp(ExpressionFactory.matchDbExp(BinaryInfo.ID_PK_COLUMN, id));
		return (BinaryInfo) Cayenne.objectForQuery(cayenneService.sharedContext(), new SelectQuery(BinaryInfo.class,
				qualifier));
	}

	@Override
	public BinaryInfo getBinaryInfoByReferenceNumber(Object refNum) {
		if (refNum == null) {
			return null;
		}

		// if this binaryInfo was created in willow-wo, it has the
		// referenceNumber property which is unique for every college
		Expression refNumQualifier = ExpressionFactory.matchExp(BinaryInfo.REFERENCE_NUMBER_PROPERTY, refNum);

		// if this binaryInfo is the willow2 entity, the referenceNumber=null as
		// it is not generated anymore, use angelId for reference.
		refNumQualifier = refNumQualifier.orExp(ExpressionFactory.matchExp(BinaryInfo.REFERENCE_NUMBER_PROPERTY, null)
				.andExp(ExpressionFactory.matchExp(BinaryInfo.ANGEL_ID_PROPERTY, refNum)));
		Expression qualifier = getCollegeQualifier().andExp(refNumQualifier);

		return (BinaryInfo) Cayenne.objectForQuery(cayenneService.sharedContext(), new SelectQuery(BinaryInfo.class,
				qualifier));
	}

	public BinaryInfo getBinaryInfo(final String searchProperty, Object value) {
		final Expression qualifier = getCollegeQualifier().andExp(ExpressionFactory.matchExp(searchProperty, value));
		return getRandomBinaryInfo(qualifier);
	}

	public BinaryInfo getRandomImage() {
		Expression qualifier = getCollegeQualifier().andExp(BinaryInfo.getImageQualifier());
		return getRandomBinaryInfo(qualifier);
	}

	private Expression getCollegeQualifier() {
		return getCollegeQualifier(true);
	}

	private Expression getCollegeQualifier(boolean showOnlyPublic) {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(BinaryInfo.COLLEGE_PROPERTY, cayenneService.sharedContext()
				.localObject(currentCollege.getObjectId(), null));
		if (showOnlyPublic) {
			if (showStudentsAttachments()) {
				qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(BinaryInfo.WEB_VISIBLE_PROPERTY, AttachmentInfoVisibility.PRIVATE));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(BinaryInfo.WEB_VISIBLE_PROPERTY, AttachmentInfoVisibility.PUBLIC));
			}
		}
		return qualifier;
	}

	private BinaryInfo getRandomBinaryInfo(final Expression qualifier) {
		ObjectContext sharedContext = cayenneService.sharedContext();
		final SelectQuery binaryCount = new SelectQuery(BinaryInfo.class, qualifier);
		@SuppressWarnings("unchecked")
		List<BinaryInfo> binaries = (List<BinaryInfo>)sharedContext.performQuery(binaryCount);
		Long count = (long) binaries.size();
		BinaryInfo randomResult = null;
		int attempt = 0;
		if (count > 0) {
			while ((randomResult == null || !fileStorageAssetService.contains(randomResult) ) && attempt++ < 5) {
				int random = new Random().nextInt(count.intValue());

				SelectQuery query = new SelectQuery(BinaryInfo.class, qualifier);
				query.setFetchOffset(random);
				query.setFetchLimit(1);
				randomResult = (BinaryInfo) Cayenne.objectForQuery(sharedContext, query);

				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(String.format("Fetched random image: %s", randomResult));
				}
			}
		}

		return randomResult;
	}

	@Override
	public List<BinaryInfo> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean showOnlyPublic) {
		ObjectContext sharedContext = cayenneService.sharedContext();
		SelectQuery query = new SelectQuery(BinaryInfoRelation.class, ExpressionFactory.matchExp(
				BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, entityIdNum).andExp(
				ExpressionFactory.matchExp(BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, entityIdentifier)));
		@SuppressWarnings("unchecked")
		List<BinaryInfoRelation> relations = sharedContext.performQuery(query);
		if (!relations.isEmpty()) {
			List<BinaryInfo> attachedFiles = new ArrayList<BinaryInfo>(relations.size());
			for (BinaryInfoRelation relation : relations) {
				attachedFiles.add(relation.getBinaryInfo());
			}
			return getCollegeQualifier(showOnlyPublic).filterObjects(attachedFiles);
		}
		return new ArrayList<BinaryInfo>(0);
	}
	
	private boolean showStudentsAttachments() {
		return applicationStateManager.getIfExists(Contact.class) != null;
	}

}
