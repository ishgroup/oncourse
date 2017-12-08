/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.tapestry;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.def.ModuleDef;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

import static org.apache.tapestry5.SymbolConstants.PRODUCTION_MODE;
import static org.apache.tapestry5.internal.InternalConstants.TAPESTRY_APP_PACKAGE_PARAM;

/**
 * User: akoiro
 * Date: 8/12/17
 */
public class WillowTapestryFilterBuilder {

	private final List<ModuleDef> modulueDefs = new ArrayList<>();

	private final Map<String, String> parameters = new HashMap<>();

	public WillowTapestryFilterBuilder() {
	}

	public WillowTapestryFilterBuilder moduleDef(ModuleDef moduleDef) {
		modulueDefs.add(moduleDef);
		return this;
	}

	public WillowTapestryFilterBuilder appPackage(String appPackage) {
		parameters.put(TAPESTRY_APP_PACKAGE_PARAM, appPackage);
		return this;
	}

	public WillowTapestryFilterBuilder initParam(String key, String value) {
		parameters.put(key, value);
		return this;
	}

	public WillowTapestryFilter build() {
		return new WillowTapestryFilter(modulueDefs.toArray(new ModuleDef[]{}), parameters);
	}
}
