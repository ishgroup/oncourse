@ignore
@parallel=false
Feature: Re-usable feature to get CSV without access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entityName = entity
        And request dataToExport
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to do prints. Please contact your administrator"

#        * def processId = $
#
#        Given path ishPathControl + '/' + processId
#        When method GET
#        Then status 200
#
#        * match $ == {"status":"#ignore","message":null}

##       <---> Pause:
#        * def sleep =
#             """
#             function(seconds){
#               for(i = 0; i <= seconds; i++)
#               {
#                 java.lang.Thread.sleep(1*200);
#                 karate.log(i);
#               }
#             }
#             """
#           * call sleep 1

#        Given path ishPathControl + '/' + processId
#        When method GET
#        Then status 200
#
#        * match $ == {"status":"#ignore","message":null}


#        Given path ishPath + '/' + processId
#        And param entityName = entity
#        When method GET
#        Then status 403
