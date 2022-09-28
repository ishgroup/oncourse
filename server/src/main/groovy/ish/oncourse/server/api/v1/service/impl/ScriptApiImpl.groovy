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


import com.google.inject.Inject
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.upgrades.DataPopulationUtils
import ish.oncourse.types.OutputType
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.service.ScriptApiService
import ish.oncourse.server.api.v1.model.ExecuteScriptRequestDTO
import ish.oncourse.server.api.v1.model.ScriptDTO
import ish.oncourse.server.api.v1.service.ScriptApi
import ish.oncourse.server.api.validation.EntityValidator
import ish.scripting.ScriptResult
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.yaml.snakeyaml.Yaml

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Context
import java.time.LocalDateTime


class ScriptApiImpl implements ScriptApi {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    private ICayenneService cayenneService

    @Inject
    private AqlService aqlService

    @Inject
    private ScriptApiService service

    @Inject
    protected EntityValidator validator

    @Context
    private HttpServletResponse response

    @Override
    void delete(Long id) {
        service.remove(id)
    }

    @Override
    ScriptDTO get(Long id) {
        service.get(id)
    }

    @Override
    void patch(Long id, ScriptDTO scriptDTO) {
        service.updateInternal(scriptDTO)
    }

    @Override
    void create(ScriptDTO script) {
        service.create(script)
    }

    @Override
    void update(Long id, ScriptDTO script) {
        service.update(id, script)
    }

    @Override
    String execute(ExecuteScriptRequestDTO request) {
        service.execute(request)
    }

    @Override
    String getResult(String processId) {
        ScriptResult results = service.getResult(processId)
        if (results.resultOutputType) {
            response.setContentType(results.resultOutputType?.mimeType?: OutputType.TEXT.mimeType)
            return results.resultValue as String
        }
        return null
    }

    @Override
    byte[] getPdf(String processId) {
        ScriptResult result = service.getResult(processId) as ScriptResult
        response.addHeader('Content-Disposition', "inline;filename=\"$result.name-${LocalDateTime.now().format('yyMMddHHmmss')}.pdf\"")
        result.resultValue as byte[]
    }

    @Override
    void updateConfigs(Long id, String configs) {
        def resourceAsStream = new ByteArrayInputStream(configs.getBytes())
        Yaml yaml = new Yaml()
        def loaded = yaml.load(resourceAsStream)
        def props = (Map<String, Object>) loaded

        def script = SelectById.query(Script,id).selectOne(cayenneService.newContext)
        DataPopulationUtils.updateExistedScript(props, script)
    }
}
