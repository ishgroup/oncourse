package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileStatic
import ish.common.types.*
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class Avetmiss80ParserTest {
    Language language
    Country country

    private Avetmiss80Parser getParser(String text) {
        ObjectContext contextMock = mock(ObjectContext)
        AvetmissImportService parsersMock = mock(AvetmissImportService)
        when(parsersMock.parseNames("Castejon, Ed Karlvincent", 0))
                .thenReturn([firstName: "Ed", lastName: "Castejon", middleName: "Karlvincent"])
        when(parsersMock.parseHighestSchoolLevel(10)).thenReturn(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10)
        when(parsersMock.getCountryBy(5204)).thenReturn(country)
        when(parsersMock.getLanguageBy(6511)).thenReturn(language)

        InputLine line = new InputLine(text)
        Avetmiss80Parser parser = Avetmiss80Parser.valueOf(line, 0, contextMock)
        parser.service = parsersMock
        return parser
    }

    @Test
    void test() {
        def text = "0000000205Castejon, Ed Karlvincent                                    10M20111999625846511025204NNYManjimup                                          4YLJ9VMZU705                                                                                31             Crowea Street                                                                             "

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
                highestSchoolLevel : AvetmissStudentSchoolLevel.COMPLETED_YEAR_10,
                indigenousStatus   : AvetmissStudentIndigenousStatus.NEITHER,
                labourForceStatus  : AvetmissStudentLabourStatus.PART_TIME,
                countryOfBirth     : country,
                disabilityType     : AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION,
                priorEducationCode : AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION,
                isStillAtSchool    : true,
                usi                : "4YLJ9VMZU7",
                usiStatus          : UsiStatus.DEFAULT_NOT_SUPPLIED,
                street             : "31, Crowea Street",
        ]

        Assertions.assertEquals(expected, result,"Wrong values:" + expected*.key.findAll { expected[it] != result[it] })
    }
}
