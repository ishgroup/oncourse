package ish.oncourse.admin.pages.college;

import java.util.Date;
import java.util.Map;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Instruction;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Instructions {
	
	@Property
	@Persist
	private College college;
	
	@Property
	private String instructionMessage;
	
	@Property
	private String selectedEntity;
	
	@Property
	@Persist
	private StringSelectModel entitySelectModel;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;
	
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
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
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
		
		College college = (College) context.localObject(this.college.getObjectId(), null);
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
}
