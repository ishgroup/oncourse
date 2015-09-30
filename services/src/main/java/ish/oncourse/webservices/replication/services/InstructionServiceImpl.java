package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Instruction;
import ish.oncourse.model.InstructionParameter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.PrefetchTreeNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstructionServiceImpl implements IInstructionService {
	
	private ICayenneService cayenneService;
	
	private IWebSiteService webSiteService;
	
	public InstructionServiceImpl(ICayenneService cayenneService, IWebSiteService webSiteService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
	}

	public InstructionServiceImpl() {}

	/**
	 * @return the cayenneService
	 */
	public ICayenneService takeCayenneService() {
		return cayenneService;
	}

	/**
	 * @return the webSiteService
	 */
	public IWebSiteService takeWebSiteService() {
		return webSiteService;
	}

	@Override
	public void confirmExecution(Long instructionId, String response) {
		ObjectContext objectContext = takeCayenneService().newNonReplicatingContext();

		List<Instruction> list = ObjectSelect.query(Instruction.class)
				.where(ExpressionFactory.matchDbExp(Instruction.ID_PK_COLUMN, instructionId))
				.select(objectContext);

		if (!list.isEmpty()) {
			Instruction instruction = list.get(0);
			instruction.setExecuted(new Date());
			instruction.setResponse(response);
			objectContext.commitChanges();
		}
	}

	@Override
	public List<GenericInstructionStub> getInstructions(SupportedVersions version) {
		List<GenericInstructionStub> result = new ArrayList<>();

		ObjectContext objectContext = takeCayenneService().newNonReplicatingContext();

		List<Instruction> list = ObjectSelect.query(Instruction.class)
				.where(Instruction.COLLEGE.eq(takeWebSiteService().getCurrentCollege()))
				.and(Instruction.EXECUTED.isNull())
				.addPrefetch(Instruction.PARAMETERS.getName(), PrefetchTreeNode.UNDEFINED_SEMANTICS)
				.select(objectContext);

		for (Instruction inst : list) {
			GenericInstructionStub stub = PortHelper.createInstructionStub(version);
			stub.setId(inst.getId());
			stub.setMessage(inst.getMessage());
			if (!inst.getParameters().isEmpty()) {
				GenericParametersMap paramMap = PortHelper.createParametersMap(version);
				for (InstructionParameter param : inst.getParameters()) {
					GenericParameterEntry entry = PortHelper.createParameterEntry(version);
					entry.setName(param.getName());
					entry.setValue(param.getValue());
					paramMap.getGenericEntry().add(entry);
				}
				StubUtils.setInstructionParameters(stub, paramMap);
			}
			result.add(stub);			
			//set executed to prevent execution during next replication run, 
			//even if session timed out.
			inst.setExecuted(new Date());
		}
		objectContext.commitChanges();
		return result;
	}

}
