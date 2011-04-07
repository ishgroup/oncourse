package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.persistence.ICayenneService;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BlockEdit {

	@Parameter(required = true)
	@Property
	private WebContent block;

	@Property
	@Persist
	private WebContent editBlock;

	@Parameter
	@Property
	private Zone updateZone;

	@Property
	@Component
	private Form blockEditForm;

	@Inject
	private ICayenneService cayenneService;

	private Action action;

	@SetupRender
	public void beforeRender() {
		editBlock = block.getPersistenceState() == PersistenceState.NEW ? block
				: (WebContent) cayenneService.newContext().localObject(block.getObjectId(), block);
	}

	Object onSubmitFromBlockEditForm() {
		ObjectContext ctx = editBlock.getObjectContext();
		switch (action) {
		case cancel:
			ctx.rollbackChanges();
			break;
		case delete:
			ctx.deleteObject(editBlock);
		case save:
			ctx.commitChanges();
		}
		blockEditForm.clearErrors();
		return updateZone.getBody();
	}

	void onSelectedFromCancel() {
		action = Action.cancel;
	}

	void onSelectedFromDelete() {
		action = Action.delete;
	}

	void onSelectedFromSave() {
		action = Action.save;
	}

	enum Action {
		save, cancel, delete
	}
}
