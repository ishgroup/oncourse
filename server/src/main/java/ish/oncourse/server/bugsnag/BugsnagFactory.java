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

package ish.oncourse.server.bugsnag;

import com.bugsnag.Bugsnag;
import io.bootique.annotation.BQConfigProperty;
import ish.oncourse.common.ResourcesUtil;

public class BugsnagFactory {

    private String api;
    private String  collegeKey;

    @BQConfigProperty
    public void setApi(String api) {
        this.api = api;
    }

    public BugsnagFactory collegeKey(String collegeKey) {
        this.collegeKey = collegeKey;
        return this;
    }

    public void init() {
        String version = ResourcesUtil.getReleaseVersionString();

        if (collegeKey != null && api != null && !version.matches(".*[a-zA-Z].*")) {
            Bugsnag bugsnag = new Bugsnag(api);
            bugsnag.setAppVersion(version);
            bugsnag.addCallback((report) -> report.addToTab("user", "college", this.collegeKey));
        }
    }



}
