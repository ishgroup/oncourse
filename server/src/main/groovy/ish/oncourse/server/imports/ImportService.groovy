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

package ish.oncourse.server.imports

import javax.inject.Inject
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.imports.ImportParameter
import ish.imports.ImportResult
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.model.ImportModel
import ish.oncourse.server.cayenne.Import
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.api.ImportSpec
import ish.oncourse.server.services.ISystemUserService
import ish.oncourse.server.services.TransactionLockedService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.script.ScriptEngineManager
import javax.script.ScriptException
import javax.script.SimpleBindings

@CompileStatic
class ImportService {

	private static final Logger logger = LogManager.getLogger(ImportService)

	private static final String SYSTEM_USERS_BY_ROLE = "getSystemUsersByRole"

	ScriptEngineManager scriptEngineManager
	ICayenneService cayenneService
	ISystemUserService systemUserService
	TransactionLockedService transactionLockedService

	@Inject
	ImportService(ICayenneService cayenneService, ISystemUserService systemUserService, TransactionLockedService transactionLockedService) {
		this.cayenneService = cayenneService
		this.systemUserService = systemUserService
		this.transactionLockedService = transactionLockedService
		this.scriptEngineManager = new ScriptEngineManager()
	}

	@CompileDynamic
	ImportResult performImport(ImportParameter parameter) {
		def context = cayenneService.newContext
		def imp = ObjectSelect.query(Import).where(Import.KEY_CODE.eq(parameter.keyCode)).selectOne(context)
		ImportModel model = new ImportModel()
		model.dbImport = imp
		parameter.data.each { entry ->
			model.addimportData(entry.key, entry.value)
		}

		return performImport(model, context)
	}

	@CompileDynamic
	ImportResult performImport(ImportModel model, ObjectContext context) {

		def groovyScriptEngine = scriptEngineManager.getEngineByName("groovy")

		List<? extends PersistentObject> createdRecords = []
		List<? extends PersistentObject> modifiedRecords = []

		def bindings = new SimpleBindings()

		bindings.put("context", context)
		bindings.put(SYSTEM_USERS_BY_ROLE, systemUserService)
		bindings.put('transactionLockedService', transactionLockedService)
		bindings.put("createdRecords", createdRecords)
		bindings.put("modifiedRecords", modifiedRecords)

		model.dbImport.options.each { option ->
			bindings.put(option.name, option.objectValue)
		}

		model.importData.each { data ->
			bindings.put(data.key, data.value)
		}

		try {
			groovyScriptEngine.eval(GroovyScriptService.DEFAULT_IMPORTS + model.dbImport.script, bindings)
		} catch (ScriptException e) {
			logger.error("Script execution error.", e)
			return new ImportResult(errorMessage: "Import script execution finished with exception:\n ${e.message}")
		} catch(all) {
			logger.error("Script execution error.", all)
			return new ImportResult(errorMessage: "Error encountered while executing import script:\n ${all.message}")
		}

		return new ImportResult(
				createdRecords: createdRecords.groupBy {
					record -> record.class.simpleName
				}.each {
					entry -> entry.value = entry.value.collect { record -> record.id }
				} as Map<String, List<Long>>,
				modifiedRecords: modifiedRecords.groupBy {
					record -> record.class.simpleName
				}.each {
					entry -> entry.value = entry.value.collect { record -> record.id }
				} as Map<String, List<Long>>
		)
	}

	static def generateValidationErrorMessage(ValidationException ve) {

		String validationMessages = ve.validationResult.failures.collect {
			failure -> failure.source instanceof PersistentObjectI ?
					"${failure.source.class.simpleName}: ${failure.description}" : failure.description
		}.join("\n")

		return "There was a problem importing your data.\n" +
				"This data could not be saved due to the following problem(s):\n" +
				"${validationMessages}\n\n" +
				"Please correct these problems and try again."
	}

	/**
	 * API method which allows to call perform data imports from groovy scripts. E.g.:
	 *
	 * <pre>importData {
	 * 	keyCode "ish.onCourse.import.avetmiss.student"
	 * 	avetmiss80 file("/home/user/data/NAT00080.txt")
	 * 	avetmiss85 file("/home/user/data/NAT00085.txt")
	 * }</pre>
	 */
	def importData(@DelegatesTo(ImportSpec) Closure cl) {
		ImportSpec spec = new ImportSpec()
		Closure build = cl.rehydrate(spec, cl, this)
		build.setResolveStrategy(Closure.DELEGATE_FIRST)
		build.call()

		return performImport(new ImportParameter(keyCode: spec.keyCode, data: spec.data))
	}
}
