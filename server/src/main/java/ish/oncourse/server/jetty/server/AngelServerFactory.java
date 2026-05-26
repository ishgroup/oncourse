/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.jetty.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.Inject;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.jetty.MappedFilter;
import io.bootique.jetty.MappedListener;
import io.bootique.jetty.MappedServlet;
import io.bootique.jetty.connector.ConnectorFactory;
import io.bootique.jetty.server.ConnectorDescriptor;
import io.bootique.jetty.server.ServerFactory;
import io.bootique.jetty.server.ServerLifecycleLogger;
import io.bootique.jetty.server.ServletContextHandlerExtender;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.modules.AngelHttpsConnectorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.*;

public class AngelServerFactory extends ServerFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    private int maxFormContentSize;
    private int maxFormKeys;

    public Server createServer(
            Set<MappedServlet> servlets,
            Set<MappedFilter> filters,
            Set<MappedListener> listeners,
            Set<ServletContextHandlerExtender> contextHandlerExtenders) {

        ThreadPool threadPool = createThreadPool();
        ServletContextHandler contextHandler = createHandler(servlets, filters, listeners);

        Server server = new Server(threadPool);
        server.setStopAtShutdown(true);
        server.setStopTimeout(1000L);
        server.setHandler(contextHandler);

        // postconfig *after* the handler is associated with the Server. Some extensions like WebSocket require access
        // to the handler's Server
        postConfigHandler(contextHandler, contextHandlerExtenders);

        if (maxFormContentSize > 0) {
            server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", maxFormContentSize);
            contextHandler.setMaxFormContentSize(maxFormContentSize);
        }

        if (maxFormKeys > 0) {
            server.setAttribute("org.eclipse.jetty.server.Request.maxFormKeys", maxFormKeys);
            contextHandler.setMaxFormKeys(maxFormKeys);
        }

        createRequestLog(server);

        Collection<ConnectorFactory> connectorFactories = connectorFactories(server);
       // connectorFactories.add(new AngelHttpsConnectorFactory());

        Collection<ConnectorDescriptor> connectorDescriptors = new ArrayList<>(2);

        if (connectorFactories.isEmpty()) {
            LOGGER.warn("Jetty starts with no connectors configured. Is that expected?");
        } else {
            connectorFactories.forEach(cf -> {
                NetworkConnector connector = cf.createConnector(server);
                server.addConnector(connector);
                connectorDescriptors.add(new ConnectorDescriptor(connector));
            });
        }

        contextHandler.addEventListener(new ServerLifecycleLogger(connectorDescriptors, context));
        return server;
    }

    protected GzipHandler createGzipHandler() {
        return  new GzipHandler();
    }

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
}
