import groovy.xml.StreamingMarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public class CombineSvg extends DefaultTask {

    @Input
    def String projectName

    @Input
    def String outputDirectoryName

    @Input
    def String imgDirectoryName

    @TaskAction
    def void run() {
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
            
            new File(outputDirectoryName + '/img').mkdirs()
            def outputFileName = "${outputDirectoryName}/img/${projectName}.svg"
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