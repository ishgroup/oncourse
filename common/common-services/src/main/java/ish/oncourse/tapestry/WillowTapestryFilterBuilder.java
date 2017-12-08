/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.tapestry;

import org.apache.tapestry5.ioc.def.ModuleDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tapestry5.internal.InternalConstants.TAPESTRY_APP_PACKAGE_PARAM;

/**
 * User: akoiro
 * Date: 8/12/17
 */
public class WillowTapestryFilterBuilder {

	private final List<ModuleDef> moduleDefs = new ArrayList<>();
	private final List<Class<? extends ModuleDef>> moduleDefClasses = new ArrayList<>();

	private final Map<String, String> parameters = new HashMap<>();

	public WillowTapestryFilterBuilder() {
	}

	public WillowTapestryFilterBuilder moduleDefClass(Class<? extends ModuleDef> mdClass) {
		moduleDefClasses.add(mdClass);
		return this;
	}

	public WillowTapestryFilterBuilder moduleDef(ModuleDef moduleDef) {
		moduleDefs.add(moduleDef);
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
		return new WillowTapestryFilter(moduleDefs, moduleDefClasses, parameters);
	}
}
