package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.util.GenericInstructionStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.List;

public interface IInstructionService {
	void confirmExecution(Long instrucitonId, String response);
	
	List<GenericInstructionStub> getInstructions(final SupportedVersions version);
}
