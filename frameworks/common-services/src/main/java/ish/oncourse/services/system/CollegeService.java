/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.system;

import java.util.Date;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.services.BaseService;
import java.util.List;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;


/**
 *
 * @author Marek Wawrzyczny
 */
public class CollegeService extends BaseService<College> implements ICollegeService {

	private static final Logger LOGGER = Logger.getLogger(CollegeService.class);


	@Override
	public College findBySecurityCode(String securityCode) {

		College college = null;
		Expression qualifier = ExpressionFactory.matchExp(
				College.WEB_SERVICES_SECURITY_CODE_PROPERTY, securityCode);
		List<College> records = findByQualifier(qualifier);

		if ((records == null) || records.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No College found for 'security code': '"
						+ securityCode + "'");
			}
		} else if (records.size() == 1) {
			college = records.get(0);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("College with ID: '" + college.getId()
						+ "' found for 'security code': '" + securityCode + "'");
			}
		} else {
			LOGGER.error("Multiple Colleges found for 'security code': '"
					+ securityCode + "'");
		}

		return college;
	}

	@Override
	public College recordNewCollege(
			String securityCode, String ipAddress, String angelVersion, Date accessTime) {

		ObjectContext context = getCayenneService().newContext();
		College college = context.newObject(College.class);

		// TODO: An entity factory would be handy here... perhaps another time
		college.setWebServicesSecurityCode(securityCode);
		college.setName(ipAddress);
		college.setIpAddress(ipAddress);
		college.setAngelVersion(angelVersion);
		college.setCreated(accessTime);
		college.setModified(accessTime);
		college.setFirstRemoteAuthentication(accessTime);
		college.setLastRemoteAuthentication(accessTime);

		college.setCommunicationKey(null);
		college.setCommunicationKeyStatus(KeyStatus.HALT);

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

		try {
			context.commitChanges();
		} catch (Exception e) {
			LOGGER.error("Error while saving/recording a new College", e);
			college = null;
		}

		return college;
	}

	@Override
	public void recordWSAccess(
			College college, String ipAddress, String angelVersion, Date accessTime) {

		ObjectContext context = getCayenneService().newContext();

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


}
