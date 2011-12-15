package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.List;

public interface ITransactionGroupValidator {
	/**
	 * Peforms additional check of transaction groups. Just in case of
	 * incomplete data appeared in queue. Incomplete data may be entered by
	 * hands or appear right after college migration.
	 * 
	 * @param result replication result
	 */
	List<TransactionGroup> validateAndReturnFixedGroups(List<TransactionGroup> groups);
}
