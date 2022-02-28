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

import io.bootique.di.Injector;
import ish.validation.AngelUriTemplatePathSpec;


/**
 * Base class for all types of permissions, e.g. ApiPermission, LicensePermission and so on.
 * Now permission can be 3 types:
 * - Main permission (ApiPermission and classes which extends him) which represents main permission checking.
 * - License permission (LicensePermission) which represents permission checking based on licenses from preferences.
 * - Chain of secondary permissions () which represents permission checking based on secondary query params.
 */
public abstract class ResourcePermission {

    // Permission key is unique key for that permission, for example, it can be path to API
    String permissionKey;
    String method;

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    Injector injector;

    public abstract PermissionCheckingResult check();

    public boolean isUsingSamePath(String method, String permissionKey) {
        AngelUriTemplatePathSpec current = new AngelUriTemplatePathSpec(this.permissionKey);
        return this.getMethod().equals(method) && (current.matches(permissionKey));
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public String getMethod() {
        return method;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public Injector getInjector() {
        return this.injector;
    }
}
