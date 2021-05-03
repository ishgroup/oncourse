package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class Avetmiss85ParserTest {


    @CompileDynamic
    private Avetmiss85Parser getParser(String text) {

        ObjectContext contextMock = mock(ObjectContext)
        Language language = new Language()
        Country country = new Country()
        AvetmissImportService parsersMock = mock(AvetmissImportService)
        when(parsersMock.getCountryBy(5204)).thenReturn(country)

        when(parsersMock.getLanguageBy(6511)).thenReturn(language)


        InputLine line = new InputLine(text)
        Avetmiss85Parser parser = Avetmiss85Parser.valueOf(line, 0, contextMock)
        parser.service = parsersMock
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
