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

package ish.oncourse.server.scripting.api;

import ish.oncourse.server.messaging.*;

public class MailDeliveryParamBuilder {
    private SmtpParameters parameters;
    private TemplateService templateService;

    private MailDeliveryParamBuilder(){

    }

    public static MailDeliveryParamBuilder valueOf(SmtpParameters parameters){
        return valueOf(parameters, null);
    }

    public static MailDeliveryParamBuilder valueOf(SmtpParameters parameters, TemplateService templateService){
        var mailDeliveryParamBuilder = new MailDeliveryParamBuilder();
        mailDeliveryParamBuilder.parameters = parameters;
        mailDeliveryParamBuilder.templateService = templateService;

        return mailDeliveryParamBuilder;
    }

    public MailDeliveryParam build(){
        var getFrom = GetFrom.valueOf(parameters.getFromAddress(), parameters.getFromName());
        var getEnvelopeFrom = GetEnvelopeFrom.valueOf(parameters.getFromAddress());
        var getAddressesTO = GetAddresses.valueOf(parameters.getToList());
        var getAddressesCC = GetAddresses.valueOf(parameters.getCcList());
        var getAddressesBCC = GetAddresses.valueOf(parameters.getBccList());
        var getSubject = GetSubject.valueOf(templateService, parameters.getTemplateIdentifier(), parameters.getBindings(), parameters.getSubject());
        var getEmailPlainBody = GetEmailPlainBody.valueOf(templateService, parameters.getTemplateIdentifier(), parameters.getBindings(), parameters.getContent());
        var getEmailHtmlBody = GetEmailHtmlBody.valueOf(templateService, parameters.getTemplateIdentifier(), parameters.getBindings());
        var getContent = GetContent.valueOf(getEmailPlainBody, getEmailHtmlBody, parameters.getAttachments());

        return MailDeliveryParam.valueOf(getFrom, getEnvelopeFrom, getAddressesTO, getAddressesCC, getAddressesBCC, getSubject, getContent);
    }
}
