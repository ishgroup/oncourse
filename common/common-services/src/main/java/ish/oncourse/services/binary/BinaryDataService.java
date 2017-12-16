package ish.oncourse.services.binary;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.AttachmentSpecialType;
import ish.oncourse.function.IGet;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;

import java.util.List;
import java.util.Random;

import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE;

public class BinaryDataService implements IBinaryDataService {

	private static final Logger logger = LogManager.getLogger();

	static final String NAME_PROFILE_PICTURE = "Profile picture";
	private static final String CONTEXT_PATH_TEMPLATE = "/a/%s/%s.%s";
	private static final String PRIVATE_RESOURCE_PATTERN = "/portal/resource/%s";

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ApplicationStateManager applicationStateManager;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private IS3Service s3Service;

	@Inject
	private ITagService tagService;
	
	public BinaryDataService(ICayenneService cayenneService, IWebSiteService webSiteService, ApplicationStateManager applicationStateManager, PreferenceController preferenceController, IS3Service s3Service, ITagService tagService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
		this.applicationStateManager = applicationStateManager;
		this.preferenceController = preferenceController;
		this.s3Service = s3Service;
		this.tagService = tagService;
	}

	public <E> Document getBinaryInfo(Property<E> property, E value) {
		return getRandomBinaryInfo(new GetCollegeExpression(webSiteService.getCurrentCollege(), true, isStudentLoggedIn()).get()
				.andExp(property.eq(value)));
	}

	private Document getRandomBinaryInfo(final Expression qualifier) {
		ObjectContext sharedContext = cayenneService.newContext();

		List<Document> binaries = ObjectSelect.query(Document.class, qualifier)
				.cacheStrategy(SHARED_CACHE, Document.class.getSimpleName())
				.select(sharedContext);

		if (binaries.size() > 0) {
			int random = new Random().nextInt(binaries.size());
			return binaries.get(random);
		} else {
			return null;
		}
	}

	@Override
	public List<Document> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean hidePrivateAttachments) {
		return new GetDocuments(entityIdentifier, entityIdNum, webSiteService.getCurrentCollege(),
				hidePrivateAttachments, isStudentLoggedIn(), cayenneService.newContext()).get();
	}

	private boolean isStudentLoggedIn() {
		return applicationStateManager.getIfExists(Contact.class) != null;
	}

	@Override
	public Document getProfilePicture(Contact contact) {
		ObjectContext sharedContext = cayenneService.newContext();

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
		return binaryInfos.size() > 0 ? binaryInfos.get(0) : null;
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

	public <E extends Queueable> Document getAttachmentByTag(E entity, final String tagPath) {
		IGet<Tag> getTag = new IGet<Tag>() {
			@Override
			public Tag get() {
				return tagService.getTagByFullPath(tagPath);
			}
		};

		GetDocuments getDocuments = new GetDocuments(entity.getObjectId().getEntityName(),
				entity.getId(), webSiteService.getCurrentCollege(), true, isStudentLoggedIn(), cayenneService.newContext());
		return new GetDocumentByTag(getTag, getDocuments).get();
	}

	public <E extends Queueable> List<Document> getAttachments(E entity) {
		GetDocuments getDocuments = new GetDocuments(entity.getObjectId().getEntityName(),
				entity.getId(), webSiteService.getCurrentCollege(), true, isStudentLoggedIn(), cayenneService.newContext());
		return getDocuments.get();
	}

	public <E extends Queueable> List<Document> getImages(E entity) {
		GetDocuments getDocuments = new GetDocuments(entity.getObjectId().getEntityName(),
				entity.getId(), webSiteService.getCurrentCollege(), true, isStudentLoggedIn(), cayenneService.newContext());
		return new GetImages(getDocuments).get();
	}
}
