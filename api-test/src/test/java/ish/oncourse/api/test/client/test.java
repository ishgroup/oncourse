/*
 * The MIT License
 *
 * Copyright 2020 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ish.oncourse.api.test.client;

import com.intuit.karate.Constants;
import com.intuit.karate.FileUtils;
import com.intuit.karate.Logger;
import com.intuit.karate.core.Config;
import com.intuit.karate.core.ScenarioEngine;
import com.intuit.karate.http.*;
import karate.io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.*;
import javax.net.ssl.*;

import karate.org.apache.http.Header;
import karate.org.apache.http.HttpEntity;
import karate.org.apache.http.HttpException;
import karate.org.apache.http.HttpHost;
import karate.org.apache.http.HttpMessage;
import karate.org.apache.http.HttpRequestInterceptor;
import karate.org.apache.http.auth.AuthScope;
import karate.org.apache.http.auth.UsernamePasswordCredentials;
import karate.org.apache.http.client.ClientProtocolException;
import karate.org.apache.http.client.CookieStore;
import karate.org.apache.http.client.CredentialsProvider;
import karate.org.apache.http.client.config.RequestConfig;
import karate.org.apache.http.client.entity.EntityBuilder;
import karate.org.apache.http.client.methods.CloseableHttpResponse;
import karate.org.apache.http.client.methods.RequestBuilder;
import karate.org.apache.http.client.utils.URIBuilder;
import karate.org.apache.http.config.Registry;
import karate.org.apache.http.config.RegistryBuilder;
import karate.org.apache.http.config.SocketConfig;
import karate.org.apache.http.conn.ssl.LenientSslConnectionSocketFactory;
import karate.org.apache.http.conn.ssl.NoopHostnameVerifier;
import karate.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import karate.org.apache.http.conn.ssl.TrustAllStrategy;
import karate.org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import karate.org.apache.http.cookie.Cookie;
import karate.org.apache.http.cookie.CookieOrigin;
import karate.org.apache.http.cookie.CookieSpecProvider;
import karate.org.apache.http.cookie.MalformedCookieException;
import karate.org.apache.http.impl.client.BasicCookieStore;
import karate.org.apache.http.impl.client.BasicCredentialsProvider;
import karate.org.apache.http.impl.client.CloseableHttpClient;
import karate.org.apache.http.impl.client.HttpClientBuilder;
import karate.org.apache.http.impl.client.LaxRedirectStrategy;
import karate.org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import karate.org.apache.http.impl.cookie.DefaultCookieSpec;
import karate.org.apache.http.protocol.HttpContext;
import karate.org.apache.http.ssl.SSLContextBuilder;
import karate.org.apache.http.ssl.SSLContexts;

/**
 *
 * @author pthomas3
 */
public class test implements HttpClient, HttpRequestInterceptor {

    private final ScenarioEngine engine;
    private final Logger logger;
    private final HttpLogger httpLogger;

    private HttpClientBuilder clientBuilder;
    private CookieStore cookieStore;

    public static class LenientCookieSpec extends DefaultCookieSpec {

        static final String KARATE = "karate";

        public LenientCookieSpec() {
            super(new String[]{"EEE, dd-MMM-yy HH:mm:ss z", "EEE, dd MMM yyyy HH:mm:ss Z"}, false);
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            return true;
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            // do nothing
        }

        public static Registry<CookieSpecProvider> registry() {
            CookieSpecProvider specProvider = (HttpContext hc) -> new LenientCookieSpec();
            return RegistryBuilder.<CookieSpecProvider>create()
                    .register(KARATE, specProvider).build();
        }

    }

    public test(ScenarioEngine engine) {
        this.engine = engine;
        logger = engine.logger;
        httpLogger = new HttpLogger(logger);
        configure(engine.getConfig());
    }

    private void configure(Config config) {
        clientBuilder = HttpClientBuilder.create();
        clientBuilder.disableAutomaticRetries();
        if (!config.isFollowRedirects()) {
            clientBuilder.disableRedirectHandling();
        } else { // support redirect on POST by default
            clientBuilder.setRedirectStrategy(LaxRedirectStrategy.INSTANCE);
        }
        cookieStore = new BasicCookieStore();
        clientBuilder.setDefaultCookieStore(cookieStore);
        clientBuilder.setDefaultCookieSpecRegistry(LenientCookieSpec.registry());
        clientBuilder.useSystemProperties();
        if (config.isSslEnabled()) {
            HostnameVerifier hv = (s, sslSession) -> true;
            try {

                X509TrustManager trustManager = new NoValidationX509TrustManager();
                TrustManager[] trust = new TrustManager[] { trustManager };

                // SSLContext represents a secure socket protocol implementation
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trust, new java.security.SecureRandom());

                SSLSocketFactory factory = sslContext.getSocketFactory();
                HttpsURLConnection.setDefaultSSLSocketFactory(factory);
                javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hv);
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, hv);

