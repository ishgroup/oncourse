package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.Instruction;
import ish.oncourse.model.InstructionParameter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.v4.stubs.InstructionStub;
import ish.oncourse.webservices.v4.stubs.InstructionTypeStub;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class InstructionPortTypeImpl implements InstructionPortType {

	@Inject
	@Autowired
	private IWebSiteService webSiteService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	@Override
	public List<InstructionStub> getInstructions() {

		List<InstructionStub> result = new ArrayList<InstructionStub>();

		Expression expr = ExpressionFactory.matchExp(Instruction.COLLEGE_PROPERTY, webSiteService.getCurrentCollege());

		SelectQuery q = new SelectQuery(Instruction.class, expr);
		q.addPrefetch(Instruction.PARAMETERS_PROPERTY);

		ObjectContext objectContext = cayenneService.sharedContext();

		List<Instruction> list = objectContext.performQuery(q);

		for (Instruction inst : list) {
			InstructionStub stub = new InstructionStub();

			stub.setId(inst.getId());
			stub.setType(InstructionTypeStub.valueOf(inst.getType().name()));

			for (InstructionParameter param : inst.getParameters()) {
				stub.getParameters().put(param.getName(), param.getValue());
			}

			result.add(stub);
		}

		return result;
	}

	@Override
	public void confirmExecution(Long instrucitonId) {
		Expression expr = ExpressionFactory.matchExp(Instruction.COLLEGE_PROPERTY, webSiteService.getCurrentCollege());
		expr = expr.andExp(ExpressionFactory.matchDbExp(Instruction.ID_PK_COLUMN, instrucitonId));
		SelectQuery q = new SelectQuery(Instruction.class, expr);

		ObjectContext objectContext = cayenneService.newContext();

		List<Instruction> list = objectContext.performQuery(q);

		objectContext.deleteObjects(list);

		objectContext.commitChanges();
	}
}
