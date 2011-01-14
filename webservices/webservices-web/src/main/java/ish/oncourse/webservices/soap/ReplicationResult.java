package ish.oncourse.webservices.soap;

import java.util.List;


/**
 * ReplicationResults class can be used by a system to set remote system's ID 
 * for new records or verify IDs for existing records. It also allows us to not 
 * have to fail an entire batch if only a few records fail to replicate. The 
 * failed records can be
 *
 * @author marek
 */
public class ReplicationResult {

	private Status status;
	private String message;
	private List<Object> referenceStubs;


	/**
	 * Indicates replication success or failure for this record.
	 *
	 * @return the status
	 */ public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */ public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Any messages, such as validation messages or replication failure reasons.
	 *
	 * @return the message
	 */ public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */ public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * List containing reference stubs for records sent to replicate. To be used
	 * to set (new records) or verify (existing records) the remote system's data.
	 *
	 * <p>If one finds, for example, that the remote system's ID is different
	 * for an existing record to the ID stored locally, then this would indicate
	 * that some sort of data corruption took place.</p>
	 *
	 * @return the referenceStubs
	 */ public List<Object> getReferenceStubs() {
		return referenceStubs;
	}

	/**
	 * @param referenceStubs the referenceStubs to set
	 */ public void setReferenceStubs(List<Object> referenceStubs) {
		this.referenceStubs = referenceStubs;
	}
}
