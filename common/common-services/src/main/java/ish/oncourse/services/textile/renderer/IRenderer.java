package ish.oncourse.services.textile.renderer;

import ish.oncourse.util.ValidationErrors;

public interface IRenderer {
	String render(String tag);
	ValidationErrors getErrors();
}
