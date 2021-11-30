package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;

public interface IAuthenticateService {

	void authenticate(String webServicesSecurityCode) throws InternalAuthenticationException;

	/**
	 * Update last LastRemoteAuthentication tomestap and put collge ID ito request trhead local* 
	 * @param college
	 */
	void recordCollegeAttribute(College college);
	
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
		INVALID_SECURITY_CODE,
	}
}
