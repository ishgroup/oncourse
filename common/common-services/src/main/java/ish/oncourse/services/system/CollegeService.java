/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.system;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.CommonUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.NO_CACHE;

/**
 * 
 * @author Marek Wawrzyczny
 */
public class CollegeService implements ICollegeService {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private ICayenneService cayenneService;

	/**
	 * @see ICollegeService#findById(Long)
	 */
	@Override
	public College findById(Long collegeId) {
		Expression expr = ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, collegeId);
		SelectQuery q = new SelectQuery(College.class, expr);
		q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		return (College) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	/**
	 * @see ICollegeService#findBySiteKey(String)
	 */
	@Override
	public College findBySiteKey(String siteKey) {
		SelectQuery query = new SelectQuery(WebSite.class, ExpressionFactory.matchExp(WebSite.SITE_KEY_PROPERTY,
				siteKey));
		WebSite site = (WebSite) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
		return (site != null) ? site.getCollege() : null;
	}

	/**
	 * @see ICollegeService#findBySecurityCode(String)
	 */
	@Override
	public College findBySecurityCode(String securityCode) {

		College college;
		try {
			ObjectContext objectContext = cayenneService.sharedContext();
			college = ObjectSelect.query(College.class).where(College.WEB_SERVICES_SECURITY_CODE.eq(securityCode)).selectOne(objectContext);
			if (college == null) {
				logger.debug("No College found for security code: {}", securityCode);
			} else {
				logger.debug("College with ID: {} found for security code: {}", college.getId(), securityCode);
			}
		} catch (CayenneRuntimeException e) {
			logger.error("Multiple Colleges found for security code: {}", securityCode, e);
			throw e;
		}
		return college;
	}
	
	/**
	 * @see ICollegeService#findBySecurityCodeLastChars(String)
	 */
	@Override
	public College findBySecurityCodeLastChars(String securityCodeEnding) {
		ObjectContext objectContext = cayenneService.sharedContext();
		return ObjectSelect.query(College.class).where(College.WEB_SERVICES_SECURITY_CODE.like("%" + securityCodeEnding)).selectOne(objectContext);
	}

	/**
	 * @see ICollegeService#recordNewCollege(String, String, String, Date)
	 */
	@Override
	public College recordNewCollege(String securityCode, String ipAddress, String angelVersion, Date accessTime) {

		ObjectContext objectContext = cayenneService.newNonReplicatingContext();

		College college = objectContext.newObject(College.class);

		// TODO: An entity factory would be handy here... perhaps another time
		college.setWebServicesSecurityCode(securityCode);
		college.setName(ipAddress);
		college.setIpAddress(ipAddress);
		college.setAngelVersion(angelVersion);
		college.setCreated(accessTime);
		college.setModified(accessTime);
		college.setFirstRemoteAuthentication(accessTime);
		college.setLastRemoteAuthentication(accessTime);
		college.setCommunicationKey(-1l);
		college.setCommunicationKeyStatus(KeyStatus.HALT);

		college.setBillingCode(null);

		college.setIsTestingWebServicePayments(false);
		college.setIsTestingWebSitePayments(false);
		college.setIsWebServicePaymentsEnabled(false);
		college.setIsWebSitePaymentsEnabled(false);
		college.setPaymentGatewayAccount(null);
		college.setPaymentGatewayPass(null);
		college.setRequiresAvetmiss(false);
		college.setTimeZone("Australia/Sydney");

		objectContext.commitChanges();

		return college;
	}

	/**
	 * @see ICollegeService#recordNewCollege(String, String, String, Date)
	 */
	@Override
	public void recordWSAccess(College college, String ipAddress, String angelVersion, Date accessTime) {

        boolean angelVersionChanged = (CommonUtils.compare(angelVersion, college.getAngelVersion()) > 0);

        ObjectContext context = cayenneService.newNonReplicatingContext();
		college = context.localObject(college);
		college.setAngelVersion(angelVersion);
		college.setIpAddress(ipAddress);

		context.commitChanges();

        if (angelVersionChanged)
        {
            resetReplication(college);
        }

	}

    /**
     * The methods reset numberOfAttempts for all QueuedRecords  for the college.
     * We call the method when the college is updated to new version.
     * We do it to try to replicate  data hich had not been replicated
     * because angel had some bug and the bug was fixed in the new version
     */
    private void resetReplication(College college)
    {
        try {
            SQLTemplate template = new SQLTemplate(QueuedRecord.class, String.format("UPDATE QueuedRecord set numberOfAttempts = 0 where collegeId = %d", college.getId()));
            ObjectContext context = cayenneService.newNonReplicatingContext();
            context.performGenericQuery(template);
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

	/**
	 * @see ICollegeService#allColleges()
	 */
	@Override
	public List<College> allColleges() {
		return ObjectSelect.query(College.class)
				.where(College.BILLING_CODE.isNotNull())
				.orderBy(College.NAME.descInsensitive())
				.cacheStrategy(NO_CACHE)
				.select(cayenneService.sharedContext());
	}
}
