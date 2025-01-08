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

package ish.oncourse.server.security.api.permission

import io.bootique.di.Injector
import groovy.transform.CompileStatic

/**
 * Represents chain of resourcePermissions that may be executed.
 * Execution on every chain element checks permission by his own logic.
 */

@CompileStatic
class ChainPermission extends ResourcePermission {

    private List<ResourcePermission> resourcePermissions

    ChainPermission(String chainKey, String method) {
        // Here, chain key is API path
        setPermissionKey(chainKey)
        setMethod(method)
        this.resourcePermissions = new ArrayList<>()
    }

    @Override
    PermissionCheckingResult check() {
        this.resourcePermissions.collect { it.check() }.findAll { !it.successful }[0] ?: PermissionCheckingResult.successful()
    }

    void addPermission(ResourcePermission resourcePermission) {
        this.resourcePermissions.add(resourcePermission)
    }

    List<ResourcePermission> getResourcePermissions() {
        return resourcePermissions
    }

    @Override
    void setInjector(Injector injector) {
        this.resourcePermissions.each { it.injector = injector }

        super.setInjector(injector)
    }
}
