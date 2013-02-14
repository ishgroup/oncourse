package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

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

	@Inject
	private ITextileConverter textileConverter;

	private Action action;

	@InjectPage
	private Page page;

	@Inject
	private Request request;

	@SetupRender
	public void beforeRender() {
		editBlock = block.getPersistenceState() == PersistenceState.NEW ? block : (WebContent) cayenneService
				.newContext().localObject(block.getObjectId(), block);
	}

	Object onSubmitFromBlockEditForm() {
		//#14616
		//if the session expired or the context for edit block were detached, we need to leave the page without process of required action.
		if (request.getSession(false) == null || editBlock == null || editBlock.getObjectContext() == null) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = editBlock.getObjectContext();
		switch (action) {
		case cancel:
			ctx.rollbackChanges();
			break;
		case delete:
			ctx.deleteObject(editBlock);
			ctx.commitChanges();
			break;
		case save:
			editBlock.setContent(textileConverter.convertCoreTextile(editBlock.getContentTextile()));
			ctx.commitChanges();
			break;
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
