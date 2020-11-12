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

package ish.oncourse.server.security.api

import ish.oncourse.server.api.security.Permission
import static ish.oncourse.server.security.api.PermissionServiceFactory.ROOT_API
import ish.oncourse.server.security.api.permission.AdminApiPermission
import ish.oncourse.server.security.api.permission.ApiPermission
import ish.oncourse.server.security.api.permission.ChainPermission
import ish.oncourse.server.security.api.permission.LazyApiPermission
import ish.oncourse.server.security.api.permission.LicensePermission
import ish.oncourse.server.security.api.permission.OpenApiPermission
import ish.oncourse.server.security.api.permission.QueryParamBasedPermission
import ish.oncourse.server.security.api.permission.ResourcePermission
import static org.apache.commons.lang3.StringUtils.isNoneBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.HttpMethod
import javax.ws.rs.PATCH
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class ChainPermissionBuilder {

    private static final Logger LOGGER = LogManager.getLogger(ChainPermissionBuilder.class)

    private static final String LAZY_PREFIX = "LAZY/"
    private static final String UNDEFINED_KEY_CODE = 'UNDEFINED'
    private static final String ADMIN_PERMISSION_KEY_CODE = "ADMIN"
    private static final String SUB_CHAIN_DELIMITER = ';'
    private static final String CUSTOM_CHAIN_DELIMITER = ','
    private static final String SUB_CHAIN_ELEMENT_DELIMETER = '/'
    private static final byte SUB_CHAIN_PARAM_INDEX = 0
    private static final byte SUB_CHAIN_KEYCODE_INDEX = 1

    private static final String MESSAGE_FOR_EMPTY_PERMISSION_PART = "empty"

    private Method method
    private String path
    private String methodDef

    private ChainPermission chainPermission

    private ChainPermissionBuilder() {

    }

    static ChainPermissionBuilder valueOf(Method method) {
        ChainPermissionBuilder builder = new ChainPermissionBuilder()
        builder.method = method
        builder.path = ROOT_API + method.getAnnotation(Path.class).value()
        builder.methodDef = getMethodTypeDefinition(method)
        builder.chainPermission = new ChainPermission(builder.path, builder.methodDef)
        builder
    }

    ResourcePermission getPermission() {
        chainPermission
    }

    /**
     * Creates main permission that formatted from yaml root level fields: keyCode, mask and errorMessage
     * @param method that will be analyzed
     * @return permission instance that will call first
     */
    ChainPermissionBuilder initMainPermission() {
        String mask = method.getAnnotation(Permission.class).mask()
        String keyCode = method.getAnnotation(Permission.class).keyCode()
        String errorMessage = method.getAnnotation(Permission.class).errorMessage()

        ResourcePermission permission
        if (keyCode.equals(UNDEFINED_KEY_CODE)) {
            permission = new OpenApiPermission(path, methodDef)
        } else if (keyCode.equals(ADMIN_PERMISSION_KEY_CODE)) {
            permission = new AdminApiPermission()
        } else if (keyCode.startsWith(LAZY_PREFIX)) {
            permission = new LazyApiPermission(path, methodDef, mask, keyCode, errorMessage)
        } else {
            permission = new ApiPermission(path, methodDef, mask, keyCode, errorMessage)
        }
        LOGGER.info("Added API permission {}:{} for {}:{}",
                mask != null ? mask : MESSAGE_FOR_EMPTY_PERMISSION_PART,
                keyCode != null ? keyCode : MESSAGE_FOR_EMPTY_PERMISSION_PART,
                path,
                methodDef)

        chainPermission.addPermission(permission)

        this
    }

    ChainPermissionBuilder initLicensePermission() {
        String licenseCode = method.getAnnotation(Permission.class).licenseCode()
        String licenseErrorMessage = method.getAnnotation(Permission.class).licenseErrorMessage()

        if (isNoneBlank(licenseCode, licenseErrorMessage)) {
            ResourcePermission permission = new LicensePermission(path, methodDef, licenseCode, licenseErrorMessage)

            LOGGER.info("Added LICENSE permission on $licenseCode for $path:$methodDef")

            chainPermission.addPermission(permission)
        }

        this
    }

    ChainPermissionBuilder initSubChain() {
        String subChainDefinition = method.getAnnotation(Permission.class).chain()

        if (isNotBlank(subChainDefinition)) {
            String errorMessage = method.getAnnotation(Permission.class).errorMessage()

            subChainDefinition.split(SUB_CHAIN_DELIMITER).findAll { isNotBlank(it) }.each { String element ->
                String[] elementPieces = element.split(SUB_CHAIN_ELEMENT_DELIMETER)
                if (elementPieces.length != 2) {
                    throw new RuntimeException("Invalid sub chain definition")
                }
                String param = elementPieces[SUB_CHAIN_PARAM_INDEX]
                String keyCodeDef = elementPieces[SUB_CHAIN_KEYCODE_INDEX]
                QueryParamBasedPermission permission = new QueryParamBasedPermission(path, methodDef, param, keyCodeDef, errorMessage)
                chainPermission.addPermission(permission)
                LOGGER.info("Added QUERY-BASED permission {}:{} for {}:{}", param, keyCodeDef, path, methodDef)
            }
        }

        this
    }

    ChainPermissionBuilder initCustomPermissions() {
        String custom = method.getAnnotation(Permission.class).custom()
        if (isNotBlank(custom)) {
            custom.split(CUSTOM_CHAIN_DELIMITER).each { it ->
                try {
                    Class<?> clazz = Class.forName(it)
                    Constructor<?> constructor = clazz.getConstructor(String, String)
                    ResourcePermission permission = constructor.newInstance(path, methodDef) as ResourcePermission

                    chainPermission.addPermission(permission)
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Class not found by name $it", e)
                    throw new RuntimeException("Class not found by name $it")
                } catch (Exception e) {
                    LOGGER.error("Cannot initialize class $it", e)
                    throw new RuntimeException("Cannot initialize class $it")
                }
            }

        }

        this
    }


    /**
     * Gets string definition of HTTP method.
     * @param method which will be analyzed.
     * @return string defenition of HTTP method.
     */
    private static String getMethodTypeDefinition(Method method) {
        if (method.getAnnotation(GET.class)) {
            return HttpMethod.GET
        }
        if (method.getAnnotation(POST.class)) {
            return HttpMethod.POST
        }
        if (method.getAnnotation(PUT.class)) {
            return HttpMethod.PUT
        }
        if (method.getAnnotation(DELETE.class)) {
            return HttpMethod.DELETE
        }
        if (method.getAnnotation(PATCH.class)) {
            return HttpMethod.PATCH
        }
        throw new RuntimeException("Illegal HTTP method in " + method)
    }
}
