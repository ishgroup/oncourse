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

import groovy.transform.CompileDynamic
import org.supercsv.io.CsvListReader
import org.supercsv.prefs.CsvPreference

@CompileDynamic
class CsvParser {

	char quote = '"'
    int delimiter = ','
    String endOfLine = "\r\n"

    Reader input
    CsvListReader csvListReader

	def header

	CsvParser(Reader input) {
		this.input = input
		this.csvListReader = new CsvListReader(input, new CsvPreference.Builder(this.quote, this.delimiter, this.endOfLine).build())
		this.header = csvListReader.getHeader(true)
	}

    Map<String, String> readLine() {
		def values = readLineValues()
		values ? [header, values].transpose().collectEntries { it } : null
	}

    List<String> readLineValues() {
		csvListReader.read()
	}

	def eachLine(Closure cl) {
		def line
		while ((line = readLine()) != null) {
			cl(line)
		}
	}

	def setDelimiter(int delimiter) {
		this.delimiter = delimiter
		this.csvListReader = new CsvListReader(input, new CsvPreference.Builder(quote, delimiter, endOfLine).build())
		this.header = csvListReader.getHeader(true)
	}

	def setQuote(char quote) {
		this.quote = quote
		this.csvListReader = new CsvListReader(input, new CsvPreference.Builder(quote, delimiter, endOfLine).build())
		this.header = csvListReader.getHeader(true)
	}

	def setEndOfLine(String endOfLine) {
		this.endOfLine = endOfLine
		this.csvListReader = new CsvListReader(input, new CsvPreference.Builder(quote, delimiter, endOfLine).build())
		this.header = csvListReader.getHeader(true)
	}
}
