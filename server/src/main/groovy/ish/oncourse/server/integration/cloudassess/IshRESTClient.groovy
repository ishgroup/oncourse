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

package ish.oncourse.server.integration.cloudassess

import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import org.apache.http.client.HttpClient
import org.apache.http.client.config.CookieSpecs
import org.apache.http.config.Lookup
import org.apache.http.config.RegistryBuilder
import org.apache.http.cookie.CookieSpecProvider
import org.apache.http.cookie.params.CookieSpecPNames
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.DefaultCookieSpecProvider
import org.apache.http.impl.cookie.IgnoreSpecProvider
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider
import org.apache.http.params.HttpParams

@CompileDynamic
class IshRESTClient extends RESTClient {


    IshRESTClient(Object defaultURI) throws URISyntaxException {
        super(defaultURI)
    }

    protected HttpClient createClient( HttpParams params ) {

        final CookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT, null, (String[])params.getParameter(CookieSpecPNames.DATE_PATTERNS), false)
        final CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(
                RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, null)
        final CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(
                RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, null)
        RegistryBuilder<CookieSpecProvider> registryBuilder = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.DEFAULT, defaultProvider)
                .register("best-match", defaultProvider)
                .register("compatibility", defaultProvider)
                .register(CookieSpecs.STANDARD, laxStandardProvider)
                .register(CookieSpecs.STANDARD_STRICT, strictStandardProvider)
                .register(CookieSpecs.NETSCAPE, new NetscapeDraftSpecProvider())
                .register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecProvider())

        Lookup<CookieSpecProvider> specProviderLookup = registryBuilder.build()


        HttpClientBuilder clientBuilder = HttpClientBuilder.create()

        clientBuilder.setDefaultCookieSpecRegistry(specProviderLookup)

        return clientBuilder.build()

    }
}
