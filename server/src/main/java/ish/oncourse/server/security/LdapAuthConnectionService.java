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

package ish.oncourse.server.security;

import javax.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.ACLRole;
import ish.security.LdapAuthConnectionRequest;
import ish.security.LdapProperties;
import ish.security.TestLdapAuthConnection;
import org.apache.cayenne.query.ObjectSelect;

import javax.naming.Name;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

public class LdapAuthConnectionService {

    private static final String AUTHENTICATION_FAILED_MESSAGE = "User authentication failed.";
    private static final String AUTHENTICATION_SUCCESS_MESSAGE = "User authentication successful.";
    private static final String NO_ROLES_FOUND_MESSAGE = "No roles found for the given user.";
    private static final String LDAP_CONNECTION_SUCCESS = "LDAP server contacted successfully.";
    private static final String LDAP_CONNECTION_FAILED = "LDAP server connection test failed, please check your settings and try again.";

    @Inject
    private ICayenneService cayenneService;

    public String testLdapConnection(LdapProperties properties) {
        var ldapConnection = TestLdapAuthConnection.valueOf(properties);

        return ldapConnection.testLdapConnection() ? LDAP_CONNECTION_SUCCESS : LDAP_CONNECTION_FAILED;
    }

    public String testLdapAuthConnection(LdapAuthConnectionRequest request) throws NamingException {
        var ldapConnection = TestLdapAuthConnection.valueOf(request.getProperties());

        Name ldapUser = ldapConnection.findUser(request.getUserName());

        if (ldapUser == null) {
            return AUTHENTICATION_FAILED_MESSAGE;
        }

        if (request.isLdapAuthEnabled()) {
            List<String> ldapRoles = new ArrayList<>();

            var roles = ObjectSelect.query(ACLRole.class)
                    .select(cayenneService.getNewContext());

            for (var role : roles) {
                Name group = ldapConnection.findGroup(role.getName());
                if (request.isLdapGroupPosixStyle()) {
                    if (group != null && ldapConnection.authorisePosixUser(request.getUserName(), group)) {
                        ldapRoles.add(role.getName());
                    }
                } else {
                    if (group != null && ldapConnection.authorise(ldapUser, group)) {
                        ldapRoles.add(role.getName());
                    }
                }
            }

            if (ldapRoles.size() == 0) {
                return NO_ROLES_FOUND_MESSAGE;
            } else {
                var groups = "";
                for (var line : ldapRoles) {
                    groups = groups + line + "<BR>";
                }
                return "The following roles were found:<BR>" + groups;
            }
        } else {
            return AUTHENTICATION_SUCCESS_MESSAGE;
        }
    }
}
