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

import ish.oncourse.API
import org.supercsv.io.CsvListWriter
import org.supercsv.prefs.CsvPreference

/**
 * This class is a simple wrapper to make exporting CSV and other formats really easy.
 * Use it like this within an export script:
 *
 * ```
 * csv << [
 * 	"first name": contact.firstName
 * 	"last name": contact.lastName
 * 	"student number": contact.student.studentNumber
 * ]
 * ```
 *
 * If you want to change the delimiters, quote or end of line, you should do that before you call `csv << []`
 *
 * Note that this builder is really flexible and isn't just for CSV. Use it for any output with consistent
 * separators between fields.
 *
 */
@API
class CsvBuilder {

	private char quote = '"'
	private int delimiter = ',' as char
	private String endOfLine = "\r\n"

    Writer output
    CsvListWriter csvListWriter

    boolean writeHeader = true

    CsvBuilder(Writer output) {
		this.output = output
	}

	/**
	 * Appends new row to builder's output. Instead of calling this method by name,
     * you can use the symbol "<<" as seen in the example above.
     *
     * If you call this more than once, the header is automatically disabled for the second
     * and subsequent calls.
	 *
	 * @param row mapping of column names to values
	 */
    @API
    void addRow(Map row) {

        if (this.csvListWriter == null) {
            this.csvListWriter = new CsvListWriter(output,
                    new CsvPreference.Builder(this.quote, this.delimiter, this.endOfLine).build())
        }

		if (writeHeader) {
			this.csvListWriter.writeHeader(row.keySet() as String[])
			writeHeader = false
		}
		this.csvListWriter.write(row.values() as String[])
		this.csvListWriter.flush()
	}

	def leftShift(Map row) {
		addRow(row)
	}

    /**
     * Use this to disable the header line
     *
     * csv.enableHeader = false
     *
     * @param writeHeader defaults to true
     * @return
     */
    @API
    void enableHeader(boolean writeHeader) {
        this.writeHeader = writeHeader
    }

    /**
     * Set the delimiter to something else.
     *
     * csv.delimiter = ';'
     *
     * @param delimiter defaults to ,
     */
    @API
    void setDelimiter(int delimiter) {
		this.delimiter = delimiter
	}

    /**
     * Set the quote to something else.
     *
     * csv.quote = '|'
     *
     * @param quote defaults to a "
     */
    @API
    void setQuote(char quote) {
		this.quote = quote
	}

    /**
     * Set the end of line to something else.
     *
     * csv.endOfLine = '\n'
     *
     * @param endOfLine defaults to a \r\n
     */
    @API
    void setEndOfLine(String endOfLine) {
		this.endOfLine = endOfLine
	}

}
