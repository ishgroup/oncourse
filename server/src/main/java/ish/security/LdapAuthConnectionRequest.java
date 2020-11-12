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

package ish.security;

import java.io.Serializable;

/**
 * Created by anarut on 12/8/16.
 */
public class LdapAuthConnectionRequest implements Serializable {

    private String userName;
    private LdapProperties properties;
    private boolean ldapAuthEnabled;
    private boolean ldapGroupPosixStyle;

    private LdapAuthConnectionRequest() {

    }

    public static LdapAuthConnectionRequest valueOf(String userName, LdapProperties properties, boolean ldapAuthEnabled, boolean ldapGroupPosixStyle) {
        LdapAuthConnectionRequest request = new LdapAuthConnectionRequest();
        request.userName = userName;
        request.properties = properties;
        request.ldapAuthEnabled = ldapAuthEnabled;
        request.ldapGroupPosixStyle = ldapGroupPosixStyle;
        return request;
    }

    public String getUserName() {
        return userName;
    }

    public LdapProperties getProperties() {
        return properties;
    }

    public boolean isLdapAuthEnabled() {
        return ldapAuthEnabled;
    }

    public boolean isLdapGroupPosixStyle() {
        return ldapGroupPosixStyle;
    }
}
