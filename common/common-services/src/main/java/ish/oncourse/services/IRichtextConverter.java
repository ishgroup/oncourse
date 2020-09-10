package ish.oncourse.services;

import ish.oncourse.util.ValidationErrors;

public interface IRichtextConverter {
	String convertCustomText(String content, ValidationErrors errors);
	String convertCoreText(String content);
}
