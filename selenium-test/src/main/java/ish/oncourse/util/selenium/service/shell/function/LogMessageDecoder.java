/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.shell.function;

import ish.oncourse.util.selenium.model.HttpConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gets names of builded budle and sourseMap js files to create localized React error message.
 */
public class LogMessageDecoder {

    private static final String URL_REGEX = "https?://" + System.getProperty(HttpConfiguration.IP.getKey()) + ":" +
            System.getProperty(HttpConfiguration.PORT.getKey()) + System.getProperty(HttpConfiguration.PATH.getKey());

    private static final String JS_FILE_REGEX = ".+?\\.js:\\d+:\\d+";

    private String bundleName;

    private String sourceMapName;

    private String errorLocation;

    private String logMessage;

    private LogMessageDecoder() {}

    public static LogMessageDecoder valueOf(String logMessage) {
        LogMessageDecoder decoder = new LogMessageDecoder();
        decoder.logMessage = logMessage;
        return decoder;
    }

    public LogMessageDecoder decode() {
        Matcher matcher = Pattern.compile(URL_REGEX + JS_FILE_REGEX).matcher(logMessage);

        if (matcher.find()) {
            String[] tokens = matcher.group().trim().replaceFirst(URL_REGEX,StringUtils.SPACE).trim().split(":");
            this.bundleName = tokens[0].replaceFirst(URL_REGEX, StringUtils.EMPTY);
            this.sourceMapName = this.bundleName + ".map";
            this.errorLocation = tokens[1] + ":" + tokens[2];
        }
        return this;
    }

    public boolean isReactLog() {
        Matcher matcher = Pattern.compile(URL_REGEX + JS_FILE_REGEX).matcher(logMessage);
        return matcher.find();
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getSourceMapName() {
        return sourceMapName;
    }

    public String getErrorLocation() {
        return errorLocation;
    }
}
