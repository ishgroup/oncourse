package ish.oncourse.enrol.components;

import org.apache.tapestry5.annotations.Parameter;

public class Delegate {
	@Parameter(required = true, cache = false)
	private Object to;

	Object beginRender()
	{
		return to;
	}
}
