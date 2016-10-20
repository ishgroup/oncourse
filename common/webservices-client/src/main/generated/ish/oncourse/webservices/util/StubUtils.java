/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.util;

public class StubUtils {

	public static void setInstructionParameters(GenericInstructionStub instructionStub, GenericParametersMap parametersMap) {
		SupportedVersions version = PortHelper.getVersionByInstructionStub(instructionStub);

		switch (version) {

			case V10:
				((ish.oncourse.webservices.v10.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v10.stubs.replication.ParametersMap) parametersMap);
				break;

			case V11:
				((ish.oncourse.webservices.v11.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v11.stubs.replication.ParametersMap) parametersMap);
				break;

			case V12:
				((ish.oncourse.webservices.v12.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v12.stubs.replication.ParametersMap) parametersMap);
				break;

			case V13:
				((ish.oncourse.webservices.v13.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v13.stubs.replication.ParametersMap) parametersMap);
				break;

			case V14:
				((ish.oncourse.webservices.v14.stubs.replication.InstructionStub) instructionStub)
						.setParameters((ish.oncourse.webservices.v14.stubs.replication.ParametersMap) parametersMap);
				break;

			default:
				throw new IllegalArgumentException("This stub version is not supported.");
		}
	}	
	
	public static boolean hasSuccessStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);
		
		switch (version) {

			case V10:
				return ish.oncourse.webservices.v10.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v10.stubs.replication.ReplicatedRecord) record).getStatus());

			case V11:
				return ish.oncourse.webservices.v11.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v11.stubs.replication.ReplicatedRecord) record).getStatus());

			case V12:
				return ish.oncourse.webservices.v12.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v12.stubs.replication.ReplicatedRecord) record).getStatus());

			case V13:
				return ish.oncourse.webservices.v13.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord) record).getStatus());

			case V14:
				return ish.oncourse.webservices.v14.stubs.replication.Status.SUCCESS.equals(
						((ish.oncourse.webservices.v14.stubs.replication.ReplicatedRecord) record).getStatus());

			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}
	
	public static boolean hasFailedStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);

		switch (version) {

			case V10:
				return ish.oncourse.webservices.v10.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v10.stubs.replication.ReplicatedRecord) record).getStatus());

			case V11:
				return ish.oncourse.webservices.v11.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v11.stubs.replication.ReplicatedRecord) record).getStatus());

			case V12:
				return ish.oncourse.webservices.v12.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v12.stubs.replication.ReplicatedRecord) record).getStatus());

			case V13:
				return ish.oncourse.webservices.v13.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord) record).getStatus());

			case V14:
				return ish.oncourse.webservices.v14.stubs.replication.Status.FAILED.equals(
						((ish.oncourse.webservices.v14.stubs.replication.ReplicatedRecord) record).getStatus());

			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}
	
	public static void setSuccessStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);

		switch (version) {

			case V10:
				((ish.oncourse.webservices.v10.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v10.stubs.replication.Status.SUCCESS);
				break;

			case V11:
				((ish.oncourse.webservices.v11.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v11.stubs.replication.Status.SUCCESS);
				break;

			case V12:
				((ish.oncourse.webservices.v12.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v12.stubs.replication.Status.SUCCESS);
				break;

			case V13:
				((ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v13.stubs.replication.Status.SUCCESS);
				break;

			case V14:
				((ish.oncourse.webservices.v14.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v14.stubs.replication.Status.SUCCESS);
				break;

			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}

	public static void setFailedStatus(GenericReplicatedRecord record) {
		SupportedVersions version = PortHelper.getVersionByReplicatedRecord(record);

		switch (version) {

			case V10:
				((ish.oncourse.webservices.v10.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v10.stubs.replication.Status.FAILED);
				break;

			case V11:
				((ish.oncourse.webservices.v11.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v11.stubs.replication.Status.FAILED);
				break;

			case V12:
				((ish.oncourse.webservices.v12.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v12.stubs.replication.Status.FAILED);
				break;

			case V13:
				((ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v13.stubs.replication.Status.FAILED);
				break;

			case V14:
				((ish.oncourse.webservices.v14.stubs.replication.ReplicatedRecord) record).setStatus(
						ish.oncourse.webservices.v14.stubs.replication.Status.FAILED);
				break;

			default:
				throw new IllegalArgumentException("This stub version is not supported");
		}
	}
}
