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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.api.v1.model.UserPreferenceDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.UserPreferenceApi
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.preference.UserPreferenceService
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.services.ISystemUserService

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.preference.UserPreferenceService.JOIN_DELIMETER

@CompileStatic
class UserPreferenceApiImpl implements UserPreferenceApi {

    @Inject
    private UserPreferenceService usePrefService

    @Inject
    private PreferenceController systemPrefService

    @Inject
    private LicenseService licenseService

    @Inject
    private ISystemUserService userService

    @Inject
    private IPermissionService permissionService

    @Override
    Map<String, String> get(String keys) {
        def throwException = { key ->
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "key: $key is not in user preferences list")).build())
        }

        Map<String, String> value =
                keys.split(JOIN_DELIMETER)
                        .collect { PreferenceEnumDTO.fromValue(it) ?: throwException.call(it) }
                        .collectEntries { [ (it.toString()) : usePrefService.get(it as PreferenceEnumDTO) ] }

        keys.split(JOIN_DELIMETER)
                .collect { PreferenceEnumDTO.fromValue(it) ?: throwException.call(it) }
                .collectEntries { [ (it.toString()) : usePrefService.get(it as PreferenceEnumDTO) ] }
    }

    @Override
    void set(UserPreferenceDTO preference) {
        usePrefService.set(preference.key, preference.value)
    }
}
