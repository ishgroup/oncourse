package ish.oncourse.server.imports.avetmiss

import groovy.mock.interceptor.MockFor
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class Avetmiss85ParserTest {
    private Language language = new Language()
    private Country country = new Country()

    @CompileDynamic
    private Avetmiss85Parser getParser(String text) {
        MockFor contextMock = new MockFor(ObjectContext)

        MockFor parsersMock = new MockFor(AvetmissImportService)
        parsersMock.ignore(~'parseNames')
        parsersMock.ignore(~'parseHighestSchoolLevel')
        parsersMock.ignore.getCountryBy { if (it == 5204) return country }
        parsersMock.ignore.getLanguageBy { if (it == 6511) return language }

        InputLine line = new InputLine(text)
        Avetmiss85Parser parser = Avetmiss85Parser.valueOf(line, 0, contextMock.proxyDelegateInstance())
        parser.service = parsersMock.proxyDelegateInstance()
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
