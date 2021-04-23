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

import java.nio.file.Files
import java.nio.file.Path


class DocumentParam {

    private String fileName
    private String type
    private Object content

    private DocumentParam() { }

    static DocumentParam valueOf(String fileName, Object content) {
        valueOf(fileName, Files.probeContentType(Path.of(fileName)), content)
    }

    static DocumentParam valueOf(String fileName, String type, Object content) {
        DocumentParam param = new DocumentParam()
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
    
    byte[] getContentInBytes() {
        byte[] bytes
        if (content instanceof File) {
            bytes = (content as File).getBytes()
        } else if (content instanceof byte[]) {
            bytes = content as byte[]
        } else if (content instanceof String) {
            bytes = (content as String).getBytes()
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
            ObjectOutputStream out = new ObjectOutputStream(bos)
            out.writeObject(content)
            out.flush()
            bytes = bos.toByteArray()
        }
        return bytes
    }
}
