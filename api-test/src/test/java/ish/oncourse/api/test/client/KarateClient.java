/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.api.test.client;


import com.intuit.karate.Config;
import com.intuit.karate.ScriptValue;
import com.intuit.karate.core.ScenarioContext;
import com.intuit.karate.http.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

import static com.intuit.karate.http.Cookie.*;


public class KarateClient extends HttpClient<Entity> {

    private Client client;
    private WebTarget target;
    private Invocation.Builder builder;
    private Charset charset;

    @Override
    public void configure(Config config, ScenarioContext context) {
        ClientConfig cc = new ClientConfig();
        // support request body for DELETE (non-standard)
        cc.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        charset = config.getCharset();
        if (!config.isFollowRedirects()) {
            cc.property(ClientProperties.FOLLOW_REDIRECTS, false);
        }
        cc.connectorProvider(new JettyConnectorProvider());

        ClientBuilder clientBuilder = ClientBuilder.newBuilder()
                .withConfig(cc)
                .register(new LoggingInterceptor(context)) // must be first
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
    public String getRequestUri() {
        return target.getUri().toString();
    }

    @Override
    public void buildUrl(String url) {
        target = client.target(url);
        builder = target.request();
    }

    @Override
    public void buildPath(String path) {
        target = target.path(path);
        builder = target.request();
    }

    @Override
    public void buildParam(String name, Object... values) {
        target = target.queryParam(name, values);
        builder = target.request();
    }

    @Override
    public void buildHeader(String name, Object value, boolean replace) {
        if (replace) {
            builder.header(name, null);
        }
        builder.header(name, value);
    }

    @Override
    public void buildCookie(com.intuit.karate.http.Cookie c) {
        Cookie cookie = new Cookie(c.getName(), c.getValue());
        builder.cookie(cookie);
    }

    private MediaType getMediaType(String mediaType) {
        Charset cs = HttpUtils.parseContentTypeCharset(mediaType);
        if (cs == null) {
            cs = charset;
        }
        MediaType mt = MediaType.valueOf(mediaType);
        return cs == null ? mt : mt.withCharset(cs.name());
    }

    @Override
    public Entity getEntity(MultiValuedMap fields, String mediaType) {
        MultivaluedHashMap<String, Object> map = new MultivaluedHashMap<>();
        for (Entry<String, List> entry : fields.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return Entity.entity(map, getMediaType(mediaType));
    }

    @Override
    public Entity getEntity(List<MultiPartItem> items, String mediaType) {
        MultiPart multiPart = new MultiPart();
        for (MultiPartItem item : items) {
            if (item.getValue() == null || item.getValue().isNull()) {
                continue;
            }
            String name = item.getName();
            String filename = item.getFilename();
            ScriptValue sv = item.getValue();
            String ct = item.getContentType();
            if (ct == null) {
                ct = HttpUtils.getContentType(sv);
            }
            MediaType itemType = MediaType.valueOf(ct);
            if (HttpUtils.isPrintable(ct)) {
                Charset cs = HttpUtils.parseContentTypeCharset(mediaType);
                if (cs == null) {
                    cs = charset;
                }
                if (cs != null) {
                    itemType = itemType.withCharset(cs.name());
                }
            }
            if (name == null) { // most likely multipart/mixed
                BodyPart bp = new BodyPart().entity(sv.getAsString()).type(itemType);
                multiPart.bodyPart(bp);
            } else if (filename != null) {
                StreamDataBodyPart part = new StreamDataBodyPart(name, sv.getAsStream(), filename, itemType);
                multiPart.bodyPart(part);
            } else {
                multiPart.bodyPart(new FormDataBodyPart(name, sv.getAsString(), itemType));
            }
        }
        return Entity.entity(multiPart, mediaType);
    }

    @Override
    public Entity getEntity(String value, String mediaType) {
        return Entity.entity(value, getMediaType(mediaType));
    }

    @Override
    public Entity getEntity(InputStream value, String mediaType) {
        return Entity.entity(value, getMediaType(mediaType));
    }

    @Override
    public HttpResponse makeHttpRequest(Entity entity, ScenarioContext context) {
        String method = request.getMethod();
        if ("PATCH".equals(method)) { // http://danofhisword.com/dev/2015/09/04/Jersey-Client-Http-Patch.html
            builder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        }
        Response resp;
        if (entity != null) {
            resp = builder.method(method, entity);
        } else {
            resp = builder.method(method);
        }
        HttpRequest actualRequest = context.getPrevRequest();
        HttpResponse response = new HttpResponse(actualRequest.getStartTime(), actualRequest.getEndTime());
        byte[] bytes = resp.readEntity(byte[].class);
        response.setUri(getRequestUri());
        response.setBody(bytes);
        response.setStatus(resp.getStatus());
        for (NewCookie c : resp.getCookies().values()) {
            com.intuit.karate.http.Cookie cookie = new com.intuit.karate.http.Cookie(c.getName(), c.getValue());
            cookie.put(DOMAIN, c.getDomain());
            cookie.put(PATH, c.getPath());
            if (c.getExpiry() != null) {
                cookie.put(EXPIRES, c.getExpiry().getTime() + "");
            }
            cookie.put(SECURE, c.isSecure() + "");
            cookie.put(HTTP_ONLY, c.isHttpOnly() + "");
            cookie.put(MAX_AGE, c.getMaxAge() + "");
            response.addCookie(cookie);
        }
        for (Entry<String, List<Object>> entry : resp.getHeaders().entrySet()) {
            response.putHeader(entry.getKey(), entry.getValue());
        }
        return response;
    }

}
