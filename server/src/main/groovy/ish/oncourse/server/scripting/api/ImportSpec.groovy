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

package ish.oncourse.server.scripting.api

import groovy.transform.CompileDynamic
import org.apache.commons.io.FileUtils

@CompileDynamic
class ImportSpec {
	String keyCode
    Map<String, byte[]> data = [:]

	def keyCode(String keyCode) {
		this.keyCode = keyCode
	}

	def methodMissing(String name, args) {
		def arg = args.find()

		if (arg instanceof byte[]) {
			data.put(name, arg)
		} else if (arg instanceof String) {
			data.put(name, arg.bytes)
		} else {
			throw new IllegalArgumentException("Unsupport argument type: ${arg.class}")
		}
	}

    static byte[] file(String fileName) {
		return FileUtils.readFileToByteArray(new File(fileName))
	}
}
