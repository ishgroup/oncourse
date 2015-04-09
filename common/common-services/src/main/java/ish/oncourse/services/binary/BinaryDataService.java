package ish.oncourse.services.binary;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.AttachmentSpecialType;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Document;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.s3.IS3Service;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BinaryDataService implements IBinaryDataService {

	private static final Logger LOGGER = Logger.getLogger(BinaryDataService.class);
	
	public static final String NAME_PROFILE_PICTURE = "Profile picture";
	public static final String CONTEXT_PATH_TEMPLATE = "/a/%s/%s.%s";
	public static final String PRIVATE_RESOURCE_PATTERN = "/portal/resource/%s";

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ApplicationStateManager applicationStateManager;

    @Inject
    private IFileStorageAssetService fileStorageAssetService;
	
	@Inject
	private PreferenceController preferenceController;
	
	@Inject
	private IS3Service s3Service;

	public Document getBinaryInfo(final String searchProperty, Object value) {
		final Expression qualifier = getCollegeQualifier().andExp(ExpressionFactory.matchExp(searchProperty, value));
		return getRandomBinaryInfo(qualifier);
	}

	private Expression getCollegeQualifier() {
		return getCollegeQualifier(true);
	}

	private Expression getCollegeQualifier(boolean hidePrivateAttachments) {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Document.COLLEGE_PROPERTY, cayenneService.sharedContext()
				.localObject(currentCollege));
		if (hidePrivateAttachments) {
			if (isStudentLoggedIn()) {
				qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.PRIVATE));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.PUBLIC));
			}
		}
		//exclude deleted documents;
		qualifier =  qualifier.andExp(ExpressionFactory.matchExp(Document.IS_REMOVED_PROPERTY, false));
		return qualifier;
	}

	private Document getRandomBinaryInfo(final Expression qualifier) {
		ObjectContext sharedContext = cayenneService.sharedContext();
		final SelectQuery binaryCount = new SelectQuery(Document.class, qualifier);
		
		List<Document> binaries = (List<Document>) sharedContext.performQuery(binaryCount);
		Long count = (long) binaries.size();
		Document randomResult = null;
		int attempt = 0;
		if (count > 0) {
			while (randomResult == null && attempt++ < 5) {
				int random = new Random().nextInt(count.intValue());

				SelectQuery query = new SelectQuery(Document.class, qualifier);
				query.setFetchOffset(random);
				query.setFetchLimit(1);
				randomResult = (Document) Cayenne.objectForQuery(sharedContext, query);

				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(String.format("Fetched random image: %s", randomResult));
				}
			}
		}

		return randomResult;
	}

	@Override
	public List<Document> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean hidePrivateAttachments) {
		ObjectContext sharedContext = cayenneService.sharedContext();
		SelectQuery query = new SelectQuery(BinaryInfoRelation.class, ExpressionFactory.matchExp(
				BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, entityIdNum).andExp(
				ExpressionFactory.matchExp(BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, entityIdentifier)));
		List<BinaryInfoRelation> relations = sharedContext.performQuery(query);
		if (!relations.isEmpty()) {
			List<Document> attachedFiles = new ArrayList<>(relations.size());
			for (BinaryInfoRelation relation : relations) {
				//we need the check to exclude Profile Picture from common attachment list
				if (!isProfilePicture(relation))
					attachedFiles.add(relation.getDocument());
			}
			return getCollegeQualifier(hidePrivateAttachments).filterObjects(attachedFiles);
		}
		return Collections.emptyList();
	}

	boolean isProfilePicture(BinaryInfoRelation relation)
	{
		// old style attachment name check is still remaining, it should be removed once all colleges switch to angel 5.1+
		return AttachmentSpecialType.PROFILE_PICTURE.equals(relation.getSpecialType()) || 
				relation.getEntityIdentifier().equals(Contact.class.getSimpleName()) && relation.getDocument().getName().equals(NAME_PROFILE_PICTURE);
	}
	
	private boolean isStudentLoggedIn() {
		return applicationStateManager.getIfExists(Contact.class) != null;
	}

	@Override
	public Document getProfilePicture(Contact contact) {
		ObjectContext sharedContext = cayenneService.sharedContext();

		SelectQuery profilePictureRelationsQuery = new SelectQuery(BinaryInfoRelation.class, ExpressionFactory.matchExp(BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, contact.getId())
				.andExp(ExpressionFactory.matchExp(BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, contact.getObjectId().getEntityName()))
				.andExp(ExpressionFactory.matchExp(BinaryInfoRelation.SPECIAL_TYPE_PROPERTY, AttachmentSpecialType.PROFILE_PICTURE)));
		
		List<BinaryInfoRelation> profilePictureRelations = sharedContext.performQuery(profilePictureRelationsQuery);
		
		if (!profilePictureRelations.isEmpty()) {
			return profilePictureRelations.get(0).getDocument();
		}
		
		// if couldn't find profile picture using BinaryInfoRelation.specialType field then default to the old style name search
		// this logic should be removed once all colleges switch to angel 5.1+
		
		SelectQuery query = new SelectQuery(Document.class, ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + '.' + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, contact.getId())
				.andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + '.' + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, contact.getObjectId().getEntityName()))
				.andExp(ExpressionFactory.matchExp(Document.NAME_PROPERTY, NAME_PROFILE_PICTURE)));
		List<Document> binaryInfos = sharedContext.performQuery(query);
		return binaryInfos.size() > 0 ? binaryInfos.get(0): null;
	}

	@Override
	public String getUrl(Document binaryInfo) {
		if (binaryInfo.getFileUUID() != null) {
			if (AttachmentInfoVisibility.PUBLIC.equals(binaryInfo.getWebVisibility())) {
				return s3Service.getPermanentUrl(preferenceController.getStorageBucketName(), binaryInfo.getFileUUID());
			} else {
				// for now hardcoding "/portal" path into url since portal is the only place where
				// private resources are displayed
				return String.format(PRIVATE_RESOURCE_PATTERN, binaryInfo.getFileUUID());
			}
		}
		return String.format(CONTEXT_PATH_TEMPLATE, binaryInfo.getCurrentVersion().getFilePath(), binaryInfo.getName(), binaryInfo.getType());
	}
}
