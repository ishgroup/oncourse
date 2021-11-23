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
import ish.util.LocalDateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class LicenseService {
    public static final String SERVICES_SECURITYKEY = "services.securitykey";
    private Integer max_concurrent_users = null;
    private Boolean access_control = null;
    private Boolean custom_scripts = null;
    private String url = null;
    private LocalDateTime modified = null;

    private Integer purge_audit_after_days = null;

    private String services_host =  "https://secure-payment.oncourse.net.au";
    private String usi_host = "https://secure-payment.oncourse.net.au";

    private String college_key;
    private String security_key;

    private LicenseSmsService smsService;
    private static final Logger logger = LogManager.getLogger();



    @BQConfigProperty
    public void setMax_concurrent_users(int max_concurrent_users) {
        logger.warn("server will limit number of concurrent users to " + max_concurrent_users);
        this.max_concurrent_users = max_concurrent_users;
    }

    @BQConfigProperty
    public void setAccess_control(boolean access_control) {
        this.access_control = access_control;
    }

    @BQConfigProperty
    public void setCustom_scripts(boolean custom_scripts) {
        this.custom_scripts = custom_scripts;
    }

    @BQConfigProperty
    public void setPurge_audit_after_days(Integer purge_audit_after_days) {
        if (purge_audit_after_days < 1) {
            purge_audit_after_days = 1;
        }
        logger.warn("server will limit number of days for storing audit logs to " + purge_audit_after_days);
        this.purge_audit_after_days = purge_audit_after_days;
    }

    @BQConfigProperty
    public void setServices_host(String services_host) {
        logger.warn("server will use followed services url: " + services_host);
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

    @BQConfigProperty
    public void setEula(LinkedHashMap<String, String> eula) {
        modified = LocalDateUtils.stringToTimeValue(eula.get("modified"));
        url = eula.get("url");
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

    public Integer getPurge_audit_after_days() {
        return purge_audit_after_days;
    }

    public Boolean isReplicationDisabled() {
        return security_key == null;
    }

    public String getCurrentHostName() {
        return college_key == null ? null : String.format("https://%s.cloud.oncourse.cc", college_key);
    }

    public void setSmsService(LicenseSmsService smsService) {
        this.smsService = smsService;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getModified() {
        return modified;
    }


    public Object getLisense(String key) {

        switch (key) {
            case "license.access_control":
                return access_control;
            case "license.scripting":
                return custom_scripts;
            case "license.sms":
                return smsService.getMessage_batch();
            default:
                return null;
        }
    }
}

class LicenseSmsService {

    private Integer message_batch = null;

    @BQConfigProperty
    public void setMessage_batch(int message_batch) {
        this.message_batch = message_batch;
    }

    public Integer getMessage_batch() {
        return message_batch;
    }
}

