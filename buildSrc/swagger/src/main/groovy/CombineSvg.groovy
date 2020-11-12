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

import groovy.xml.StreamingMarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
class CombineSvg extends DefaultTask {

    @Input
    String projectName

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    String outputDirectoryName

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    String imgDirectoryName

    @TaskAction
    void combineSvg() {
        def imgDirectory = new File(imgDirectoryName)
        if (imgDirectory.exists()) {
            def xmlParser = new XmlParser()
            def xml = new StreamingMarkupBuilder().with {
                it.encoding = 'UTF-8'
                it
            }.bind {
                mkp.xmlDeclaration()
                svg(xmlns: 'http://www.w3.org/2000/svg', version: '1.2', baseProfile: 'tiny', x: '0px', y: '0px', width: '128px', height: '128px', viewBox: '0 0 128 128',
                        'xml:space': 'preserve', 'xmlns:xml': 'http://www.w3.org/XML/1998/namespace') {
                    defs(xmlns: "http://www.w3.org/2000/svg") {
                        style {
                            mkp.yieldUnescaped("<![CDATA[.sprite { display: none; }.sprite:target { display: inline; }]]>")
                        }
                    }
                }
            }
            def result = xmlParser.parseText(xml.toString())

            imgDirectory.eachFile {
                if (it.name.endsWith('.svg')) {
                    def node = xmlParser.parse(it)
                    def imageNode = node.children().get(0)
                    imageNode.@class = 'sprite'
                    result.append(imageNode)
                }
            }

            new File(outputDirectoryName).mkdir()
            new File(outputDirectoryName + '/img').mkdir()
            def outputFileName = outputDirectoryName + "/img/" + projectName + ".svg"
            def outputFile = new File(outputFileName)
            outputFile.createNewFile()

            def out = new PrintWriter(outputFile, 'UTF-8')
            out.write('<?xml version="1.0" encoding="utf-8"?>\n')
            new XmlNodePrinter(out).with {
                preserveWhitespace = true
                expandEmptyElements = true
                quote = '\''
                it
            }.print(result)
        }
    }
}
