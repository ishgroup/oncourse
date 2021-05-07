package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock

@CompileStatic
class Avetmiss85ParserTest {


    @CompileDynamic
    private Avetmiss85Parser getParser(String text) {

        ObjectContext contextMock = mock(ObjectContext)

        InputLine line = new InputLine(text)
        Avetmiss85Parser parser = Avetmiss85Parser.valueOf(line, 0, contextMock)
        return parser
    }

    
    @Test
    void test() {

        def text = "0000000205MR  ED KARLVINCENT                          CASTEJON                                                                                                                31             Crowea Street                                                                               MANJIMUP                                          62580597771365                                                    vincentcastejon@kearnan.wa.edu.au                                               "

        def result = getParser(text).parse()

        def expected = [
                clientId   : "0000000205",
                firstName  : "ED",
                lastName   : "CASTEJON",
                middleName : "KARLVINCENT",
                street     : "31, Crowea Street",
                suburb     : "MANJIMUP",
                postcode   : "6258",
                homePhone  : "97771365",
                workPhone  : "",
                mobilePhone: "",
                email      : "vincentcastejon@kearnan.wa.edu.au"
        ]
        Assertions.assertEquals(expected, result, "Wrong values:" + expected*.key.findAll { expected[it] != result[it] })
    }

}
