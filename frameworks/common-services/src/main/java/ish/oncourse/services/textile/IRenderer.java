package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

public interface IRenderer {

	String render(String tag, ValidationErrors errors);

}
