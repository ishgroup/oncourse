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

package ish.oncourse.server.export

import com.google.inject.Inject
import groovy.json.JsonBuilder
import groovy.transform.CompileDynamic
import groovy.xml.MarkupBuilder
import ish.export.ExportParameter
import ish.export.ExportResult
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.ExportTemplate
import ish.oncourse.server.messaging.DocumentParam
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.api.CollegePreferenceService
import ish.oncourse.server.scripting.api.ExportSpec
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

@CompileDynamic
class ExportService {

	ObjectContext context
    ScriptEngineManager scriptEngineManager
	CollegePreferenceService collegePreferenceService

	public static final String CONTEXT = "context"
	public static final String OUTPUT = "output"
	public static final String RESULT = "result"
	public static final String PREFERENCE = "preference"
	public static final String RECORDS = "records"
	public static final String XML = "xml"
	public static final String CSV = "csv"
	public static final String JSON = "json"

	@Inject
    ExportService(ICayenneService cayenneService, CollegePreferenceService collegePreferenceService) {
		context = cayenneService.newContext
		this.collegePreferenceService = collegePreferenceService
		scriptEngineManager = new ScriptEngineManager()
	}

	/**
	 * Generates exports for passed specification
	 * @param expParam export specification
	 * @return {@link ExportResult} object containing resulting export data
	 */
	ExportResult export(ExportParameter expParam) {
		def entity = expParam.entity

		def exportables = expParam.expression == null && expParam.ids.size() > 0 ?
				EntityUtil.getObjectsByIds(context, EntityUtil.entityClassForName(entity), expParam.ids) as Iterable<? extends PersistentObjectI> :
				EntityUtil.getObjectsByQualifier(context, EntityUtil.entityClassForName(entity), expParam.expression) as Iterable<? extends PersistentObjectI>

		def template = ObjectSelect.query(ExportTemplate).where(
				ExportTemplate.ENTITY.eq(expParam.entity).andExp(ExportTemplate.KEY_CODE.eq(expParam.xslKeyCode))).selectOne(context)

		ExportResult result = new ExportResult()

		result.setResult(performExport(template, exportables).toString().bytes)

		return result
	}

	/**
	 * Generates export for the list of passed records using template provided.
	 *
	 * @param template export template to use for export
	 * @param records list of records to be exported
	 * @return {@link Writer} object containing export output
	 */
	def performExport(ExportTemplate template, Iterable<? extends PersistentObjectI> records, Map<String, Object> variables = [:], Boolean clipboardExport = false) {
		def groovyScriptEngine = scriptEngineManager.getEngineByName("groovy")

		def bindings = new SimpleBindings()

		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		CsvBuilder csv = new CsvBuilder(writer)
		if (clipboardExport) {
			csv.delimiter = '\t' as char
		}
		def json = new JsonBuilder(writer)

		template.options.each { opt ->
			bindings.put(opt.name, opt.objectValue)
		}

		variables.each {k,v ->
			bindings.put(k, v)
		}

		bindings.put(CONTEXT, context)
		bindings.put(OUTPUT, writer)
		bindings.put(RESULT, writer)

		bindings.put(XML, xml)
		bindings.put(JSON, json)
		bindings.put(CSV, csv)
		bindings.put(RECORDS, records)
		bindings.put(PREFERENCE, collegePreferenceService.prefHelper)

		groovyScriptEngine.eval(GroovyScriptService.DEFAULT_IMPORTS +
				String.format(GroovyScriptService.PREPARE_LOGGER, template.name) +
				template.script, bindings)

		return writer
	}

	/**
	 * Generates export for the list of passed records using template with provided keyCode.
	 *
	 * @param keyCode template's keyCode
	 * @param records list of records to be exported
	 * @return {@link Writer} object containing export output
	 */
	def performExport(String keyCode, List records) {
		def template = ObjectSelect.query(ExportTemplate).where(
				ExportTemplate.KEY_CODE.eq(keyCode)).selectOne(context)
		if (template) {
			return performExport(template, records)
		} else {
			throw new IllegalArgumentException("The export template with code '${keyCode}' was not found.")
		}
	}

	/**
	 * Script API method allowing calling export execution from groovy code.
	 */
	def export(@DelegatesTo(ExportSpec) Closure cl) {
		ExportSpec exportSpec = new ExportSpec()
		Closure build = cl.rehydrate(exportSpec, cl, this)
		build.setResolveStrategy(Closure.DELEGATE_FIRST)
		build.call()

		return performExport(exportSpec.templateKeyCode, exportSpec.entityRecords).toString()
	}
}
