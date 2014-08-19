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
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.*;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Marek Wawrzyczny
 */
public class CollegeService implements ICollegeService {

	private static final Logger LOGGER = Logger.getLogger(CollegeService.class);

	@Inject
	private ICayenneService cayenneService;

	/**
	 * @see ICollegeService#findById(Long)
	 */
	@Override
	public College findById(Long collegeId) {
		Expression expr = ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, collegeId);
		SelectQuery q = new SelectQuery(College.class, expr);
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

		College college = null;

		ObjectContext objectContext = cayenneService.sharedContext();

		Expression qualifier = ExpressionFactory.matchExp(College.WEB_SERVICES_SECURITY_CODE_PROPERTY, securityCode);
		SelectQuery q = new SelectQuery(College.class, qualifier);

		@SuppressWarnings("unchecked")
		List<College> records = objectContext.performQuery(q);

		if ((records == null) || records.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No College found for 'security code': '" + securityCode + "'");
			}
		} else if (records.size() == 1) {
			college = records.get(0);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("College with ID: '" + college.getId() + "' found for 'security code': '" + securityCode
						+ "'");
			}
		} else {
			LOGGER.error("Multiple Colleges found for 'security code': '" + securityCode + "'");
		}

		return college;
	}
	
	/**
	 * @see ICollegeService#findBySecurityCodeLastChars(String)
	 */
	@Override
	public College findBySecurityCodeLastChars(String securityCodeEnding) {
		College college = null;

		ObjectContext objectContext = cayenneService.sharedContext();

		Expression qualifier = ExpressionFactory.likeExp(College.WEB_SERVICES_SECURITY_CODE_PROPERTY, "%" + securityCodeEnding);
		SelectQuery q = new SelectQuery(College.class, qualifier);

		@SuppressWarnings("unchecked")
		List<College> records = objectContext.performQuery(q);

		if (records.size() == 1) {
			college = records.get(0);
		}

		return college;
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
		college = (College) context.localObject(college.getObjectId(), null);
		if (college.getFirstRemoteAuthentication() == null) {
			college.setFirstRemoteAuthentication(accessTime);
		} else {
			college.setLastRemoteAuthentication(accessTime);
		}
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
            LOGGER.error(e.getMessage(), e);
        }
    }

	/**
	 * @see ICollegeService#allColleges()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<College> allColleges() {
		Expression expr = ExpressionFactory.noMatchExp(College.BILLING_CODE_PROPERTY, null);
		SelectQuery q = new SelectQuery(College.class, expr);
		q.setPageSize(20);
		q.addOrdering(new Ordering(College.NAME_PROPERTY, SortOrder.DESCENDING_INSENSITIVE));
		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	public Set<String> allSiteKeys() {
		EJBQLQuery q = new EJBQLQuery("select distinct w.siteKey from WebSite w");
		@SuppressWarnings("unchecked")
		List<String> keys = cayenneService.sharedContext().performQuery(q);
		return new HashSet<>(keys);
	}
}
