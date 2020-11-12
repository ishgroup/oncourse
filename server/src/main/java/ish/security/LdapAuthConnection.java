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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import java.net.URI;
import java.util.Hashtable;

import static javax.naming.Context.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class LdapAuthConnection {
    private static final Logger logger = LogManager.getLogger();
    // holds the LDAP connection parameters

    private LdapProperties properties;
    protected Hashtable<String, String> ldapParams = new Hashtable<>();

    private LdapAuthConnection() {
    }

    private void initBindUser() {
        /*
         * The LDAP v3 supports anonymous, simple, and SASL authentication. SASL is the Simple Authentication and Security Layer (RFC 2222). It specifies a
		 * challenge-response protocol in which data is exchanged between the client and the server for the purposes of authentication and establishment of a
		 * security layer on which to carry out subsequent communication. Default authentication would be anonymous and this is used if nothing is specified for
		 * the SECURITY_AUTHENTICATION field.
		 */

        if (properties.getBindUserDN() != null) {
            this.ldapParams.put(Context.SECURITY_PRINCIPAL, properties.getBindUserDN());
            if (properties.getBindUserPass() != null) {
                this.ldapParams.put(Context.SECURITY_CREDENTIALS, properties.getBindUserPass());
            }
        }
    }

    /**
     * Check to see whether we can connect to the ldap server
     *
     * @return true if everything is OK
     */
    public boolean testLdapConnection() {

        try {
            new InitialDirContext(this.ldapParams);
        } catch (NamingException e) {
            System.out.println(e);
            logger.warn(e);
            return false;
        }
        return true;
    }

    public LdapName findUser(String username) throws NamingException {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"dn"});

        InitialDirContext context = new InitialDirContext(this.ldapParams);

        String search = this.properties.getUsernameAttribute() + "=" + username;
        if (!isBlank(this.properties.getUserSearchFilter())) {
            search = "(&(" + search + ")(" + this.properties.getUserSearchFilter() + "))";
        }

        NamingEnumeration<SearchResult> result = context.search(this.properties.getBaseDN(), search, searchControls);

        if (result == null || !result.hasMore()) {
            return null;
        }

        return new LdapName(result.next().getNameInNamespace());
    }

    public boolean authenticate(LdapName user, String password) {
        Hashtable<String, String> userBindParams = new Hashtable<>(this.ldapParams);
        userBindParams.put(Context.SECURITY_PRINCIPAL, user.toString());
        userBindParams.put(Context.SECURITY_CREDENTIALS, password);
        try {
            new InitialDirContext(userBindParams);
            return true;
        } catch (NamingException e) {
            logger.warn(e);
            return false;
        }
    }

    public LdapName findGroup(String groupname) throws NamingException {
        if (StringUtils.isBlank(groupname)) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        if (StringUtils.isBlank(this.properties.getGroupAttribute())) {
            throw new IllegalArgumentException("Group name attribute cannot be empty.");
        }

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"dn"});
        InitialDirContext context = new InitialDirContext(this.ldapParams);

        String search = this.properties.getGroupAttribute() + "=" + groupname;
        if (!isBlank(this.properties.getGroupSearchFilter())) {
            search = "(&(" + search + ")(" + this.properties.getGroupSearchFilter() + "))";
        }

        NamingEnumeration<SearchResult> result = context.search(this.properties.getBaseDN(), search, searchControls);

        if (result == null || !result.hasMore()) {
            return null;
        }

        return new LdapName(result.next().getNameInNamespace());
    }

    /**
     * Authorise user for this group
     *
     * @return true if success
     */
    public boolean authorise(Name user, Name group) throws NamingException {
        if (isBlank(this.properties.getGroupMemberAttribute())) {
            throw new IllegalArgumentException("Group member attribute cannot be empty.");
        }

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);
        searchControls.setReturningAttributes(new String[]{"dn"});

        String search = this.properties.getGroupMemberAttribute() + "=" + user.toString();
        InitialDirContext context = new InitialDirContext(this.ldapParams);

        String groupBaseDN = group.toString();
        NamingEnumeration<SearchResult> result = context.search(groupBaseDN, search, searchControls);
        return !(result == null || !result.hasMore());

    }

    /**
     * Posix style authorise where the user uid is passed rather than the dn
     *
     * @return true if success
     */
    public boolean authorisePosixUser(String user, Name group) throws NamingException {
        if (isBlank(this.properties.getGroupMemberAttribute())) {
            throw new IllegalArgumentException("Group member attribute cannot be empty.");
        }

        if (group == null || user == null) {
            return false;
        }

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);
        searchControls.setReturningAttributes(new String[]{"dn"});

        String search = this.properties.getGroupMemberAttribute() + "=" + user;
        InitialDirContext context = new InitialDirContext(this.ldapParams);
        String groupBaseDN = group.toString();

        NamingEnumeration<SearchResult> result = context.search(groupBaseDN, search, searchControls);
        return !(result == null || !result.hasMore());

    }

    /**
     * Create a new LDAP channel ready for authentication and authorisation
     */
    public static LdapAuthConnection valueOf(LdapProperties properties) {
        try {
            LdapAuthConnection result = new LdapAuthConnection();

            result.properties = properties;
            result.ldapParams.put(INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");


            if (isBlank(result.properties.getUsernameAttribute())) {
                throw new IllegalArgumentException("The username attribute must be set.");
            }

            // creating a URI will validate that the URI is valid
            URI ldapURI = new URI(result.properties.isUseSSL() ? "ldaps" : "ldap", null, result.properties.getHost(), result.properties.getPort(), null, null, null);
            result.ldapParams.put(PROVIDER_URL, ldapURI.toString());
            if (result.properties.isUseSSL()) {
                result.ldapParams.put(Context.SECURITY_PROTOCOL, "ssl");
            }
            result.ldapParams.put(SECURITY_AUTHENTICATION, "simple");
            result.initBindUser();
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static LdapAuthConnection valueOf(CommonPreferenceController preferenceController) {
        return valueOf(LdapProperties.valueOf(preferenceController));
    }
}
