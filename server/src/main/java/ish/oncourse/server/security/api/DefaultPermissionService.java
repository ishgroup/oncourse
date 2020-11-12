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

import com.fasterxml.jackson.databind.ObjectMapper;
import ish.common.types.KeyCode;
import ish.oncourse.server.api.v1.model.ValidationErrorDTO;
import ish.oncourse.server.cayenne.ACLAccessKey;
import ish.oncourse.server.cayenne.ACLRole;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.security.api.permission.*;
import ish.oncourse.server.services.ISystemUserService;
import org.apache.xerces.util.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default permission service which checks permission based on Permission map.
 */
class DefaultPermissionService implements IPermissionService {

    private ISystemUserService systemUserService;
    private List<ResourcePermission> permissionList;

    DefaultPermissionService(List<ResourcePermission> permissionList, ISystemUserService systemUserService) {
        this.systemUserService = systemUserService;
        this.permissionList = permissionList;
    }

    @Override
    public boolean authorize(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        var requestURI = servletRequest.getRequestURI();
        var method = servletRequest.getMethod();
        var permission = getPermissionByPath(requestURI, method);
        if (permission != null) {
            var result = permission.check();
            if (!result.isSuccessful()) {
                if (result.getErrorMessage().equals(PermissionCheckingResult.EMPTY_ERROR_MESSAGE)) {
                    // if error message isn't presented - send standard Jetty 403 page
                    servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                } else {
                    // Return JSON with error to client instead standard Jetty 403 error page
                    var error = new ValidationErrorDTO(null, null, result.getErrorMessage());
                    var mapper = new ObjectMapper();
                    servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    servletResponse.setContentType("application/json");
                    servletResponse.getWriter().write(mapper.writeValueAsString(error));
                    return false;
                }
            }
            return true;
        } else {
            if (!systemUserService.getCurrentUser().getIsAdmin()) {
                servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public PermissionCheckingResult hasAccess(String path, String method) {
        var pathPieces = path.split("\\?");
        var uri = pathPieces[0];
        var permission = getPermissionByPath(uri, method);
        if (permission == null) {
            return new PermissionCheckingResult(false, "Haven't configured permission for this path.");
        }
        var hasQueryParams = pathPieces.length == 2;

        // set reserve query
        if (hasQueryParams) {
            injectReserveQuery(permission, pathPieces[1]);
        }

        var result = permission.check();

        // return reserve query string to null
        if (hasQueryParams) {
            injectReserveQuery(permission, null);
        }
        return result;
    }

    /**
     * Injects reserve query into permission, it's need when we can't get query params from servlet request
     * @param permission that will traversed
     */
    private void injectReserveQuery(ResourcePermission permission, String value) {
        if (permission instanceof ChainPermission) {
            for (var perm : ((ChainPermission) permission).getResourcePermissions()) {
                if (perm instanceof LazyApiPermission) {
                    ((LazyApiPermission) perm).setReserveQueryString(value);
                } else if (perm instanceof QueryParamBasedPermission) {
                    ((QueryParamBasedPermission) perm).setReserveQueryString(value);
                }
            }
        }
    }

    @Override
    public boolean currentUserCan(KeyCode keyCode, int mask) {
        return userCan(systemUserService.getCurrentUser(), keyCode, mask);
    }

    @Override
    public boolean userCan(SystemUser currentUser, KeyCode keyCode, int mask) {
        if (keyCode == null) {
            return false;
        }

        if (currentUser == null) {
            return false;
        }

        // admin can anything
        if (currentUser.getIsAdmin()) {
            return true;
        }


        var key = getAccessKeys(currentUser).get(keyCode);


        var currentMask = key == null ? keyCode.getDefaultValueMask() : key.getMask();

        return (currentMask & mask) == mask;
    }


    private Map<KeyCode, ACLAccessKey> getAccessKeys(SystemUser currentUser) {
        Map<KeyCode, ACLAccessKey> accessKeys = new LinkedHashMap<>();
        for (var role : currentUser.getAclRoles()) {
            for (var key : role.getAccessKeys()) {
                accessKeys.put(key.getKeycode(), key);
            }
        }
        return accessKeys;
    }

    /**
     * Returns ResourcePermission instance for concrete path.
     * @param path which will be used as key.
     * @param method which will be used as key.
     * @return ResourcePermission if it exists for path, null if doesn't exist.
     */
    private ResourcePermission getPermissionByPath(String path, String method) {
        var permissions = permissionList.stream()
                .filter(permission -> permission.isUsingSamePath(method, path))
                .collect(Collectors.toList());
        if (permissions == null || permissions.isEmpty()) {
            return null;
        } else if (permissions.size() == 1) {
            return permissions.get(0);
        } else {
            return permissions.stream().filter(it -> it.getPermissionKey().equalsIgnoreCase(path)).findFirst().orElse(permissions.get(permissions.size()-1));
        }
    }
}
