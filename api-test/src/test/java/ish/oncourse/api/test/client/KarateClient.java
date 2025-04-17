/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.api.test.client;

import com.intuit.karate.core.Config;
import com.intuit.karate.core.ScenarioEngine;
import com.intuit.karate.http.ApacheHttpClient;
import com.intuit.karate.http.HttpLogger;
import com.intuit.karate.http.HttpRequest;
import com.intuit.karate.http.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class KarateClient extends ApacheHttpClient {

    private final HttpLogger httpLogger;
    private Client client;

    public KarateClient(ScenarioEngine engine) {
        super(engine);
        httpLogger = new HttpLogger(engine.logger);
        configure(engine.getConfig());
    }

    public void configure(Config config) {
        ClientConfig cc = new ClientConfig();
        // support request body for DELETE (non-standard)
        cc.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        if (!config.isFollowRedirects()) {
            cc.property(ClientProperties.FOLLOW_REDIRECTS, false);
        }
        cc.connectorProvider(new JettyConnectorProvider());

        ClientBuilder clientBuilder = ClientBuilder.newBuilder()
                .withConfig(cc)
                .register(MultiPartFeature.class);

        client = clientBuilder.build();
        client.property(ClientProperties.CONNECT_TIMEOUT, config.getConnectTimeout());
        client.property(ClientProperties.READ_TIMEOUT, config.getReadTimeout());
        if (config.getProxyUri() != null) {
            client.property(ClientProperties.PROXY_URI, config.getProxyUri());
            if (config.getProxyUsername() != null && config.getProxyPassword() != null) {
                client.property(ClientProperties.PROXY_USERNAME, config.getProxyUsername());
                client.property(ClientProperties.PROXY_PASSWORD, config.getProxyPassword());
            }
        }
    }

    @Override
    public Response invoke(HttpRequest request) {
        WebTarget target = client.target(request.getUrl());
        Invocation.Builder builder = target.request();
        String method = request.getMethod();

        if ("PATCH".equals(method)) { // http://danofhisword.com/dev/2015/09/04/Jersey-Client-Http-Patch.html
            builder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        }
        javax.ws.rs.core.Response httpResp;
        if (request.getHeaders() != null) {
            request.getHeaders().forEach((k, vals) -> vals.forEach(v -> builder.header(k, v)));
        }
        if (request.getBody() != null) {
            httpResp = builder.method(method, Entity.entity(request.getBody(), request.getContentType()));
        } else {
            httpResp = builder.method(method);
        }
        Map<String, List<String>> headers = new LinkedHashMap<>();
        for (Entry<String, List<Object>> entry : httpResp.getHeaders().entrySet()) {
            List<String> values = entry.getValue().stream().map(Object::toString).collect(Collectors.toList());
            headers.put(entry.getKey(), values);
        }

        Response response = new Response(httpResp.getStatus(), headers, httpResp.readEntity(byte[].class));

        httpLogger.logResponse(getConfig(), request, response);
        return response;
    }
}
