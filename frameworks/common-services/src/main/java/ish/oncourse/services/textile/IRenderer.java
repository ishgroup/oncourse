package ish.oncourse.services.textile;

import org.apache.cayenne.ObjectContext;

import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

public interface IRenderer {

	String render(String tag, ValidationErrors errors, ObjectContext sharedContext, College currentCollege);

}
