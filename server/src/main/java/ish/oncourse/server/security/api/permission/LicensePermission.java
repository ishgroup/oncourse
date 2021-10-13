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

package ish.oncourse.server.security.api.permission;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.license.LicenseService;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Permission which checks license from preferences.
 */
public class LicensePermission extends ResourcePermission {

    private String code;
    private String errorMessage;

    public LicensePermission(String path, String method, String code, String errorMessage) {
        this.permissionKey = path;
        this.code = code;
        this.method = method;
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public PermissionCheckingResult check() {
        LicenseService licenseService = injector.getInstance(LicenseService.class);
        boolean result = false;

        if (code.startsWith("license.")) {
            var value = licenseService.getLisense(code);
            result = value != null && Boolean.parseBoolean(value.toString());
        } else {
            var cayenneService = injector.getInstance(ICayenneService.class);
            var context = cayenneService.getNewContext();
            var preference = ObjectSelect.query(Preference.class)
                    .where(Preference.UNIQUE_KEY.eq(code))
                    .and(Preference.VALUE_STRING.eq("true"))
                    .selectOne(context);
            if (preference != null) {
                result = true;
            }
        }

        return result ?  new PermissionCheckingResult(true) :
                new PermissionCheckingResult(false, errorMessage);
    }
}
