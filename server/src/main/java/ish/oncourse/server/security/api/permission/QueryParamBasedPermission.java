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

import io.bootique.jetty.servlet.AngelServletEnvironment;
import ish.common.types.KeyCode;
import ish.common.types.Mask;
import ish.oncourse.server.security.api.IPermissionService;

/**
 * Permission which checks permission based on query parameter value.
 */
public class QueryParamBasedPermission extends ResourcePermission {

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String SINGLE_QUERY_PARAM_DELIMITER = "=";
    private static final String ACTIVE_PARAM_VALUE = "true";
    private static final String NON_ACTIVE_PARAM_VALUE = "false";

    private String param;
    private KeyCode keyCode;
    private String errorMessage;
    private String reserveQueryString;

    public QueryParamBasedPermission(String path, String method, String param, String keyCodeDef, String errorMessage) {
        this.permissionKey = path;
        this.param = param;
        this.method = method;
        this.keyCode = KeyCode.valueOf(keyCodeDef);
        this.errorMessage = errorMessage;
    }

    @Override
    public PermissionCheckingResult check() {
        var permissionService = injector.getInstance(IPermissionService.class);
        if (permissionService == null) {
            return new PermissionCheckingResult(false, "Permission service wasn't initialized!");
        }
        var queryString = getQueryString();
        if (queryString == null) {
            return new PermissionCheckingResult(false, "Empty query parameters!");
        }
        var params = queryString.split(QUERY_PARAM_DELIMITER);
        for (var queryParam : params) {
            var paramValues = queryParam.split(SINGLE_QUERY_PARAM_DELIMITER);
            var paramName = paramValues[0];
            var paramValue = paramValues[1];
            if ((paramName.equals(this.param)) && (paramValue.equals(ACTIVE_PARAM_VALUE))) {
                var userCan = permissionService.currentUserCan(keyCode, Mask.VIEW);
                if (!userCan) {
                    return new PermissionCheckingResult(false, errorMessage);
                } else {
                    return new PermissionCheckingResult(true);
                }
            } else if ((paramName.equals(this.param)) && (paramValue.equals(NON_ACTIVE_PARAM_VALUE))) {
                new PermissionCheckingResult(false, errorMessage);
            }
        }
        return new PermissionCheckingResult(true);
    }

    private String getQueryString() {
        var environment = injector.getInstance(AngelServletEnvironment.class);
        if (!environment.request().isPresent()) {
            return null;
        }
        var request = environment.request().get();
        var queryString = request.getQueryString();
        // if query params is null, try to get it from replaced
        // this situation can be, if we will get path from /v1/access path
        // there path will be in request body
        if ((queryString == null) && (reserveQueryString != null)) {
            return reserveQueryString;
        }
        return queryString;
    }

    public String getParam() {
        return param;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setReserveQueryString(String reserveQueryString) {
        this.reserveQueryString = reserveQueryString;
    }
}
