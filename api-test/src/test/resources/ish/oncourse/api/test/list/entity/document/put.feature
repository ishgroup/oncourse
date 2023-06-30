@parallel=false
Feature: Main feature for PUT request with path 'list/entity/document/'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/document'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * def ishPathPlain = 'list/plain'


    Scenario: (+) Create new not current document version

#       <-----> Upload public document:
        * def someStream = read('testDoc8.1.txt')

        Given path ishPath
        And params {"name":"testDoc8","description":"some description","shared":true,"access":"Public","tags":[], "fileName":"testDoc8.1.txt"}
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ["testDoc8"])].id
        * print "docId = " + docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def versionId = $.versions[0].id
        * print "versionId = " + versionId

        * def documentToUpdate =
        """
        {
            "id": "#(docId)",
            "name": "testDoc8",
            "tags": [],
            "thumbnail": null,
            "versions": [
                {
                    "id": #(versionId),
                    "current": true,
                    "createdBy": "onCourse Administrator",
                    "fileName": "testDoc8.1.txt",
                    "mimeType": "text/plain",
                    "size": "10 b",
                },
                {
                    "id": null,
                    "current": false,
                    "createdBy": "onCourse Administrator",
                    "mimeType": "text/plain",
                    "size": "800 b",
                    "fileName": "testDoc8.2.txt",
                    "content": [55,55,55],
                }
            ],
            "description": "some description",
            "access": "Public",
            "shared": true,
            "removed": false,
            "attachmentRelations": [],
        }
        """

        Given path ishPath + '/' + docId
        And request documentToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + docId
        When method GET
        Then status 200
        And def versions = $.verions
        And match $.versions[1].current == true
        And match $.versions[0].current == false


    Scenario: (+) Change current document version

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ["testDoc8"])].id
        * print "docId = " + docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def newCurrentVersionId = $.versions[0].id
        * print "newCurrentVersionId = " + newCurrentVersionId
        * def oldCurrentVersionId = $.versions[1].id
        * print "oldCurrentVersionId = " + oldCurrentVersionId

        * def documentToUpdate =
        """
        {
            "id": "#(docId)",
            "name": "testDoc8",
            "tags": [],
            "thumbnail": null,
            "versions": [
                {
                    "id": #(oldCurrentVersionId),
                    "current": false,
                    "createdBy": "onCourse Administrator",
                    "fileName": "testDoc8.1.txt",
                    "mimeType": "text/plain",
                    "size": "10 b",
                },
                {
                    "id": #(newCurrentVersionId),
                    "current": true,
                    "createdBy": "onCourse Administrator",
                    "fileName": "testDoc8.2.txt",
                    "mimeType": "text/plain",
                    "size": "800 b",
                },
            ],
            "description": "some description",
            "access": "Public",
            "shared": true,
            "removed": false,
            "attachmentRelations": [],
        }
        """

        Given path ishPath + '/' + docId
        And request documentToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + docId
        When method GET
        Then status 200
        And def versions = $.verions
        And match $.versions[1].current == false
        And match $.versions[0].current == true


    Scenario: (+) Try to delete current document version. Validation error expected

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ["testDoc8"])].id
        * print "docId = " + docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def versionId = $.versions[1].id
        * print "versionId = " + versionId

        * def documentToUpdate =
        """
        {
            "id": "#(docId)",
            "name": "testDoc8",
            "tags": [],
            "thumbnail": null,
            "versions": [
                {
                    "id": #(versionId),
                    "current": false,
                    "createdBy": "onCourse Administrator",
                    "fileName": "testDoc8.1.txt",
                    "mimeType": "text/plain",
                    "size": "10 b",
                },
            ],
            "description": "some description",
            "access": "Public",
            "shared": true,
            "removed": false,
            "attachmentRelations": [],
        }
        """

        Given path ishPath + '/' + docId
        And request documentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Failed to delete the document version. The 'testDoc8.2.txt' is current and cannot be deleted unless you select a other version."


    Scenario: (+) Try to delete all document versions. Validation error expected

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ["testDoc8"])].id
        * print "docId = " + docId

        * def documentToUpdate =
        """
        {
            "id": "#(docId)",
            "name": "testDoc8",
            "tags": [],
            "thumbnail": null,
            "versions": [],
            "description": "some description",
            "access": "Public",
            "shared": true,
            "removed": false,
            "attachmentRelations": [],
        }
        """

        Given path ishPath + '/' + docId
        And request documentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == 'At least one document version is required.'