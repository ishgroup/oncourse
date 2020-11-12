@ignore
@parallel=false
Feature: re-usable feature to remove entity from DB
    
    Scenario:
        * url 'https://127.0.0.1:8182/a/v1'
        * def data = {path: '#(path)', name: '#(entityName)'}
        Given path data.path
        When method GET
        Then status 200
        And def entity = karate.jsonPath(response, "[?(@.name=='" + data.name + "')]")[0]
    
        Given path data.path + "/" + entity.id
        When method DELETE
        Then status 204