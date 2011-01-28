package ish.oncourse.services.system;

import ish.oncourse.model.College;
import java.util.Date;

/**
 * A number of services used primarily by WebServices.
 *
 * @author marek
 */
public interface ICollegeService {

	/**
	 * Finds college by security code.
	 *
	 * @param securityCode code to match against the security code property
	 *
	 * @return College with matching security code
	 */
	College findBySecurityCode(String securityCode);

	/**
	 * Creates and saves a new College record for this security code.
	 * 
	 * @param securityCode
	 * @param ipAddress
	 * @param angelVersion
	 * @param accessTime
	 * @return the new College object
	 */
	College recordNewCollege(String securityCode, String ipAddress, String angelVersion, Date accessTime);

	/**
	 * Function that persists details of Web Service access within the College's
	 * database record
	 *
	 * @param college the college
	 * @param ipAddress the IP address
	 * @param angelVersion Angel Server version
	 * @param accessTime time of access (stored either in First or Last Access time
	 * property)
	 */
	void recordWSAccess(College college, String ipAddress, String angelVersion, Date accessTime);
}
