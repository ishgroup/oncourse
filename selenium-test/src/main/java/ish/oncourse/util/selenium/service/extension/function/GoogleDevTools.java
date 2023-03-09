/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.extension.function;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GoogleDevTools {

    private static DevTools devTools;

    private static final List<String> requestList = new ArrayList<>();

    private static final List<String> responseList = new ArrayList<>();

    private GoogleDevTools() {}

    public static GoogleDevTools valueOf(WebDriver driver) {
        GoogleDevTools googleDevTools = new GoogleDevTools();

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        GoogleDevTools.devTools = devTools;
        return googleDevTools;
    }

    public GoogleDevTools createResponseListener() {
        devTools.addListener(Network.responseReceived(), l ->
            responseList.add(String.join(", ",
                    "Url: " + l.getResponse().getUrl(),
                    "Status: " + l.getResponse().getStatus(),
                    "Type: " + l.getResponse().getMimeType()
            ))
        );
        return this;
    }
    public GoogleDevTools createRequestListener() {
        devTools.addListener(Network.requestWillBeSent(), l ->
                requestList.add(l.getRequest().getMethod() + ": " + l.getRequest().getUrl())
        );
        return this;
    }

    public List<String> getRequestList() {
        return requestList;
    }

    public List<String> getResponseList() {
        return responseList;
    }
}
