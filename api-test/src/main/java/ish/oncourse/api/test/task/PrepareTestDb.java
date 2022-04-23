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

package ish.oncourse.api.test.task;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrepareTestDb {

    private static final int DB_URL_ARG_INDEX = 0;
    private static final int DATASET_PATH_ARG_INDEX = 1;

    // Args structure:
    // args[0] - db url
    // args[1] - path to dataset file
    public static void main(String[] args) throws Exception {
        if (args.length != 2)
            throw new RuntimeException("Test data wasn't inserted. Invalid parameters count!");

        Connection connection = DriverManager.getConnection(args[DB_URL_ARG_INDEX]);
        IDatabaseConnection testDatabaseConnection = new DatabaseConnection(connection, null);
        testDatabaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);

        String tempDatasetPath = createAndGetPathOfTempDatasetBasedOn(args[DATASET_PATH_ARG_INDEX]);

        try {
            InputStream st = new FileInputStream(tempDatasetPath);
            FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setCaseSensitiveTableNames(false).setColumnSensing(true).build(st);
            DatabaseOperation.REFRESH.execute(testDatabaseConnection, dataSet);

            fillAbns(connection);

            try (var statement = connection.createStatement()) {
                statement.execute("UPDATE `SequenceSupport` " +
                        "set `nextId` =  (SELECT max(`invoiceNumber`) + 1 FROM `Invoice`) " +
                        "WHERE `tableName` = 'invoice'");

                statement.execute("UPDATE `SequenceSupport` " +
                        "set `nextId` =  (SELECT max(`studentNumber`) + 1 FROM `Student`) " +
                        "WHERE `tableName` = 'student'");
            }

        } finally {
            new File(tempDatasetPath).deleteOnExit();
        }
    }


    private static String createAndGetPathOfTempDatasetBasedOn(String datasetPath)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        var doc = readXml(datasetPath);
        String tempFileName = datasetPath + "-temp";
        Element documentElement = doc.getDocumentElement();
        var nodeList = documentElement.getChildNodes();

        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            replaceCustomAttributesMarkersWithValues(item);
        }

        writeXmlDoc(tempFileName, doc);

        return tempFileName;
    }

    private static Document readXml(String filePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new File(filePath));
    }

    private static void replaceCustomAttributesMarkersWithValues(Node item) {
        NamedNodeMap attributes = item.getAttributes();
        if (attributes == null)
            return;

        var customAttributes = CustomDatasetAttribute.values();

        int attributesLength = attributes.getLength();
        for (int j = 0; j < attributesLength; j++) {
            var attribute = attributes.item(j);
            var attributeValue = attribute.getNodeValue();
            for (var customAttribute : customAttributes) {
                var customAttributeMarker = customAttribute.getAttributeMarker();
                if (attributeValue.contains(customAttributeMarker)) {
                    String newAttributeValue = attributeValue.replaceAll(customAttributeMarker, customAttribute.getAttributeValue());
                    attribute.setNodeValue(newAttributeValue);
                }
            }
        }
    }

    private static void writeXmlDoc(String filePath, Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    private static void fillAbns(Connection connection){
        try(var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT id FROM CustomFieldType cft WHERE cft.fieldKey='ABN'");
            long customFieldTypeId = 1004;
            if(resultSet.next()){
                customFieldTypeId = resultSet.getLong(1);
            }else{
                statement.execute("INSERT INTO CustomFieldType (id,isMandatory,createdOn,modifiedOn,name,fieldKey,entityIdentifier,sortOrder) " +
                        "VALUES ("+customFieldTypeId+",1,'2019-09-20 20:50:36.83000000','2019-09-20 20:50:36.83000000','ABN','ABN','Contact',0)");
            }

            statement.execute("INSERT INTO CustomField (customFieldTypeId,id,createdOn,modifiedOn,value,foreignId,entityIdentifier) " +
                    "VALUES ("+customFieldTypeId+",2015,'2019-09-20 20:50:36.83000000','2019-09-20 20:50:36.83000000','111',21,'Contact')");
            statement.execute("INSERT INTO CustomField (customFieldTypeId,id,createdOn,modifiedOn,value,foreignId,entityIdentifier) " +
                    "VALUES ("+customFieldTypeId+",2016,'2019-09-20 20:50:36.83000000','2019-09-20 20:50:36.83000000','222',22,'Contact')");
            statement.execute("INSERT INTO CustomField (customFieldTypeId,id,createdOn,modifiedOn,value,foreignId,entityIdentifier) " +
                    "VALUES ("+customFieldTypeId+",2017,'2019-09-20 20:50:36.83000000','2019-09-20 20:50:36.83000000','555',24,'Contact')");
            statement.execute("INSERT INTO CustomField (customFieldTypeId,id,createdOn,modifiedOn,value,foreignId,entityIdentifier) " +
                    "VALUES ("+customFieldTypeId+",2018,'2019-09-20 20:50:36.83000000','2019-09-20 20:50:36.83000000','777',27,'Contact')");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
