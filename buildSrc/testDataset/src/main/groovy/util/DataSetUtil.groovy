/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package util

import model.CustomDatasetAttribute
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DataSetUtil {

    static String createAndGetPathOfTempDatasetBasedOn(String datasetPath)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        def doc = readXml(datasetPath)
        String tempFileName = datasetPath + "-temp"
        Element documentElement = doc.getDocumentElement()
        def nodeList = documentElement.getChildNodes()

        int length = nodeList.getLength()
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i)
            replaceCustomAttributesMarkersWithValues(item)
        }

        writeXmlDoc(tempFileName, doc)

        return tempFileName
    }

    private static Document readXml(String filePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        return builder.parse(new File(filePath))
    }

    private static void replaceCustomAttributesMarkersWithValues(Node item) {
        NamedNodeMap attributes = item.getAttributes()
        if (attributes == null)
            return

        def customAttributes = CustomDatasetAttribute.values()

        int attributesLength = attributes.getLength()
        for (int j = 0; j < attributesLength; j++) {
            def attribute = attributes.item(j)
            def attributeValue = attribute.getNodeValue()
            for (def customAttribute : customAttributes) {
                def customAttributeMarker = customAttribute.getAttributeMarker()
                if (attributeValue.contains(customAttributeMarker)) {
                    String newAttributeValue = attributeValue.replaceAll(customAttributeMarker, customAttribute.getAttributeValue())
                    attribute.setNodeValue(newAttributeValue)
                }
            }
        }
    }

    private static void writeXmlDoc(String filePath, Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance()
        Transformer transformer = transformerFactory.newTransformer()
        DOMSource source = new DOMSource(document)
        StreamResult result = new StreamResult(new File(filePath))
        transformer.transform(source, result)
    }
}
