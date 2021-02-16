/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.messaging

import io.bootique.annotation.BQConfigProperty
import ish.util.RuntimeUtil

class SMTPService {

    private Integer email_batch = null
    private String host
    private Integer port
    private String username
    private String password
    private Mode mode = Mode.ssl

    @BQConfigProperty
    void setEmail_batch(Integer email_batch) {
        RuntimeUtil.println("server will limit number of email batch to " + email_batch);
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
        this.mode = Mode.valueOf(mode)
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
    
    static enum Mode {
        ssl,
        starttls,
        unsafe
        
        static Mode valueOf(String name) {
            switch (name) {
                case 'starttls':
                    return starttls
                case 'unsafe': 
                    return unsafe
                case 'tls':
                case 'ssl':
                    return ssl
                default:
                    return ssl
            }
        }
    }
}
