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

package ish.oncourse.server.quality.api

import groovy.transform.CompileDynamic
import groovy.xml.MarkupBuilder
import ish.quality.QualityResult
/**
 * Helper containing API methods available in quality rule scripts.
 */
@CompileDynamic
class QualityResultHelper {

	/**
	 * Returns new {@link MarkupBuilder} instance with writer instance bound to it
	 */
    static MarkupBuilder builder() {
		def w = new StringWriter()
		def builder = new MarkupBuilder(w)
		builder.metaClass.writer = w
		return builder
	}

	def static result(@DelegatesTo(QualityResultSpec) Closure cl) {
		def resultSpec = new QualityResultSpec()
		def build = cl.rehydrate(resultSpec, cl, this)
		build.resolveStrategy = Closure.DELEGATE_FIRST
		build()

		resultSpec.recordsToFix.collect { entity, ids ->
			new QualityResult(
					description: resultSpec.messageResult,
					severity: resultSpec.severity,
					entity: entity,
					records: ids)
		}
	}
}
