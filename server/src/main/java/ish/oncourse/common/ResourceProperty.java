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

package ish.oncourse.common;/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Enumeration containing all available resource properties.
 */
public enum  ResourceProperty {

    //common automation props
    NAME("name"),
    ENABLED("enabled"),
    ENTITY_CLASS("entityClass"),
    ENTITY_ATTRIBUTE("entityAttribute"),
    DESCRIPTION("description"),
    BODY("body"),
    KEY_CODE("keyCode"),
    OUTPUT_TYPE("outputType"),

    @Deprecated
    MIN_VERSION("minVersion"),
    @Deprecated
    PROPERTY_FILE_NAME("propertyFileName"),

    //bingings
    OPTIONS("options"),
    VARIABLES("variables"),
    LABEL("label"),
    VALUE("value"),
    DATA_TYPE("dataType"),

    //scripts
    TRIGGER_TYPE("triggerType"),
    CRON_SCHEDULE("schedule"),
    ENTITY_EVENT_TYPE("entityEventType"),
    ON_COURSE_EVENT_TYPE("onCourseEventType"),
    @Deprecated
    SCRIPT("script"),

	//groovy XML and CSV exports
    @Deprecated
	EXPORT_TEMPLATE("exportTemplate"),

    //email templates
    SUBJECT("subject"),
    MESSAGE_TYPE("type"),
    TXT_TEMPLATE("txtTemplate"),
    HTML_TEMPLATE("htmlTemplate"),
    @Deprecated
    BODY_PLAIN("bodyPlain"),
    @Deprecated
    BODY_HTML("bodyHtml");



    private int value;
    private String displayName;

    ResourceProperty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
