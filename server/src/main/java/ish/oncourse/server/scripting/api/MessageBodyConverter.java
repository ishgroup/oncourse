/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.scripting.api;

import ish.oncourse.server.scripting.converter.ConvertCoreMarkdown;
import ish.oncourse.server.scripting.converter.ConvertCoreTextile;
import ish.oncourse.server.scripting.converter.RenderType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageBodyConverter {

    private final static String RENDER_RGXP = "(\\{render:\"\\w+\"})";

    private final static String TAG_RGXP = "<[^>]*>";

    private String messageBody;
    private RenderType renderType;

    private MessageBodyConverter() {}

    public static MessageBodyConverter valueOf(String messageBody, RenderType renderType) {
        MessageBodyConverter messageBodyConverter = new MessageBodyConverter();
        messageBodyConverter.messageBody = messageBody;
        messageBodyConverter.renderType = renderType;

        return messageBodyConverter;
    }

    public String convert() {

        String content = null;
        String type = "textile";

        Pattern pattern = Pattern.compile(RENDER_RGXP);
        Matcher matcher = pattern.matcher(messageBody);
        if (matcher.find()) {

            String renderMarker = matcher.group(matcher.groupCount() - 1);
            pattern = Pattern.compile("(md)|(html)|(textile)");
            matcher = pattern.matcher(renderMarker);
            if (matcher.find())
                type = matcher.group();
            content = messageBody.substring(0, messageBody.indexOf("{render:\"" + type + "\"")).trim();
        }

        if (content == null) {
           content = messageBody;
        }

        if (renderType == RenderType.RAW) {
            return content;
        }

        String result;
        switch (type) {
            case ("html"):
                result = content;
                break;
            case ("md"):
                result = ConvertCoreMarkdown.valueOf(content).convert();
                break;
            default:
                result = ConvertCoreTextile.valueOf(content).convert();
        }

        if (renderType == RenderType.HTML) {
            return result;
        }

        List<String> plainText = Arrays.stream(result.split(System.lineSeparator()))
                .map(line -> line.replaceAll(TAG_RGXP, StringUtils.SPACE).replaceAll("\\s+", StringUtils.SPACE).trim())
                .filter( tagContent -> !tagContent.isEmpty())
                .collect(Collectors.toList());

        return String.join(System.lineSeparator(), plainText);
    }

    public boolean shouldConvert() {
        Pattern pattern = Pattern.compile(RENDER_RGXP);
        Matcher matcher = pattern.matcher(messageBody);
        return matcher.find();
    }
}