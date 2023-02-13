/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util;

import com.beust.jcommander.Strings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DevToolsUtil {

    private static DevTools devTools;

    private static final List<String> requestList = new ArrayList<>();

    private static final List<String> responseList = new ArrayList<>();


    private DevToolsUtil() {}

    public static DevToolsUtil valueOf(WebDriver driver) {
        DevToolsUtil devToolsUtil = new DevToolsUtil();

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        DevToolsUtil.devTools = devTools;
        return devToolsUtil;
    }

    public DevToolsUtil createResponseListener() {
        devTools.addListener(Network.responseReceived(), l ->
            requestList.add(String.join(", ",
                    "Url: " + l.getResponse().getUrl(),
                    "Status: " + l.getResponse().getStatus(),
                    "Type: " + l.getResponse().getMimeType()
            ))
        );
        return this;
    }
    public DevToolsUtil createRequestListener() {
        devTools.addListener(Network.requestWillBeSent(), l ->
                responseList.add(l.getRequest().getMethod() + ": " + l.getRequest().getUrl())
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
