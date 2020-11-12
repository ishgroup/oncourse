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

package ish.oncourse.server.messaging

import groovy.transform.CompileDynamic

@CompileDynamic
class AttachmentParam {

    private String fileName
    private String type
    private Object content

    private AttachmentParam() {

    }

    static AttachmentParam valueOf(String fileName, String type, Object content) {
        AttachmentParam param = new AttachmentParam()
        param.fileName = fileName
        param.type = type
        param.content = content
        param
    }

    String getFileName() {
        return fileName
    }

    String getType() {
        return type
    }

    Object getContent() {
        return content
    }
}
