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

package ish.oncourse.server.http;

import com.google.inject.Provider;
import io.bootique.annotation.BQConfigProperty;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class HttpFactory {

    private Integer port;
    private String ip;
    private String path;

    private Provider<Server> serverProvider;

    @BQConfigProperty
    void setPort(int port) {
        this.port = port;
    }

    @BQConfigProperty
    void setIp(String ip) {
        this.ip = ip;
    }

    @BQConfigProperty
    void setPath(String path) {
        this.path = path;
    }

    public Integer getPort() {
        return port;
    }
    public String getIp() {
        return ip;
    }

    public HttpFactory serverProvider(Provider<Server> serverProvider) {
        this.serverProvider = serverProvider;
        return this;
    }

    public void init() {
        Server server = serverProvider.get();
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        ServletContextHandler handler = (ServletContextHandler) server.getHandler();
        if (port != null) {
            connector.setPort(port);
        }
        if (ip != null) {
            connector.setHost(ip);
        }
        if (path != null) {
            handler.setContextPath(path);
        }
    }
}
