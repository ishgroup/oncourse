/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.selenium.service.shell.command;

import java.io.File;

public class DecodeReactErrorCommand {

    private final String bundleName;

    private final String sourceMapName;

    private final String errorLocation;

    public DecodeReactErrorCommand(String bundleName, String sourceMapName, String errorLocation) {
        this.bundleName = bundleName;
        this.sourceMapName = sourceMapName;
        this.errorLocation = errorLocation;
    }

    public String getCommand() {
        String resourceFolder = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath() +
                "/server/build/resources/main/static/";

        return String.join(" ",
                "npx",
                "@fatso83/stacktrace-cli",
                resourceFolder + bundleName,
                resourceFolder + sourceMapName,
                errorLocation
        );
    }
}
