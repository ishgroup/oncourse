package ish.oncourse.ui.services;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.StylesheetLink;

import java.util.Collections;
import java.util.List;

public class DisableJavaScriptStack implements JavaScriptStack {
	@Override
	public List<String> getStacks() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Asset> getJavaScriptLibraries() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<StylesheetLink> getStylesheets() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getInitialization() {
		return null;
	}
}
