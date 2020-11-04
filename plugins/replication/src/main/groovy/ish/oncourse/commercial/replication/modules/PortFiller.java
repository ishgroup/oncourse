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

package ish.oncourse.commercial.replication.modules;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import javax.xml.ws.BindingProvider;
import java.util.ArrayList;
import java.util.List;

class PortFiller<P> {
    private P port;
    private long timeOut;
    private String serviceUrl;
    private List<Header> headers;
    private List<Interceptor<? extends Message>> outInterceptors = new ArrayList<>();

    private static final GZIPInInterceptor IN = new GZIPInInterceptor();
    private static final GZIPOutInterceptor OUT = new GZIPOutInterceptor();

    private PortFiller() {
    }

    public P fill() {
        var client = ClientProxy.getClient(port);

        client.getInInterceptors().add(IN);
        client.getOutInterceptors().add(OUT);
        client.getOutFaultInterceptors().add(OUT);

        client.getOutInterceptors().addAll(outInterceptors);

        var conduit = (HTTPConduit) client.getConduit();

        var httpClientPolicy = createHTTPClientPolicy(timeOut);

        conduit.setClient(httpClientPolicy);

        var provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);
        provider.getRequestContext().put(Header.HEADER_LIST, headers);

        return port;
    }

    private HTTPClientPolicy createHTTPClientPolicy(long timeOut) {
        var httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpClientPolicy.setConnectionTimeout(timeOut);
        httpClientPolicy.setReceiveTimeout(timeOut);
        return httpClientPolicy;
    }

    public void addOutInterceptor(Interceptor<? extends Message> outInterceptor) {
        outInterceptors.add(outInterceptor);
    }

    public static <P> PortFiller<P> valueOf(P port, String serviceUrl, List<Header> headers, long timeOut) {
        var builder = new PortFiller<P>();
        builder.port = port;
        builder.serviceUrl = serviceUrl;
        builder.headers = headers;
        builder.timeOut = timeOut;
        return builder;
    }
}
