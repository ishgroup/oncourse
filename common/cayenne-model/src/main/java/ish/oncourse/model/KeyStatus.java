package ish.oncourse.model;

public enum KeyStatus {
	/**
	 * Replication works, college shows in willowAdmin
	 */
	VALID,

	/**
	 * Replication works, college shows in willowAdmin. Some things might be limited because they haven’t paid their invoices
	 */
	RESTRICTED,

	/**
	 * Replication stopped, websites attached to this college not shown, college not shown in willowAdmin
	 */
	HALT,

	/**
	 * College ready for deletion. Some other script later on will delete the S3 bucket and other things.
	 */
	DELETE
}
