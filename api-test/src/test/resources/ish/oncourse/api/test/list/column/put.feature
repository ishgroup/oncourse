@parallel=false
Feature: Main feature for all PUT requests with path 'list/column'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'list/column'
        * def ishPathList = 'list'



    Scenario: (+) Update from 3- to 2-columns view

        * def updateTableModel =
        """
        {
            "sortings":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
            "columns":
                [
                {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":true,"width":100,"sortFields":[]}
                ],
            "layout":"Two column",
            "filterColumnWidth":261
        }
        """

        Given path ishPath
        And param entity = 'Qualification'
        And request updateTableModel
        When method PUT
        Then status 204

#       <--->  Assertion:
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.layout == "Two column"



    Scenario: (+) Update columns visibility

        * def updateTableModel =
        """
            {
                "sortings":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                    {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"width":100,"sortFields":[]}
                    ],
                "layout":"Two column",
                "filterColumnWidth":261
            }
        """

        Given path ishPath
        And param entity = 'Qualification'
        And request updateTableModel
        When method PUT
        Then status 204

#       <--->  Assertion:
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.columns ==
            """
                [
                {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"system":null,"width":100,"sortFields":[]}
                ]
            """


    Scenario: (+) Update columns order

        * def updateTableModel =
        """
        {
            "sortings":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
            "columns":
                [
                {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"width":100,"sortFields":[]}
                ],
            "layout":"Two column",
            "filterColumnWidth":261
        }
        """

        Given path ishPath
        And param entity = 'Qualification'
        And request updateTableModel
        When method PUT
        Then status 204

#       <--->  Assertion:
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.columns ==
            """
                [
                {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"system":null,"width":100,"sortFields":[]}
                ]
            """



    Scenario: (+) Update columns width

        * def updateTableModel =
        """
            {
                "sortings":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                    {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"width":195,"sortFields":[]},
                    {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"width":246,"sortFields":[]},
                    {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"width":0,"sortFields":[]}
                    ],
                "layout":"Two column",
                "filterColumnWidth":261
            }
        """

        Given path ishPath
        And param entity = 'Qualification'
        And request updateTableModel
        When method PUT
        Then status 204

#       <--->  Assertion:
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.columns ==
            """
                [
                {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[]},
                {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"system":null,"width":195,"sortFields":[]},
                {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"system":null,"width":246,"sortFields":[]},
                {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"system":null,"width":0,"sortFields":[]}
                ]
            """



    Scenario: (+) Update columns sorting

        * def updateTableModel =
        """
            {
                "sortings":[{"attribute":"title","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                    {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                    {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"width":195,"sortFields":[]},
                    {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"width":246,"sortFields":[]},
                    {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":false,"width":0,"sortFields":[]}
                    ],
                "layout":"Two column",
                "filterColumnWidth":261
            }
        """

        Given path ishPath
        And param entity = 'Qualification'
        And request updateTableModel
        When method PUT
        Then status 204

#       <--->  Assertion:
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.sort == [{"attribute":"title","ascending":true,"complexAttribute":[]}]



    Scenario: (+) Update left-side panel width

        * def updateTableModel =
        """
        {
            "sortings":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
            "columns":
                [
                {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"width":100,"sortFields":[]},
                {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":true,"width":100,"sortFields":[]}
                ],
            "layout":"Three column",
            "filterColumnWidth":200
        }
        """

        Given path ishPath
        And param entity = 'Qualification'
        And request updateTableModel
        When method PUT
        Then status 204

#       <--->  Assertion:
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.filterColumnWidth == 200








