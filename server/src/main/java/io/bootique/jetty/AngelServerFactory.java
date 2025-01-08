/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package io.bootique.jetty;

import io.bootique.jetty.connector.ConnectorFactory;
import io.bootique.jetty.request.RequestMDCManager;
import io.bootique.jetty.server.*;
import ish.oncourse.server.modules.AngelHttpsConnectorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.*;

// Implementation use Jetty 11.0 instead of 9.0
public class AngelServerFactory extends ServerFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    private int maxFormContentSize;
    private int maxFormKeys;

    @Override
    public ServerHolder createServerHolder(Set<MappedServlet> servlets, Set<MappedFilter> filters, Set<MappedListener> listeners, Set<ServletContextHandlerExtender> contextHandlerExtenders, RequestMDCManager mdcManager) {
        String context = this.resolveContext();
        ThreadPool threadPool = this.createThreadPool();
        ServletContextHandler contextHandler = this.createHandler(context, servlets, filters, listeners);
        Server server = new Server(threadPool);
        server.setStopAtShutdown(true);
        server.setStopTimeout(1000L);
        server.setHandler(contextHandler);
        this.postConfigHandler(contextHandler, contextHandlerExtenders);
        if (this.maxFormContentSize > 0) {
            server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", this.maxFormContentSize);
            contextHandler.setMaxFormContentSize(this.maxFormContentSize);
        }

        if (this.maxFormKeys > 0) {
            server.setAttribute("org.eclipse.jetty.server.Request.maxFormKeys", this.maxFormKeys);
            contextHandler.setMaxFormKeys(this.maxFormKeys);
        }

        this.createRequestLog(server);
        Collection<ConnectorFactory> connectorFactories = this.connectorFactories(server);
        Collection<ConnectorHolder> connectorHolders = new ArrayList(2);
        if (connectorFactories.isEmpty()) {
            LOGGER.warn("Jetty starts with no connectors configured. Is that expected?");
        } else {
            connectorFactories.forEach((cf) -> {
                NetworkConnector connector = cf.createConnector(server);
                connector.addBean(mdcManager);
                server.addConnector(connector);
                connectorHolders.add(new ConnectorHolder(connector));
            });
        }

        ServerHolder serverHolder = new ServerHolder(server, context, connectorHolders);
        contextHandler.addEventListener(new ServerLifecycleLogger(serverHolder));
        return serverHolder;
    }

    @Override
    protected GzipHandler createGzipHandler() {
        return  new GzipHandler();
    }

    @Override
    protected void installListeners(ServletContextHandler handler, Set<MappedListener> listeners) {
        if (listeners.isEmpty()) {
            return;
        }
        sortedListeners(listeners).forEach(listener -> {
            LOGGER.info("Adding listener {}", listener.getListener().getClass().getName());
            handler.addEventListener(listener.getListener());
        });
    }

    private List<MappedListener> sortedListeners(Set<MappedListener> unsorted) {
        List<MappedListener> sorted = new ArrayList<>(unsorted);

        sorted.sort(Comparator.comparing(MappedListener::getOrder));
        return sorted;
    }

    @Override
    protected Collection<ConnectorFactory> connectorFactories(Server server) {
        Collection<ConnectorFactory> connectorFactories = new ArrayList<>();
        if (this.connectors != null) {
            connectorFactories.addAll(this.connectors);
        }

        if (connectorFactories.isEmpty()) {
            connectorFactories.add(new AngelHttpsConnectorFactory());
        }

        return connectorFactories;
    }
}
