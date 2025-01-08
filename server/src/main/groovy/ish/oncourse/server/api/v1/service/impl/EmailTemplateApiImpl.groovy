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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.common.types.AutomationStatus
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.service.EmailTemplateApiService
import ish.oncourse.server.api.v1.model.AutomationConfigsDTO
import ish.oncourse.server.api.v1.model.EmailTemplateDTO
import ish.oncourse.server.api.v1.service.EmailTemplateApi
import ish.oncourse.server.cayenne.EmailTemplate
import ish.util.AbstractEntitiesUtil
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect

class EmailTemplateApiImpl implements EmailTemplateApi {

    @Inject
    private EmailTemplateApiService service

    @Inject
    private ICayenneService cayenneService

    List<EmailTemplateDTO> getTemplatesWithKeyCode(String entityName) {
        def currentEntitiesNames = AbstractEntitiesUtil.getImplsOf(entityName)
        def currentEntityNameRequiredExp = buildExpressionForEntitiesNames(currentEntitiesNames)
        List<EmailTemplateDTO> result = ObjectSelect.query(EmailTemplate)
                    .and(EmailTemplate.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED))
                    .and(currentEntityNameRequiredExp)
                .select(cayenneService.newContext)
                .collect { service.toRestModel(it) }
        result
    }

    @Override
    void create(EmailTemplateDTO emailTemplate) {
        service.create(emailTemplate)
    }

    @Override
    EmailTemplateDTO get(Long id) {
        service.get(id)
    }

    @Override
    String getConfigs(Long id) {
        return service.getConfigs(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, EmailTemplateDTO emailTemplate) {
        service.update(id, emailTemplate)
    }

    @Override
    void updateConfigs(Long id, AutomationConfigsDTO  emailTemplateConfigs) {
        service.updateConfigs(id, emailTemplateConfigs.config)
    }

    @Override
    void updateInternal(EmailTemplateDTO exportTemplate) {
        service.updateInternal(exportTemplate)
    }


    private Expression buildExpressionForEntitiesNames(List<String> entitiesNames){
        if(entitiesNames.isEmpty())
            throw new IllegalArgumentException("List of entity names cannot be null!")
        def expression = EmailTemplate.ENTITY.eq(entitiesNames.get(0))
        for(int i = 1; i < entitiesNames.size(); i++){
            expression = expression.orExp(EmailTemplate.ENTITY.eq(entitiesNames.get(i)))
        }
        return expression
    }
}
