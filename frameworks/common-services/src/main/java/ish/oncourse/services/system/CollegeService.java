/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.system;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.College;
import ish.oncourse.services.BaseService;
import java.util.List;
import org.apache.log4j.Logger;


/**
 *
 * @author Marek Wawrzyczny
 */
public class CollegeService extends BaseService<College> implements ICollegeService {

	private static final Logger LOGGER = Logger.getLogger(CollegeService.class);


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


}
