@parallel=false
Feature: Main feature for all requests for special tags (tag/special)

  Background: Authorize first
    * configure headers = { Authorization: 'admin' }
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPath = 'tag/special'
    * def ishSpecialGetPath = 'tag/special'


  Scenario: (-) Create valid special tags group

    * def newTagGroup =
        """
        {"childTags":[{"id":null,"name":"third course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]},{"id":null,"name":"second course type","type":"Tag","status":"Show on website","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]},{"id":null,"name":"first course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]}],"specialType":"Course extended types"}
        """

    Given path ishPath
    And request newTagGroup
    When method POST
    Then status 204

    Given path ishSpecialGetPath+"?entityName=Course"
    When method GET
    Then status 200
    And match karate.sizeOf(response.childTags) == 3
    And match response contains
    """
      {
    "specialType": "Course extended types",
    "childTags": [
        {
            "id": #ignore,
            "name": "first course type",
            "status": "Private",
            "type": "Tag",
            "system": true,
            "urlPath": null,
            "content": null,
            "color": "74add1",
            "weight": null,
            "taggedRecordsCount": 0,
            "childrenCount": 0,
            "created": #ignore,
            "modified": #ignore,
            "requirements": [],
            "childTags": []
        },
        {
            "id": #ignore,
            "name": "second course type",
            "status": "Show on website",
            "type": "Tag",
            "system": true,
            "urlPath": null,
            "content": null,
            "color": "74add1",
            "weight": null,
            "taggedRecordsCount": 0,
            "childrenCount": 0,
            "created": #ignore,
            "modified": #ignore,
            "requirements": [],
            "childTags": []
        },
        {
            "id": #ignore,
            "name": "third course type",
            "status": "Private",
            "type": "Tag",
            "system": true,
            "urlPath": null,
            "content": null,
            "color": "74add1",
            "weight": null,
            "taggedRecordsCount": 0,
            "childrenCount": 0,
            "created": #ignore,
            "modified": #ignore,
            "requirements": [],
            "childTags": []
        }]
      }
    """

    * def secondTypeId = get[0] response.childTags[?(@.name == 'second course type')].id
    * def thirdTypeId = get[0] response.childTags[?(@.name == 'third course type')].id

    * def updateGroup =
    """
    {"childTags":[{"id":"#(thirdTypeId)","name":"third course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]},{"id":"#(secondTypeId)","name":"second course type","type":"Tag","status":"Show on website","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]}],"specialType":"Course extended types"}
    """


    Given path ishPath
    And request updateGroup
    When method POST
    Then status 204

#       >>> Assertion:
    Given path ishSpecialGetPath+"?entityName=Course"
    When method GET
    Then status 200
    And match response contains
      """
          {
    "specialType": "Course extended types",
    "childTags": [
        {
            "id": #ignore,
            "name": "second course type",
            "status": "Show on website",
            "type": "Tag",
            "system": true,
            "urlPath": null,
            "content": null,
            "color": "74add1",
            "weight": null,
            "taggedRecordsCount": 0,
            "childrenCount": 0,
            "created": #ignore,
            "modified": #ignore,
            "requirements": [],
            "childTags": []
        },
        {
            "id": #ignore,
            "name": "third course type",
            "status": "Private",
            "type": "Tag",
            "system": true,
            "urlPath": null,
            "content": null,
            "color": "74add1",
            "weight": null,
            "taggedRecordsCount": 0,
            "childrenCount": 0,
            "created": #ignore,
            "modified": #ignore,
            "requirements": [],
            "childTags": []
        }
    ]
}
  """

  Scenario: (-) Remove  special tag group

    * def newTagGroup =
        """
        {"childTags":[],"specialType":"Course extended types"}
        """

    Given path ishPath
    And request newTagGroup
    When method POST
    Then status 204

#       >>> Assertion:
    Given path ishSpecialGetPath+"?entityName=Course"
    When method GET
    Then status 204


  Scenario: (-) Add wrong special tag group

    * def newTagGroup =
        """
        {"childTags":[{"id":null,"name":"third course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]},{"id":null,"name":"second course type","type":"Tag","status":"Show on website","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]},{"id":null,"name":"first course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]}],"specialType":null}
        """

    Given path ishPath
    And request newTagGroup
    When method POST
    Then status 400
    And match response.errorMessage == "You can edit only special tags with this endpoint"


  Scenario: (-) Add wrong tag group with childs of childs

    * def newTagGroup =
        """
        {"childTags":[{"id":null,"name":"third course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[{"id":null,"name":"second course type","type":"Tag","status":"Show on website","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]},{"id":null,"name":"first course type","type":"Tag","status":"Private","system":false,"urlPath":null,"content":"","weight":1,"taggedRecordsCount":0,"created":null,"modified":null,"color":"74add1","requirements":[],"childTags":[]}]}],"specialType":"Course extended types"}
        """

    Given path ishPath
    And request newTagGroup
    When method POST
    Then status 400
    And match response.errorMessage == "Special tags cannot have second level of hierarchy"