                clientBuilder.setSSLSocketFactory(socketFactory);
            } catch (Exception e) {
                logger.error("ssl context init failed: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        RequestConfig.Builder configBuilder = RequestConfig.custom()
                .setCookieSpec(LenientCookieSpec.KARATE)
                .setConnectTimeout(config.getConnectTimeout())
                .setSocketTimeout(config.getReadTimeout());
        if (config.getLocalAddress() != null) {
            try {
                InetAddress localAddress = InetAddress.getByName(config.getLocalAddress());
                configBuilder.setLocalAddress(localAddress);
            } catch (Exception e) {
                logger.warn("failed to resolve local address: {} - {}", config.getLocalAddress(), e.getMessage());
            }
        }
        clientBuilder.setDefaultRequestConfig(configBuilder.build());
        SocketConfig.Builder socketBuilder = SocketConfig.custom().setSoTimeout(config.getConnectTimeout());
        clientBuilder.setDefaultSocketConfig(socketBuilder.build());
        if (config.getProxyUri() != null) {
            try {
                URI proxyUri = new URIBuilder(config.getProxyUri()).build();
                clientBuilder.setProxy(new HttpHost(proxyUri.getHost(), proxyUri.getPort(), proxyUri.getScheme()));
                if (config.getProxyUsername() != null && config.getProxyPassword() != null) {
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(
                            new AuthScope(proxyUri.getHost(), proxyUri.getPort()),
                            new UsernamePasswordCredentials(config.getProxyUsername(), config.getProxyPassword()));
                    clientBuilder.setDefaultCredentialsProvider(credsProvider);
                }
                if (config.getNonProxyHosts() != null) {
                    ProxySelector proxySelector = new ProxySelector() {
                        private final List<String> proxyExceptions = config.getNonProxyHosts();

                        @Override
                        public List<Proxy> select(URI uri) {
                            return Collections.singletonList(proxyExceptions.contains(uri.getHost())
                                    ? Proxy.NO_PROXY
                                    : new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort())));
                        }

                        @Override
                        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                            logger.info("connect failed to uri: {}", uri, ioe);
                        }
                    };
                    clientBuilder.setRoutePlanner(new SystemDefaultRoutePlanner(proxySelector));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        clientBuilder.addInterceptorLast(this);
    }

    @Override
    public void setConfig(Config config) {
        configure(config);
    }

    @Override
    public Config getConfig() {
        return engine.getConfig();
    }

    private HttpRequest request;

    @Override
    public Response invoke(HttpRequest request) {
        this.request = request;
        RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod()).setUri(request.getUrl());
        if (request.getBody() != null) {
            EntityBuilder entityBuilder = EntityBuilder.create().setBinary(request.getBody());
            List<String> transferEncoding = request.getHeaderValues(HttpConstants.HDR_TRANSFER_ENCODING);
            if (transferEncoding != null) {
                for (String te : transferEncoding) {
                    if (te == null) {
                        continue;
                    }
                    if (te.contains("chunked")) { // can be comma delimited as per spec
                        entityBuilder.chunked();
                    }
                    if (te.contains("gzip")) {
                        entityBuilder.gzipCompress();
                    }
                }
                request.removeHeader(HttpConstants.HDR_TRANSFER_ENCODING);
            }
            requestBuilder.setEntity(entityBuilder.build());
        } 
        if (request.getHeaders() != null) {
            request.getHeaders().forEach((k, vals) -> vals.forEach(v -> requestBuilder.addHeader(k, v)));
        }
        CloseableHttpClient client = clientBuilder.build();
        CloseableHttpResponse httpResponse;
        byte[] bytes;
        try {
            httpResponse = client.execute(requestBuilder.build());
            HttpEntity responseEntity = httpResponse.getEntity();
            if (responseEntity == null || responseEntity.getContent() == null) {
                bytes = Constants.ZERO_BYTES;
            } else {
                InputStream is = responseEntity.getContent();
                bytes = FileUtils.toBytes(is);
            }
            request.setEndTimeMillis(System.currentTimeMillis());
        } catch (Exception e) {
            if (e instanceof ClientProtocolException && e.getCause() != null) { // better error message                
                throw new RuntimeException(e.getCause());
            } else {
                throw new RuntimeException(e);
            }
        }

        Map<String, List<String>> headers;
        List<Cookie> cookies = cookieStore.getCookies();
        if (!cookies.isEmpty()) {
            // TODO improve - this is only for the edge case where the apache client
            // auto-followed a redirect where cookies were involved
            List<String> cookieValues = new ArrayList(cookies.size());
            for (Cookie c : cookieStore.getCookies()) {
                Map<String, Object> map = new HashMap();
                map.put(Cookies.NAME, c.getName());
                map.put(Cookies.VALUE, c.getValue());
                map.put(Cookies.DOMAIN, c.getDomain());
                if (c.getExpiryDate() != null) {
                    map.put(Cookies.MAX_AGE, c.getExpiryDate().getTime());
                }
                map.put(Cookies.SECURE, c.isSecure());
                karate.io.netty.handler.codec.http.cookie.Cookie nettyCookie = Cookies.fromMap(map);
                String cookieValue = ServerCookieEncoder.LAX.encode(nettyCookie);
                cookieValues.add(cookieValue);
            }
            // removing is probably not needed since apache cookie handling is enabled, but anyway
            httpResponse.removeHeaders(HttpConstants.HDR_SET_COOKIE);
            headers = toHeaders(httpResponse);
            headers.put(HttpConstants.HDR_SET_COOKIE, cookieValues);
            cookieStore.clear();
        } else {
            headers = toHeaders(httpResponse);
        }
        Response response = new Response(httpResponse.getStatusLine().getStatusCode(), headers, bytes);
        httpLogger.logResponse(getConfig(), request, response);
        return response;
    }

    @Override
    public void process(karate.org.apache.http.HttpRequest hr, HttpContext hc) throws HttpException, IOException {
        request.setHeaders(toHeaders(hr));
        httpLogger.logRequest(getConfig(), request);
        request.setStartTimeMillis(System.currentTimeMillis());
    }

    private static Map<String, List<String>> toHeaders(HttpMessage msg) {
        Header[] headers = msg.getAllHeaders();
        Map<String, List<String>> map = new LinkedHashMap(headers.length);
        for (Header outer : headers) {
            String name = outer.getName();
            Header[] inner = msg.getHeaders(name);
            List<String> list = new ArrayList(inner.length);
            for (Header h : inner) {
                list.add(h.getValue());
            }
            map.put(name, list);
        }
        return map;
    }

}
