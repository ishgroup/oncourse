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
package ish.oncourse.common;

import ish.oncourse.server.cayenne.AutomationTrait;
import ish.oncourse.server.cayenne.EmailTemplate;
import ish.oncourse.server.cayenne.ExportTemplate;
import ish.oncourse.server.cayenne.Import;
import ish.oncourse.server.cayenne.Report;
import ish.oncourse.server.cayenne.Script;

import javax.annotation.Nonnull;

/**
 * Enumeration containing all available default resource types.
 */
public enum ResourceType {

	MESSAGING("Message template", "messages/",".*\\.yaml", EmailTemplate.class),

	EXPORT("Export Template", "exports/", ".*\\.yaml", ExportTemplate.class),

	REPORT("Jasper Report", "reports/", ".*\\.jrxml", Report.class),

	SCRIPT("Script", "scripts/", ".*\\.yaml", Script.class),

	IMPORT("Import", "imports/", ".*\\.yaml", Import.class);

	private String displayName;
	private String resourcePath;
	private String filePattern;
	private Class<? extends AutomationTrait> cayenneClass;


	ResourceType(String displayName, String resourcePath, String filePattern, Class<? extends AutomationTrait> cayenneClass) {
		this.filePattern = filePattern;
		this.displayName = displayName;
		this.resourcePath = resourcePath;
		this.cayenneClass = cayenneClass;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Nonnull
	public String getResourcePath() {
		return resourcePath;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public Class<? extends AutomationTrait> getCayenneClass() {
		return cayenneClass;
	}

}
