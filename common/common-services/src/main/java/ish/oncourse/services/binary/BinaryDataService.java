package ish.oncourse.services.binary;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BinaryDataService implements IBinaryDataService {

	private static final Logger LOGGER = Logger.getLogger(BinaryDataService.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

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

	public BinaryInfo getBinaryInfo(String searchProperty, Object value) {
		Expression qualifier = getCollegeQualifier().andExp(ExpressionFactory.matchExp(searchProperty, value));
		return getRandomBinaryInfo(qualifier);
	}

	public BinaryInfo getRandomImage() {
		Expression qualifier = getCollegeQualifier().andExp(BinaryInfo.getImageQualifier());
		return getRandomBinaryInfo(qualifier);
	}

	private Expression getCollegeQualifier() {
		return getCollegeQualifier(true);
	}

	private Expression getCollegeQualifier(boolean isWebVisible) {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(BinaryInfo.COLLEGE_PROPERTY, cayenneService.sharedContext()
				.localObject(currentCollege.getObjectId(), null));
		if (isWebVisible) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(BinaryInfo.IS_WEB_VISIBLE_PROPERTY, true));
		}
		return qualifier;
	}

	private BinaryInfo getRandomBinaryInfo(Expression qualifier) {

		ObjectContext sharedContext = cayenneService.sharedContext();

		EJBQLQuery q = new EJBQLQuery("select count(i) from BinaryInfo i where " + qualifier.toEJBQL("i"));

		Long count = (Long) sharedContext.performQuery(q).get(0);

		BinaryInfo randomResult = null;

		int attempt = 0;

		if (count != null && count > 0) {
			while ((randomResult == null || randomResult.getBinaryData() == null) && attempt++ < 5) {
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
	public List<BinaryInfo> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean isWebVisible) {
		ObjectContext sharedContext = cayenneService.sharedContext();

		SelectQuery query = new SelectQuery(BinaryInfoRelation.class, ExpressionFactory.matchExp(
				BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, entityIdNum).andExp(
				ExpressionFactory.matchExp(BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, entityIdentifier)));

		List<BinaryInfoRelation> relations = sharedContext.performQuery(query);
		if (!relations.isEmpty()) {
			List<BinaryInfo> attachedFiles = new ArrayList<BinaryInfo>(relations.size());
			for (BinaryInfoRelation relation : relations) {
				attachedFiles.add(relation.getBinaryInfo());
			}
			return getCollegeQualifier(isWebVisible).filterObjects(attachedFiles);
		}

		return new ArrayList<BinaryInfo>(0);
	}

}
