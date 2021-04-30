package ish.oncourse.function

import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import static org.junit.Assert.assertEquals

@RunWith(Parameterized.class)
class GetContactFullNameTest {

    private String expectedResult

    private String firstName
    private String middleName
    private String lastName
    private boolean isCompany
    private boolean firstNameFirst

    @Parameters
    static Collection<Object[]> data() {
        Object[] data =  [
                ["Steve Rogers", "Steve", null, "Rogers", false, true],
                ["Rogers, Steve", "Steve", null, "Rogers", false, false],
                ["Steve Joseph Rogers", "Steve", "Joseph", "Rogers", false, true],
                ["Rogers, Steve Joseph", "Steve", "Joseph", "Rogers", false, false],
                ["Steve", "Steve", null, "Steve", false, true],
                ["Steve", "Steve", null, "Steve", false, false],
                ["Rogers", "Steve", null, "Rogers", true, true]
        ]
        Collection<Object[]> resultData = new ArrayList<>()
        for(Object currData : data) {
            resultData.add(currData as Object[])
        }
        return resultData
    }

    GetContactFullNameTest(String expectedResult,
                           String firstName,
                           String middleName,
                           String lastName,
                           boolean isCompany,
                           boolean firstNameFirst) {
        this.expectedResult = expectedResult
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.isCompany = isCompany
        this.firstNameFirst = firstNameFirst
    }

    @Test
    void test() {
        assertEquals(expectedResult,
                GetContactFullName.valueOf(firstName, middleName, lastName, isCompany, firstNameFirst).get())
    }
}
