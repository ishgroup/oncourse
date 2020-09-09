package ish.oncourse.services;

import ish.oncourse.util.ValidationErrors;

public interface IReachtextConverter {
	String convertCustomText(String content, ValidationErrors errors);
	String convertCoreText(String content);
}
