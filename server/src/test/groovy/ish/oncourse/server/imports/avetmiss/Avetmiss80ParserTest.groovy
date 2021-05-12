package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileDynamic
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.Gender
import ish.common.types.UsiStatus
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static ish.common.types.AvetmissStudentIndigenousStatus.NEITHER
import static ish.common.types.AvetmissStudentLabourStatus.PART_TIME

class Avetmiss80ParserTest {
    private language
    private country


    @CompileDynamic
    private Avetmiss80Parser getParser(String text) {

        ObjectContext contextMock = mock(ObjectContext)
        AvetmissImportService parsersMock = mock(AvetmissImportService)
        when(parsersMock.parseNames("Castejon, Ed Karlvincent", 0))
                .thenReturn([firstName: "Ed", lastName: "Castejon", middleName: "Karlvincent"])
        when(parsersMock.parseHighestSchoolLevel(10)).thenReturn(COMPLETED_YEAR_10)
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
                highestSchoolLevel : COMPLETED_YEAR_10,
                indigenousStatus   : NEITHER,
                labourForceStatus  : PART_TIME,
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
