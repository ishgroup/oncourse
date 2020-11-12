/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class GoogleGuiceInjector {

	private static Injector injector;

	@Inject
	public GoogleGuiceInjector(Injector injector) {
		GoogleGuiceInjector.injector = injector;
	}

	public static Injector getInstance() {
		return injector;
	}
}
