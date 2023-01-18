/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.messaging

import io.bootique.annotation.BQConfigProperty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SMTPService {

    private Integer email_batch = null
    private String host
    private Integer port
    private String username
    private String password
    private Mode mode = Mode.ssl
    private Boolean notSupportsTls13 = false

    private static final Logger logger = LogManager.logger

    @BQConfigProperty
    void setEmail_batch(Integer email_batch) {
        logger.warn("server will limit number of email batch to " + email_batch)
        this.email_batch = email_batch
    }

    @BQConfigProperty
    void setHost(String host) {
        this.host = host
    }

    @BQConfigProperty
    void setPort(Integer port) {
        this.port = port
    }

    @BQConfigProperty
    void setUsername(String username) {
        this.username = username
    }

    @BQConfigProperty
    void setPassword(String password) {
        this.password = password
    }
    
    @BQConfigProperty
    void setMode(String mode) {
        this.mode = Mode.byName(mode)
    }

    @BQConfigProperty
    void setNotSupportsTls13(Boolean notSupportsTls13) {
        this.notSupportsTls13 = notSupportsTls13
    }

    Integer getEmail_batch() {
        return email_batch
    }

    String getHost() {
        return host
    }

    Integer getPort() {
        return port
    }

    String getUserName() {
        return username
    }

    String getPassword() {
        return password
    }

    Mode getMode() {
        return mode
    }

    Boolean supportsTls13() {
        return !notSupportsTls13
    }

    static enum Mode {
        ssl,
        starttls,
        unsafe,
        mock
        
        static Mode byName(String name) {
            switch (name) {
                case 'starttls':
                    return starttls
                case 'unsafe': 
                    return unsafe
                case 'tls':
                case 'ssl':
                    return ssl
                case 'mock':
                    return mock
                default:
                    return ssl
            }
        }
    }
}
