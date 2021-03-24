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

package ish.oncourse.server.license;

import io.bootique.annotation.BQConfigProperty;
import ish.util.RuntimeUtil;

public class LicenseService {
    public static final String SERVICES_SECURITYKEY = "services.securitykey";
    private Integer max_concurrent_users = null;
    private Integer max_audit_log_store = 12;

    private String services_host =  "https://secure-payment.oncourse.net.au";
    private String usi_host = "https://secure-payment.oncourse.net.au";

    private String college_key;
    private String security_key;



    @BQConfigProperty
    public void setMax_concurrent_users(int max_concurrent_users) {
        RuntimeUtil.println("server will limit number of concurrent users to " + max_concurrent_users);
        this.max_concurrent_users = max_concurrent_users;
    }

    @BQConfigProperty
    public void setMax_audit_log_store(Integer max_audit_log_store) {
        if (max_audit_log_store < 1) {
            max_audit_log_store = 1;
        }
        if (max_audit_log_store > 24) {
            max_audit_log_store = 24;
        }
        RuntimeUtil.println("server will limit number of months for storing audit logs to " + max_audit_log_store);
        this.max_audit_log_store = max_audit_log_store;
    }

    @BQConfigProperty
    public void setServices_host(String services_host) {
        RuntimeUtil.println("server will use followed services url: " + services_host);
        this.services_host = services_host;
    }

    @BQConfigProperty
    public void setCollege_key(String college_key) {
        this.college_key = college_key;
    }

    @BQConfigProperty
    public void setSecurity_key(String security_key) {
        this.security_key = security_key;
    }

    @BQConfigProperty
    public void setUsi_host(String usi_host) {
        this.usi_host = usi_host;
    }

    public String getUsi_host() {
        return usi_host;
    }

    public String getSecurity_key() {
        return security_key;
    }

    public String getCollege_key() {
        return college_key;
    }

    public String getServices_host() {
        return services_host;
    }

    public Integer getMax_concurrent_users() {
        return max_concurrent_users;
    }

    public Integer getMax_audit_log_store() {
        return max_audit_log_store;
    }

    public Boolean isReplicationDisabled() {
        return security_key == null;
    }
}
