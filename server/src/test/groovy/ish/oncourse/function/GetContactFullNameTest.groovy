package ish.oncourse.function

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@CompileStatic
class GetContactFullNameTest {

    static Collection<Arguments> values() {
       Object[][] data = [
                ["Steve Rogers", "Steve", null, "Rogers", false, true],
                ["Rogers, Steve", "Steve", null, "Rogers", false, false],
                ["Steve Joseph Rogers", "Steve", "Joseph", "Rogers", false, true],
                ["Rogers, Steve Joseph", "Steve", "Joseph", "Rogers", false, false],
                ["Steve", "Steve", null, "Steve", false, true],
                ["Steve", "Steve", null, "Steve", false, false],
                ["Rogers", "Steve", null, "Rogers", true, true]
        ] as Object[][]
        
        Collection<Arguments> resultData = new ArrayList<>()
        for (Object[] test : data) {
            resultData.add(Arguments.of(test[0], test[1], test[2], test[3], test[4], test[5]))
        }
        return resultData
    }

    @ParameterizedTest
    @MethodSource("values")
    void test(String expectedResult, String firstName, String middleName, String lastName,  boolean isCompany, boolean firstNameFirst) {
        Assertions.assertEquals(expectedResult,
                GetContactFullName.valueOf(firstName, middleName, lastName, isCompany, firstNameFirst).get())
    }
}
