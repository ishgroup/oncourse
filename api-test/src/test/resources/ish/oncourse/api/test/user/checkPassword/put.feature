@parallel=false
Feature: Main feature for all PUT requests with path 'user/checkPassword'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/checkPassword'


    Scenario: (+) Check valid password

        Given path ishPath + '/' + 'AaLtR12'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 2


    Scenario: (+) Check valid password: special symbols: "§!@#$%^&*()_+=-[];:|><?{}±\"

        Given path ishPath + '/' + 'C2A7!402324255E26*()_2B3D-5B5D3B3A7C3E3C3F7B7DC2B15C'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 4


    Scenario: (+) Check very long valid password: 200 symbols

        Given path ishPath + '/' + 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A2'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 4


    Scenario: (-) Check short password: < 7 symbols

        Given path ishPath + '/' + 'GdUl5f'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 1
        And match response.feedback == "Add another word or two. Uncommon words are better."


    Scenario: (-) Check password: empty value

        Given path ishPath + '/' + ''
        And request {}
        When method PUT
        Then status 404


    Scenario: (-) Check password: only spaces

        Given path ishPath + '/' + '       '
        And request {}
        When method PUT
        Then status 200
        And match response.score == 0
        And match response.feedback == "Repeats like \"aaa\" are easy to guess."


    Scenario: (-) Check top-10 common password

        Given path ishPath + '/' + '1234567'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 0
        And match response.feedback == "This is a top-10 common password."


    Scenario: (-) Check too short password: < 7 symbols

        Given path ishPath + '/' + 'dGf4dg'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 1
        And match response.feedback == "Add another word or two. Uncommon words are better."


    Scenario: (-) Check password: Straight rows of keys:

        Given path ishPath + '/' + 'qwertyuiop[]'
        And request {}
        When method PUT
        Then status 200
        And match response.score == 1
        And match response.feedback == "Straight rows of keys are easy to guess."


    Scenario: (-) Check password: "value":null

        Given path ishPath + '/' + null
        And request {}
        When method PUT
        Then status 200
        And match response.score == 1
        And match response.feedback == "Names and surnames by themselves are easy to guess."


    Scenario: (-) Check password using only body

        Given path ishPath
        And request { value:"AasfggJ76JJhg" }
        When method PUT
        Then status 404

