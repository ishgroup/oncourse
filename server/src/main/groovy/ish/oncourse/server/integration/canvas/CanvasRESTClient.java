/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.canvas;

import groovyx.net.http.Method;
import groovyx.net.http.RESTClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * CanvasRESTClient can send DELETE request with body. Current implementation {@link RESTClient} with body in DELETE method throw exception,
 * that cannot set a request body for a DELETE method.
 */
public class CanvasRESTClient extends RESTClient {

    private static class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {

        @Override
        public String getMethod() {
            return Method.DELETE.name();
        }
    }

    public CanvasRESTClient(Object defaultURI) throws URISyntaxException {
        super(defaultURI);

    }

    @Override
    public Object delete(Map<String, ?> args) throws URISyntaxException, IOException {
        return this.doRequest(new RequestConfigDelegate(args, new HttpDeleteWithEntity(), null));
    }

}
