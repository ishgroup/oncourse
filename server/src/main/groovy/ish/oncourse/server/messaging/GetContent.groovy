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
import org.apache.commons.lang3.StringUtils

import javax.activation.DataHandler
import javax.mail.BodyPart
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

@CompileDynamic
class GetContent {

    public static final String MIXED_MULTIPART_TYPE = 'mixed'
    public static final String ALTERNATIVE_MULTIPART_TYPE = 'alternative'

    private static final String TEXT_HTML = 'text/html'

    private GetEmailPlainBody getEmailPlainBody
    private GetEmailHtmlBody getEmailHtmlBody
    private List<AttachmentParam> attachmentParams

    //should be private after deleting all deprecated content
    GetContent() {

    }

    static GetContent valueOf(GetEmailPlainBody getEmailPlainBody) {
        valueOf(getEmailPlainBody, GetEmailHtmlBody.empty(), null)
    }

    static GetContent valueOf(GetEmailPlainBody getEmailPlainBody, GetEmailHtmlBody getEmailHtmlBody) {
        valueOf(getEmailPlainBody, getEmailHtmlBody, null)
    }

    static GetContent valueOf(GetEmailPlainBody getEmailPlainBody, GetEmailHtmlBody getEmailHtmlBody, List<AttachmentParam> attachmentParams) {
        GetContent getContent = new GetContent()
        getContent.getEmailPlainBody = getEmailPlainBody
        getContent.getEmailHtmlBody = getEmailHtmlBody
        getContent.attachmentParams = attachmentParams
        getContent
    }

    GetEmailPlainBody getGetEmailPlainBody() {
        return getEmailPlainBody
    }

    GetEmailHtmlBody getGetEmailHtmlBody() {
        return getEmailHtmlBody
    }

    Multipart get() throws MessagingException {

        Multipart multipart = new MimeMultipart()

        if (StringUtils.isNotBlank(getEmailHtmlBody.get())) {
            BodyPart htmlPart = new MimeBodyPart()
            htmlPart.setContent(getEmailHtmlBody.get(), TEXT_HTML)

            multipart.addBodyPart(htmlPart)
        } else if (StringUtils.isNotBlank(getEmailPlainBody.get())) {
            BodyPart textPart = new MimeBodyPart()
            textPart.text = getEmailPlainBody.get()

            multipart.addBodyPart(textPart)
        }

        attachmentParams.each { param ->
            BodyPart part = new MimeBodyPart()

            if (param.fileName)  {
                part.fileName = param.fileName
            }

            if (param.content instanceof File) {
                part.attachFile(param.content as File)
            } else if (param.content instanceof byte[]) {
                ByteArrayDataSource dataSrc = new ByteArrayDataSource(param.content as byte[], param.type)
                part.setDataHandler(new DataHandler(dataSrc))
            } else if (param.content instanceof String) {
                ByteArrayDataSource dataSrc = new ByteArrayDataSource(param.content.toString().bytes, param.type)
                part.setDataHandler(new DataHandler(dataSrc))
            } else {
                part.setContent(param.content, param.type)
            }

            multipart.addBodyPart(part)
        }

        multipart
    }
}
