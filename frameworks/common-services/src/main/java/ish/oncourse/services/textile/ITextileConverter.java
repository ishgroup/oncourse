package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationException;

public interface ITextileConverter {
	String convert(String content) throws ValidationException;
}
