package ish.oncourse.portal.usi.handler;

import ish.oncourse.portal.usi.Result;
import ish.oncourse.portal.usi.Value;

import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public abstract class AbstractValueHandler<T> {
	T entity;
	String key;
	Map<String, Value> inputValues;

	Result result = new Result();

	public abstract void handle();
}
