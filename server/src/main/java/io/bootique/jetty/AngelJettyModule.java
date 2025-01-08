/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package io.bootique.jetty;

import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Provides;
import io.bootique.jetty.request.RequestMDCItem;
import io.bootique.jetty.request.RequestMDCManager;
import io.bootique.jetty.server.ServerFactory;
import io.bootique.jetty.server.ServerHolder;
import io.bootique.jetty.server.ServletContextHandlerExtender;
import io.bootique.jetty.servlet.AngelServletEnvironment;
import io.bootique.log.BootLogger;
import io.bootique.shutdown.ShutdownManager;
import org.eclipse.jetty.server.Server;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

public class AngelJettyModule extends JettyModule {

    public AngelJettyModule(String configPrefix) {
        super(configPrefix);
    }

    public AngelJettyModule() {
        super("jetty");
    }

    @Singleton
    @Provides
    ServerFactory providerServerFactory(ConfigurationFactory configFactory) {
        return configFactory.config(AngelServerFactory.class, getConfigPrefix());
    }

    @Singleton
    @Provides
    Server providerServer(ServerHolder holder) {
        return super.providerServer(holder);
    }

    @Singleton
    @Provides
    ServerHolder provideServerHolder(ServerFactory factory, Set<Servlet> servlets, Set<MappedServlet> mappedServlets, Set<Filter> filters, Set<MappedFilter> mappedFilters, Set<EventListener> listeners, Set<MappedListener> mappedListeners, Set<ServletContextHandlerExtender> contextHandlerExtenders, RequestMDCManager mcdManager, BootLogger bootLogger, ShutdownManager shutdownManager) {
        return super.provideServerHolder(factory, servlets, mappedServlets, filters, mappedFilters, listeners, mappedListeners, contextHandlerExtenders, mcdManager, bootLogger, shutdownManager);
    }

    @Provides
    @Singleton
    RequestMDCManager provideRequestMDCManager(Map<String, RequestMDCItem> items) {
        return super.provideRequestMDCManager(items);
    }

    @Singleton
    @Provides
    AngelServletEnvironment createStateTrackerImpl() {
        return new AngelServletEnvironment();
    }
}
