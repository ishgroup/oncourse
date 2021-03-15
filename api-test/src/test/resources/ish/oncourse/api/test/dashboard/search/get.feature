@parallel=false
Feature: Main feature for all GET requests with path 'dashboard/search'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'dashboard/search'
        * def ishPathLogin = 'login'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get search result by admin

        * table getSearch

            | searchQuery   | dataToAssert                                      |
            | 'account'     | []                                                |
            | 'refund'      | [{"entity":"Contact","items":[{"id":19,"name":"student1 refund #13"}]},{"entity":"Enrolment","items":[{"id":109,"name":"student1 refund #13 course4-1"}]},{"entity":"Invoice","items":[{"id":27,"name":"student1 refund, #28"},{"id":25,"name":"student1 refund, #26"}]}]     |
            | 'course4-1'   | [{"entity":"CourseClass","items":[{"id":6,"name":"Course4 course4-1"}]},{"entity":"Enrolment","items":[{"id":114,"name":"student2 PaymentOut #18 course4-1"},{"id":110,"name":"student1 PaymentOut #14 course4-1"},{"id":109,"name":"student1 refund #13 course4-1"},{"id":107,"name":"stud4 #4 course4-1"},{"id":106,"name":"stud3 #3 course4-1"}]}]  |

        * call read('getSearchResult.feature') getSearch



    Scenario: (+) Get search result by notadmin

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * table getSearch

            | searchQuery   | dataToAssert                                      |
            | 'account'     | []                                                |
            | 'refund'      | [{"entity":"Contact","items":[{"id":19,"name":"student1 refund #13"}]}]     |
            | 'course4-1'   | [{"items":[{"name":"Course4 course4-1","id":6}],"entity":"CourseClass"}]  |

        * call read('getSearchResult.feature') getSearch

