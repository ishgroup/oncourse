/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.system;

import ish.oncourse.model.College;
import ish.oncourse.model.CommunicationKey;
import ish.oncourse.model.CommunicationKeyType;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * 
 * @author Marek Wawrzyczny
 */
public class CollegeService implements ICollegeService {

	private static final Logger LOGGER = Logger.getLogger(CollegeService.class);

	@Inject
	private ICayenneService cayenneService;

	@Override
	public College findBySecurityCode(String securityCode) {

		College college = null;

		ObjectContext objectContext = cayenneService.newContext();

		Expression qualifier = ExpressionFactory.matchExp(College.WEB_SERVICES_SECURITY_CODE_PROPERTY, securityCode);
		SelectQuery q = new SelectQuery(College.class, qualifier);
		q.addPrefetch(College.COMMUNICATION_KEYS_PROPERTY);

		List<College> records = objectContext.performQuery(q);

		if ((records == null) || records.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No College found for 'security code': '" + securityCode + "'");
			}
		} else if (records.size() == 1) {
			college = records.get(0);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("College with ID: '" + college.getId() + "' found for 'security code': '" + securityCode + "'");
			}
		} else {
			LOGGER.error("Multiple Colleges found for 'security code': '" + securityCode + "'");
		}

		return college;
	}

	@Override
	public College recordNewCollege(String securityCode, String ipAddress, String angelVersion, Date accessTime) {

		ObjectContext objectContext = cayenneService.newContext();

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

		CommunicationKey replicationKey = new CommunicationKey();
		replicationKey.setKeyStatus(KeyStatus.HALT);
		replicationKey.setType(CommunicationKeyType.REPLICATION);
		replicationKey.setKey(-1l);

		CommunicationKey paymentInKey = new CommunicationKey();
		paymentInKey.setKeyStatus(KeyStatus.HALT);
		paymentInKey.setType(CommunicationKeyType.PAYMENT);
		paymentInKey.setKey(-1l);

		CommunicationKey paymentOutKey = new CommunicationKey();
		paymentOutKey.setType(CommunicationKeyType.REFUND);
		paymentOutKey.setKeyStatus(KeyStatus.HALT);
		paymentOutKey.setKey(-1l);

		college.addToCommunicationKeys(replicationKey);
		college.addToCommunicationKeys(paymentInKey);
		college.addToCommunicationKeys(paymentOutKey);

		college.setBillingCode(null);

		college.setIsTestingWebServicePayments(false);
		college.setIsTestingWebSitePayments(false);
		college.setIsWebServicePaymentsEnabled(false);
		college.setIsWebSitePaymentsEnabled(false);
		college.setNationalProviderCode(null);
		college.setPaymentGatewayAccount(null);
		college.setPaymentGatewayPass(null);
		college.setPaymentGatewayType(null);
		college.setRequiresAvetmiss(null);
		college.setWebServicesLogin(null);
		college.setWebServicesPass(null);

		objectContext.commitChanges();

		return college;
	}

	@Override
	public void recordWSAccess(College college, String ipAddress, String angelVersion, Date accessTime) {

		ObjectContext context = cayenneService.newContext();

		if (college.getFirstRemoteAuthentication() == null) {
			college.setFirstRemoteAuthentication(accessTime);
		} else {
			college.setLastRemoteAuthentication(accessTime);
		}
		college.setAngelVersion(angelVersion);
		college.setIpAddress(ipAddress);

		context.localObject(college.getObjectId(), college);
		context.commitChanges();
	}

	@Override
	public List<College> allColleges() {
		SelectQuery q = new SelectQuery(College.class);
		return cayenneService.sharedContext().performQuery(q);
	}
}
