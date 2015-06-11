package ish.oncourse.cms.webdav;

import ish.oncourse.services.mail.EmailBuilder;

import java.io.File;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public interface ICompiler {
	void compile();
	File getResult();
	File getGzResult();
	List<String> getErrors();
	ErrorEmailTemplate getErrorEmailTemplate();
}
