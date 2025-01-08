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

package ish.oncourse.server.security.api;

import io.bootique.di.Injector;
import ish.oncourse.server.api.security.Permission;
import ish.oncourse.server.security.api.permission.OpenApiPermission;
import ish.oncourse.server.security.api.permission.ResourcePermission;
import ish.oncourse.server.services.ISystemUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for all IPermissionService implementations.
 */
public class PermissionServiceFactory {

    public static final String ROOT_API = "/a";

    private static final Logger LOGGER = LogManager.getLogger(PermissionServiceFactory.class);

    private String apiPackage;

    public IPermissionService createPermissionService(Injector injector) {
        return new DefaultPermissionService(loadPermissions(injector), injector.getInstance(ISystemUserService.class));
    }

    /**
     * Creates map of permissions ({@see ish.oncourse.server.security.api.ApiPermission})
     * where key - is path like in @Path annotation, value - ApiPermission object,
     * which created based on @Permission, @Path and @{HttpMethod} annotations.
     * @return map of permissions.
     */
    private List<ResourcePermission> loadPermissions(Injector injector) {
        List<ResourcePermission> permissions = new ArrayList<>();
        LOGGER.info("Loading permissions...");
        var reflections = new Reflections(apiPackage, new MethodAnnotationsScanner());
        reflections
                .getMethodsAnnotatedWith(Permission.class)
                .stream()
                .filter(method -> method.getAnnotation(Path.class) != null)
                .map(this::createChainPermission)
                .forEach(permissions::add);
        LOGGER.info("Was loaded {} permissions.", permissions.size());

        permissions.add(new OpenApiPermission(ROOT_API +"/", HttpMethod.GET));
        permissions.forEach( p -> p.setInjector(injector));
        return permissions;
    }

    private ResourcePermission createChainPermission(Method method) {
        return ChainPermissionBuilder
                .valueOf(method)
                .initMainPermission()
                .initLicensePermission()
                .initSubChain()
                .initCustomPermissions()
                .getPermission();
    }

    public String getApiPackage() {
        return apiPackage;
    }

    public void setApiPackage(String apiPackage) {
        this.apiPackage = apiPackage;
    }
}
