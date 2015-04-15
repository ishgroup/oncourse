package ish.oncourse.admin.pages.college;

import ish.common.types.EntityMapping;
import ish.oncourse.admin.pages.Index;
import ish.oncourse.model.College;
import ish.oncourse.model.Instruction;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Instructions {

	public static final String SUPPORTED_INSTRUCTIONS = "Available complex instructions: " +
		"queue:AttendanceForClass:class_angelId (for angel >=4.1 version), " +
		"queue:AttendanceForEnrolment:enrolment_angelId (for angel >=4.1 version), " +
		"queue:instructWithRelationships:PaymentIn:paymentIn_angelId (for angel >=4.0 version), " +
		"queue:instructWithRelationships:Invoice:invoice_angelId (for angel >=4.0 version), " +
		"queue:instructWithRelationships:Enrolment:enrolment_angelId (for angel >=4.0 version)\n";
	@Property
	private College college;

	@Property
	private String instructionMessage;

	@Property
	private String selectedEntity;

	@Property
	@Persist
	private StringSelectModel entitySelectModel;

	@Property
	private List<Instruction> lastInstructions;

	@Property
	private Instruction currentInstruction;

	@Inject
	private ICollegeService collegeService;

	@Inject
	private ICayenneService cayenneService;

    @InjectPage
    private Index indexPage;

	@SetupRender
	void setupRender() {
		Map<String, ObjEntity> entities = cayenneService.sharedContext()
				.getEntityResolver().getDataMap("oncourse")
				.getObjEntityMap();

		String[] entitiesArray = new String[entities.size()];
		int i = 0;

		for (String key : entities.keySet()) {
			entitiesArray[i] = entities.get(key).getName();
			i++;
		}

		this.entitySelectModel = new StringSelectModel(entitiesArray);

		ObjectContext context = cayenneService.sharedContext();
		College college = context.localObject(this.college);
		if (college != null) {
			lastInstructions = ObjectSelect.query(Instruction.class).
					where(Instruction.COLLEGE.eq(college)).
					orderBy(Instruction.CREATED.desc()).
					limit(5).
					select(context);
		}
	}

	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
	}

    @OnEvent(component="resetAngelQueueForm", value="success")
	void resetAngelQueue() {
		createInstruction("queue:resetRetries");
	}

	@OnEvent(component="addInstructionForm", value="success")
	void addInstruction() {
		if (instructionMessage != null) {
			createInstruction(instructionMessage);
		}
	}

	@OnEvent(component="queueEntityForm", value="success")
	void queueEntity() {
		createInstruction("queue:" +
				EntityMapping.getAngelEntityIdentifer(selectedEntity));
	}

	private void createInstruction(String message) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		College college = context.localObject(this.college);
		if (college != null) {
			Date now = new Date();

			Instruction instruction = context.newObject(Instruction.class);
			instruction.setCollege(college);
			instruction.setCreated(now);
			instruction.setModified(now);

			instruction.setMessage(message);

			context.commitChanges();
		}
	}

    public Object onException(Throwable cause){
        //redirect to index page when session was expired and persist properties got null value
        if (college == null || entitySelectModel == null)
            return indexPage;
        else throw new IllegalStateException(cause);
    }

	public String getListOfInstructions() {
		return SUPPORTED_INSTRUCTIONS;
	}
}
