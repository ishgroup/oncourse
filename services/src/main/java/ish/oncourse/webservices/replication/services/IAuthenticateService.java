package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;

public interface IAuthenticateService {

	long authenticate(String webServicesSecurityCode, long lastCommKey) throws InternalAuthenticationException;

	/**
	 * Generates new communication key for college, for details refer
	 * http://intranet.ish.com.au/drupal/ReplicationWorkflow#Use of the
	 * communication key
	 * 
	 * @param college
	 *            willow college
	 * @return communication key
	 */
	Long generateNewKey(College college);
	
	/**
	 * Puts college into HALT state, which prevents all further replication,
	 * until recovery from HALT is done by admins.
	 * 
	 * @param college
	 *            willow college
	 */
	void putCollegeInHaltState(College college);
	
	public class InternalAuthenticationException extends Exception {
		private static final long serialVersionUID = 4896841823965817593L;
		private InternalErrorCode errorCode;

		public InternalAuthenticationException(final String message, final InternalErrorCode errorCode) {
			super(message);
			this.errorCode = errorCode;
		}

		public InternalAuthenticationException(String message) {
			this(message, null);
		}

		/**
		 * @return the errorCodeIndex
		 */
		public InternalErrorCode getErrorCode() {
			return errorCode;
		}

		/**
		 * @param errorCode the errorCodeIndex to set
		 */
		public void setErrorCode(InternalErrorCode errorCode) {
			this.errorCode = errorCode;
		}
	}

	public enum InternalErrorCode {
		INVALID_SESSION,
		INVALID_SECURITY_CODE,
		EMPTY_COMMUNICATION_KEY,
		HALT_COMMUNICATION_KEY,
		INVALID_COMMUNICATION_KEY,
		NO_KEYS
	}
}
