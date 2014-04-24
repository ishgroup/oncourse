package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.util.GenericReplicationRecords;
import ish.oncourse.webservices.util.GenericReplicationResult;
import ish.oncourse.webservices.util.SupportedVersions;

public interface IReplicationService {
	
	GenericReplicationResult sendRecords(GenericReplicationRecords req) throws InternalReplicationFault;
	
	GenericReplicationRecords getRecords(final SupportedVersions version) throws InternalReplicationFault;
	
	int sendResults(GenericReplicationResult request) throws InternalReplicationFault;
	
	public class InternalReplicationFault extends Exception {
		private static final long serialVersionUID = 8559175687600028596L;
		private String faultReasonMessage;
		private Integer faultCode;

		public InternalReplicationFault(String message, Integer faultReasonCode, String faultReasonMessage) {
			super(message);
			this.faultCode = faultReasonCode;
			this.faultReasonMessage = faultReasonMessage;
		}
		
		/**
		 * @return the faultReasonMessage
		 */
		public String getFaultReasonMessage() {
			return faultReasonMessage;
		}
		
		/**
		 * @param faultReasonMessage the faultReasonMessage to set
		 */
		public void setFaultReasonMessage(String faultReasonMessage) {
			this.faultReasonMessage = faultReasonMessage;
		}
		
		/**
		 * @return the faultCode
		 */
		public Integer getFaultCode() {
			return faultCode;
		}
		
		/**
		 * @param faultCode the faultCode to set
		 */
		public void setFaultCode(Integer faultCode) {
			this.faultCode = faultCode;
		}

	}
}
