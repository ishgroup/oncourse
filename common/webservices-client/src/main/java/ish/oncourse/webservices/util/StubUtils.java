/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.util;

public class StubUtils {

	public static void setInstructionParameters(GenericInstructionStub instructionStub, GenericParametersMap parametersMap) {
		SupportedVersions version = PortHelper.getVersionByInstructionStub(instructionStub);

		switch (version) {
			case V6:
				((ish.oncourse.webservices.v6.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v6.stubs.replication.ParametersMap) parametersMap);
				break;
			case V7:
				((ish.oncourse.webservices.v7.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v7.stubs.replication.ParametersMap) parametersMap);
				break;
			case V8:
				((ish.oncourse.webservices.v8.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v8.stubs.replication.ParametersMap) parametersMap);
				break;
			case V9:
				((ish.oncourse.webservices.v9.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v9.stubs.replication.ParametersMap) parametersMap);
				break;
			default:
				throw new IllegalArgumentException("This stub version is not supported.");
		}
	}	
	
	public static boolean hasSuccessStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);
		
		switch (version) {
			case V6:
				return ish.oncourse.webservices.v6.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v6.stubs.replication.ReplicatedRecord) record).getStatus());
			case V7:
				return ish.oncourse.webservices.v7.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v7.stubs.replication.ReplicatedRecord) record).getStatus());
			case V8:
				return ish.oncourse.webservices.v8.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v8.stubs.replication.ReplicatedRecord) record).getStatus());
			case V9:
				return ish.oncourse.webservices.v9.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v9.stubs.replication.ReplicatedRecord) record).getStatus());
			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}
	
	public static boolean hasFailedStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);

		switch (version) {
			case V6:
				return ish.oncourse.webservices.v6.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v6.stubs.replication.ReplicatedRecord) record).getStatus());
			case V7:
				return ish.oncourse.webservices.v7.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v7.stubs.replication.ReplicatedRecord) record).getStatus());
			case V8:
				return ish.oncourse.webservices.v8.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v8.stubs.replication.ReplicatedRecord) record).getStatus());
			case V9:
				return ish.oncourse.webservices.v9.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v9.stubs.replication.ReplicatedRecord) record).getStatus());
			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}
	
	public static void setSuccessStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);

		switch (version) {
			case V6:
				((ish.oncourse.webservices.v6.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v6.stubs.replication.Status.SUCCESS);
				break;
			case V7:
				((ish.oncourse.webservices.v7.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v7.stubs.replication.Status.SUCCESS);
				break;
			case V8:
				((ish.oncourse.webservices.v8.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v8.stubs.replication.Status.SUCCESS);
				break;
			case V9:
				((ish.oncourse.webservices.v9.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v9.stubs.replication.Status.SUCCESS);
				break;
			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}

	public static void setFailedStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);

		switch (version) {
			case V6:
				((ish.oncourse.webservices.v6.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v6.stubs.replication.Status.FAILED);
				break;
			case V7:
				((ish.oncourse.webservices.v7.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v7.stubs.replication.Status.FAILED);
				break;
			case V8:
				((ish.oncourse.webservices.v8.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v8.stubs.replication.Status.FAILED);
				break;
			case V9:
				((ish.oncourse.webservices.v9.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v9.stubs.replication.Status.FAILED);
				break;
			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}
}
