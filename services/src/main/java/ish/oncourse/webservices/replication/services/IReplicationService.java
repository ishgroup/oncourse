package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.soap.v4.ReplicationFault;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;

public interface IReplicationService {
	
	ReplicationResult sendRecords(ReplicationRecords req) throws ReplicationFault;

	ReplicationRecords getRecords() throws ReplicationFault;

	int sendResults(ReplicationResult request) throws ReplicationFault;
}
