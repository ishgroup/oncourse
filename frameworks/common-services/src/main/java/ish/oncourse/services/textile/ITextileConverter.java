package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

public interface ITextileConverter {
	String convert(String content, ValidationErrors errors);
}
