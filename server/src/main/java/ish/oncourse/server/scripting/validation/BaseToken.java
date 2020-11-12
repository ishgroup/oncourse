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

package ish.oncourse.server.scripting.validation;

/**
 * Basic token for script.
 */
public abstract class BaseToken {

    static final String ONE_LINE_COMMENT_REGEX = "\\/\\/.+";
    static final String SCRIPT_STRUCTURE_REGEX = "(import(\\s+static){0,1}\\s+([\\w]+)(([.]?[\\w*]+))+\\s+)*[\\w\\W\\s\\S\\d\\D]*";

    protected String content = "";
    private boolean isComplete;

    public abstract boolean appendContent(String appendedContent);

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
