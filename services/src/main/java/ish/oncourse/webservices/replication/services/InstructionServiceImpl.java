package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Instruction;
import ish.oncourse.model.InstructionParameter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.util.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

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
	public void confirmExecution(Long instrucitonId, String response) {
		Expression expr = ExpressionFactory.matchDbExp(Instruction.ID_PK_COLUMN, instrucitonId);
		SelectQuery q = new SelectQuery(Instruction.class, expr);
		ObjectContext objectContext = takeCayenneService().newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<Instruction> list = objectContext.performQuery(q);		
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
		Expression expr = ExpressionFactory.matchExp(Instruction.COLLEGE_PROPERTY, takeWebSiteService().getCurrentCollege()).andExp(
			ExpressionFactory.matchExp(Instruction.EXECUTED_PROPERTY, null));
		SelectQuery q = new SelectQuery(Instruction.class, expr);
		q.addPrefetch(Instruction.PARAMETERS_PROPERTY);
		ObjectContext objectContext = takeCayenneService().newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<Instruction> list = objectContext.performQuery(q);
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
