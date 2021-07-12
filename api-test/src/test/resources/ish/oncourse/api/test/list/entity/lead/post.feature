@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/lead'

  Background: Authorize first
    * configure headers = { Authorization: 'admin' }
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPath = 'list/entity/lead'
    * def ishPathLogin = 'login'
    * def ishPathPlain = 'list/plain'


  Scenario: (+) Create lead

    * def newLead =
        """
        {
        "studentName":"student for lead",
        "contactId": 33,
        "estimatedValue": null,
        "status": "Open",
        "studentCount":1,
        "nextActionOn":"2021-07-10T12:30:00.000Z",
        "notify":false,
        "relatedSellables":[
          {"id":112,"active":true,"name":"FirstLeadCourse","code":"lead1","type":"Course"},
          {"id":1010,"active":true,"name":"Exclusive Lead Voucher","code":"LFV1","type":"Voucher"}
        ],
        "tags": [
          {"id":239,"name":"leads 1","status":"Private","system":false,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":1,"childrenCount":0,"created":"2019-06-05T14:45:50.000Z","modified":"2019-06-05T14:47:01.000Z","requirements":[],"childTags":[]}
        ],
        "sites":[
          {"id":203,"name":"Default site 3","suburb":"Adelaide","postcode":"5001"}
        ],
        "documents": [
          {"id": 302,"name": "defaultPublicDocument2","description": "Public description","access": "Public","shared": true,"removed": false, "attachmentRelations": [],
            "versions": [
              {"id": 302,"added": "2021-07-06T07:14:11.000Z","createdBy": "UserWithRightsCreate UserWithRightsCreate","fileName": "defaultPublicDocument2.txt","mimeType": "text/plain"}
              ],
          }
        ]
        }
        """

    Given path ishPath
    And request newLead
    When method POST
    Then status 204

    Given path ishPathPlain
    And param entity = 'Lead'
    And param search = 'customer.id = 33'
    And param columns = 'id'
    When method GET
    Then status 200

    * def id = response.rows[0].id
    * print "id = " + id

#    <---> Assertion:
    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $.contactId == 33
    And match $.status == 'Open'
    And match $.estimatedValue == 350.0
    And match $.assignTo == "admin@gmail.com"
    And match $.tags == "#[1]"
    And match $.relatedSellables == "#[2]"
    And match $.sites == "#[1]"
    And match $.documents == "#[1]"

#    <---> Remove:

    Given path ishPath + '/' + id
    When method DELETE
    Then status 204