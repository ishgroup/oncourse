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
        And params { name: 'testDoc8', description: 'some description', shared: true, access: 'Public', tags: [], fileName: 'testDoc8.1.txt' }
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def versionId = $.versions[0].id
        * print 'versionId =', versionId

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: 'testDoc8',
            tags: [],
            thumbnail: null,
            versions: [
                {
                    id: '#(versionId)',
                    current: true,
                    createdBy: 'onCourse Administrator',
                    fileName: 'testDoc8.1.txt',
                    mimeType: 'text/plain',
                    size: '10 b',
                },
                {
                    id: null,
                    current: false,
                    createdBy: 'onCourse Administrator',
                    mimeType: 'text/plain',
                    size: '800 b',
                    fileName: 'testDoc8.2.txt',
                    content: [55, 55, 55],
                }
            ],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: [],
        }
        """
        When method PUT
        Then status 204

        Given path ishPath + '/' + docId
        When method GET
        Then status 200
        And match $.versions[1].current == true
        And match $.versions[0].current == false


    Scenario: (+) Change current document version

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def newCurrentVersionId = $.versions[0].id
        * print 'newCurrentVersionId =', newCurrentVersionId
        * def oldCurrentVersionId = $.versions[1].id
        * print 'oldCurrentVersionId =', oldCurrentVersionId

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: 'testDoc8',
            tags: [],
            thumbnail: null,
            versions: [
                {
                    id: '#(oldCurrentVersionId)',
                    current: false,
                    createdBy: 'onCourse Administrator',
                    fileName: 'testDoc8.1.txt',
                    mimeType: 'text/plain',
                    size: '10 b',
                },
                {
                    id: '#(newCurrentVersionId)',
                    current: true,
                    createdBy: 'onCourse Administrator',
                    fileName: 'testDoc8.2.txt',
                    mimeType: 'text/plain',
                    content: [55, 55, 55],
                    size: '800 b',
                },
            ],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: [],
        }
        """
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

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def versionId = $.versions[1].id
        * print 'versionId =', versionId

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: 'testDoc8',
            tags: [],
            thumbnail: null,
            versions: [
                {
                    id: '#(versionId)',
                    current: false,
                    createdBy: 'onCourse Administrator',
                    fileName: 'testDoc8.1.txt',
                    mimeType: 'text/plain',
                    size: '10 b',
                },
            ],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: [],
        }
        """
        When method PUT
        Then status 400
        And match $.errorMessage == "At least one document version must be current."


    Scenario: (+) Try to delete all document versions. Validation error expected

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: 'testDoc8',
            tags: [],
            thumbnail: null,
            versions: [],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: [],
        }
        """
        When method PUT
        Then status 400
        And match $.errorMessage == 'At least one document version is required.'


    Scenario: (+) Try to add multiple current document versions. Validation error expected

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def version1Id = $.versions[0].id
        * print 'version1Id =', version1Id
        * def version2Id = $.versions[1].id
        * print 'version2Id =', version2Id

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: 'testDoc8',
            tags: [],
            thumbnail: null,
            versions: [
                {
                    id: '#(version1Id)',
                    current: true,
                    createdBy: 'onCourse Administrator',
                    fileName: 'testDoc8.1.txt',
                    mimeType: 'text/plain',
                    size: '10 b',
                },
                {
                    id: '#(version2Id)',
                    current: true,
                    createdBy: 'onCourse Administrator',
                    mimeType: 'text/plain',
                    size: '800 b',
                    fileName: 'testDoc8.2.txt',
                    content: [55, 55, 55],
                }
            ],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: [],
        }
        """
        When method PUT
        Then status 400
        And match $.errorMessage == 'Document cannot contain 2 current versions.'


    Scenario: (+) Try to upload a document version with null content. Validation error expected

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def versionId = $.versions[0].id
        * print 'versionId =', versionId

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: 'testDoc8',
            tags: [],
            thumbnail: null,
            versions: [
                {
                    id: '#(versionId)',
                    current: true,
                    createdBy: 'onCourse Administrator',
                    fileName: 'testDoc8.1.txt',
                    mimeType: 'text/plain',
                    size: '10 b',
                    content: [55, 55, 55]
                },
                {
                    id: null,
                    current: false,
                    createdBy: 'onCourse Administrator',
                    fileName: 'emptyDocument.txt',
                    mimeType: 'text/plain',
                    size: '0 b',
                    content: null
                }
            ],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: []
        }
        """
        When method PUT
        Then status 400
        And match $.errorMessage contains 'Your upload has failed. A least one version of document required.'


    Scenario: (+) Try to upload a duplicate document. Validation error expected

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def docId = get[0] response.rows[?(@.values == ['testDoc8'])].id
        * print 'docId =', docId

        Given path ishPath + '/' + docId
        When method GET
        Then status 200

        * def existingDoc = response
        * def existingDocVersion = get[0] existingDoc.versions[?(@.url != null)]
        * match existingDocVersion.url == '#string'

        * def filename = karate.extract(existingDocVersion.url, '[^/]+$', 0)
        * def dir = 'build/resources/tmp/'
        * karate.exec({ line: 'curl -o ' + filename + ' ' + existingDocVersion.url, workingDir: dir })
        * def bytes = karate.readAsBytes('file:' + dir + filename)

        # Here is a workaround to fit byte array into json
        * def byteList = []
        * karate.forEach(bytes, function(b, i){ byteList.push(b) })

        Given path ishPath + '/' + docId
        And request
        """
        {
            id: '#(docId)',
            name: '#(existingDoc.name)',
            tags: [],
            thumbnail: null,
            versions: [
                {
                    id: null,
                    current: true,
                    createdBy: 'onCourse Administrator',
                    fileName: 'duplicateContent.txt',
                    mimeType: 'text/plain',
                    size: '10 b',
                    content: '#(byteList)'
                }
            ],
            description: 'some description',
            access: 'Public',
            shared: true,
            removed: false,
            attachmentRelations: []
        }
        """
        When method PUT
        Then status 400
        And match $.errorMessage contains "Your upload has failed. The file you are trying to upload already exists as a document called '" + existingDoc.name + "' or its history."
