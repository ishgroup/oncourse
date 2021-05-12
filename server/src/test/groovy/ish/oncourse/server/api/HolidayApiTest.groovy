package ish.oncourse.server.api

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.api.v1.model.HolidayDTO
import ish.oncourse.server.api.v1.model.RepeatEndEnumDTO
import ish.oncourse.server.api.v1.model.RepeatEnumDTO
import ish.oncourse.server.api.v1.service.HolidayApi
import ish.oncourse.server.api.v1.service.impl.HolidayApiImpl
import ish.oncourse.server.cayenne.UnavailableRule
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static ish.util.LocalDateUtils.stringToValue

@CompileStatic
@DatabaseSetup
class HolidayApiTest extends CayenneIshTestCase {

    @Test
    void testHoliday() {
        ICayenneService cayenneService = injector.getInstance(ICayenneService)
        UnavailableRuleDao dao = injector.getInstance(UnavailableRuleDao)

        HolidayApi api = new HolidayApiImpl()
        api.cayenneService = cayenneService
        api.unavailableRuleDao = dao

        HolidayDTO newYearHoliday = new HolidayDTO().with { newYear ->
            newYear.description = 'newYear'
            newYear.startDate = stringToValue("1991-11-05")
            newYear.endDate = stringToValue("1991-11-06")
            newYear.repeat = RepeatEnumDTO.YEAR
            newYear.repeatEnd = RepeatEndEnumDTO.ONDATE
            newYear.repeatOn = stringToValue("1999-11-05")
            newYear
        }

        HolidayDTO easterHoliday = new HolidayDTO().with { easter ->
            easter.description = 'easter'
            easter.startDate = stringToValue("1992-11-05")
            easter.endDate = stringToValue("1992-11-06")
            easter.repeat = RepeatEnumDTO.YEAR
            easter.repeatEnd = RepeatEndEnumDTO.AFTER
            easter.repeatEndAfter = BigDecimal.valueOf(2)
            easter
        }

        HolidayDTO firstMayHoliday = new HolidayDTO().with { firstMay ->
            firstMay.description = 'firstMay'
            firstMay.startDate = stringToValue("1993-11-05")
            firstMay.endDate = stringToValue("1993-11-06")
            firstMay.repeat = RepeatEnumDTO.YEAR
            firstMay.repeatEnd = RepeatEndEnumDTO.NEVER
            firstMay
        }

        api.update([newYearHoliday, easterHoliday, firstMayHoliday])


        List<UnavailableRule> persistRecords = ObjectSelect.query(UnavailableRule).select(cayenneService.newContext)
        Assertions.assertEquals(3, persistRecords.size())


        List<HolidayDTO> holidays = api.get()
        Assertions.assertEquals(3, holidays.size())

        assertHoliday(newYearHoliday, holidays[0])
        assertHoliday(easterHoliday, holidays[1])
        assertHoliday(firstMayHoliday, holidays[2])

    }

    
    private static void assertHoliday(HolidayDTO expect, HolidayDTO actual) {
        Assertions.assertEquals(expect.description, actual.description)
        Assertions.assertEquals(expect.startDate, actual.startDate)
        Assertions.assertEquals(expect.endDate, actual.endDate)
        Assertions.assertEquals(expect.repeat, actual.repeat)
        Assertions.assertEquals(expect.repeatEnd, actual.repeatEnd)
        if (expect.repeatEndAfter && actual.repeatEndAfter) {
            Assertions.assertEquals(expect.repeatEndAfter, actual.repeatEndAfter)
        } else if (expect.repeatEndAfter != actual.repeatEndAfter) {
            assert false
        }

        Assertions.assertEquals(expect.repeatOn, actual.repeatOn)
    }

}
