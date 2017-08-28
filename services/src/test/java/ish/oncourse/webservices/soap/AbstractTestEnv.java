/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

/**
 * User: akoiro
 * Date: 28/8/17
 */
public abstract class AbstractTestEnv<TransportConfig> {
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
	private TestEnv testEnv;
	private TransportConfig transportConfig;
	private org.apache.tapestry5.ioc.Messages messages;


}
