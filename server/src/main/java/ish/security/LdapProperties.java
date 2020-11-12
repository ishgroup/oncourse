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

import ish.persistence.CommonPreferenceController;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Created by akoira on 11/4/16.
 */
public class LdapProperties implements Serializable {
    private String host;
    private int port;
    private boolean useSSL;
    private String baseDN;
    private String usernameAttribute;
    private String bindUserDN;
    private String bindUserPass;
    private String groupMemberAttribute;
    private String groupAttribute;
    private String groupSearchFilter;
    private String userSearchFilter;


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public String getUsernameAttribute() {
        return usernameAttribute;
    }

    public String getBindUserDN() {
        return bindUserDN;
    }

    public String getBindUserPass() {
        return bindUserPass;
    }

    public String getGroupMemberAttribute() {
        return groupMemberAttribute;
    }

    public String getGroupAttribute() {
        return groupAttribute;
    }

    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }

    public String getUserSearchFilter() {
        return userSearchFilter;
    }

    public static LdapProperties valueOf(CommonPreferenceController preferenceController) {
        return valueOf(preferenceController.getLdapHost(),
                preferenceController.getLdapServerport(),
                BooleanUtils.isTrue(preferenceController.getLdapSSL()),
                preferenceController.getLdapBaseDN(),
                preferenceController.getLdapUsernameAttribute(),
                preferenceController.getLdapBindUserDn(),
                preferenceController.getLdapBindUserPass(),
                preferenceController.getLdapGroupMemberAttribute(),
                preferenceController.getLdapGroupAttribute(),
                preferenceController.getLdapGroupSearchFilter(),
                preferenceController.getLdapUserSearchFilter());
    }

    public static LdapProperties valueOf(String host,
                                         int port,
                                         boolean useSSL,
                                         String baseDN,
                                         String usernameAttribute,
                                         String bindUserDN,
                                         String bindUserPass,
                                         String groupMemberAttribute,
                                         String groupAttribute,
                                         String groupSearchFilter,
                                         String userSearchFilter) {
        LdapProperties result = new LdapProperties();
        result.host = trimToEmpty(host);
        result.port = port;
        result.useSSL = useSSL;
        result.baseDN = trimToEmpty(baseDN);
        result.usernameAttribute = trimToEmpty(usernameAttribute);
        result.bindUserDN = trimToEmpty(bindUserDN);
        result.bindUserPass = trimToEmpty(bindUserPass);
        result.groupMemberAttribute = trimToEmpty(groupMemberAttribute);
        result.groupAttribute = trimToEmpty(groupAttribute);
        result.groupSearchFilter = trimToEmpty(groupSearchFilter);
        result.userSearchFilter = trimToEmpty(userSearchFilter);
        return result;
    }

    public static LdapProperties valueOf(String host,
                                         int port,
                                         boolean useSSL,
                                         String baseDN,
                                         String usernameAttribute) {
        LdapProperties result = new LdapProperties();
        result.host = trimToEmpty(host);
        result.port = port;
        result.useSSL = useSSL;
        result.baseDN = trimToEmpty(baseDN);
        result.usernameAttribute = trimToEmpty(usernameAttribute);
        return result;
    }

}

