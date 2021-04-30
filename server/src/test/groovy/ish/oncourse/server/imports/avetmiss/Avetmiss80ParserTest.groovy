package ish.oncourse.server.imports.avetmiss

import groovy.mock.interceptor.MockFor
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.Gender
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import java.time.LocalDate

import static ish.common.types.AvetmissStudentEnglishProficiency.VERY_WELL
import static ish.common.types.AvetmissStudentIndigenousStatus.NEITHER
import static ish.common.types.AvetmissStudentLabourStatus.PART_TIME
import static ish.common.types.AvetmissStudentSchoolLevel.COMPLETED_YEAR_10

class Avetmiss80ParserTest {
    private language
    private country

    private Avetmiss80Parser getParser(String text) {
        MockFor contextMock = new MockFor(ObjectContext)
        language = new Language()
        country = new Country()

        MockFor parsersMock = new MockFor(AvetmissImportService)
        parsersMock.ignore(~'parseNames')
        parsersMock.ignore(~'parseHighestSchoolLevel')
        parsersMock.ignore.getCountryBy { if (it == 5204) return country }
        parsersMock.ignore.getLanguageBy { if (it == 6511) return language }

        InputLine line = new InputLine(text)
        Avetmiss80Parser parser = Avetmiss80Parser.valueOf(line, 0, contextMock.proxyDelegateInstance())
        parser.service = parsersMock.proxyDelegateInstance()
        return parser
    }

    @Test
    void test() {

        def text = "0000000205Castejon, Ed Karlvincent                                    102014M20111999625846511025204NNY1Manjimup                                          4YLJ9VMZU705                                                                                31             Crowea Street                                                                             "

        def parser = getParser(text)
        def result = parser.parse()

        def expected = [
                clientId           : "0000000205",
                firstName          : "Ed",
                lastName           : "Castejon",
                middleName         : "Karlvincent",
                gender             : Gender.MALE,
                birthDate          : LocalDate.of(1999, 11, 20),
                postcode           : "6258",
                suburb             : "Manjimup",
                language           : language,
                highestSchoolLevel : COMPLETED_YEAR_10,
                yearSchoolCompleted: 2014,
                indigenousStatus   : NEITHER,
                labourForceStatus  : PART_TIME,
                countryOfBirth     : country,
                disabilityType     : AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION,
                priorEducationCode : AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION,
                isStillAtSchool    : true,
                englishProficiency : VERY_WELL,
        ]

        Assertions.assertEquals("Wrong values:" + expected*.key.findAll { expected[it] != result[it] }, expected, result)
    }
}
