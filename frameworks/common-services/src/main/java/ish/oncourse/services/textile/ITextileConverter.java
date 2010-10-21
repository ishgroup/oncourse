package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

public interface ITextileConverter {
	String convertCustomTextile(String content, ValidationErrors errors);
	String convertCoreTextile(String content);
}
