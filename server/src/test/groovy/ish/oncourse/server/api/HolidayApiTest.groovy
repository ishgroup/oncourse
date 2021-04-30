package ish.oncourse.server.api

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.api.v1.model.HolidayDTO
import ish.oncourse.server.api.v1.model.RepeatEndEnumDTO
import ish.oncourse.server.api.v1.model.RepeatEnumDTO
import ish.oncourse.server.api.v1.service.HolidayApi
import ish.oncourse.server.api.v1.service.impl.HolidayApiImpl
import ish.oncourse.server.cayenne.UnavailableRule
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Test
import org.testng.annotations.BeforeTest

import static ish.util.LocalDateUtils.stringToValue
import static org.junit.Assert.assertEquals

class HolidayApiTest extends CayenneIshTestCase {


    @BeforeTest
    void before() {
        wipeTables()
    }
    
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
            newYear.repeatEnd =  RepeatEndEnumDTO.ONDATE
            newYear.repeatOn =  stringToValue("1999-11-05")
            newYear
        }

        HolidayDTO easterHoliday = new HolidayDTO().with { easter ->
            easter.description = 'easter'
            easter.startDate = stringToValue("1992-11-05")
            easter.endDate = stringToValue("1992-11-06")
            easter.repeat = RepeatEnumDTO.YEAR
            easter.repeatEnd =  RepeatEndEnumDTO.AFTER
            easter.repeatEndAfter = 2
            easter
        }

        HolidayDTO firstMayHoliday = new HolidayDTO().with { firstMay ->
            firstMay.description = 'firstMay'
            firstMay.startDate = stringToValue("1993-11-05")
            firstMay.endDate = stringToValue("1993-11-06")
            firstMay.repeat = RepeatEnumDTO.YEAR
            firstMay.repeatEnd =  RepeatEndEnumDTO.NEVER
            firstMay
        }
        
        api.update([newYearHoliday, easterHoliday, firstMayHoliday])
        
        
        List<UnavailableRule> persistRecords = ObjectSelect.query(UnavailableRule).select(cayenneService.newContext)
        assertEquals(3, persistRecords.size())


        List<HolidayDTO> holidays = api.get()
        assertEquals(3, holidays.size())

        assertHoliday(newYearHoliday, holidays[0])
        assertHoliday(easterHoliday, holidays[1])
        assertHoliday(firstMayHoliday, holidays[2])
        
    }
    
    private static void assertHoliday(HolidayDTO expect, HolidayDTO actual) {
        assertEquals(expect.description, actual.description)
        assertEquals(expect.startDate, actual.startDate)
        assertEquals(expect.endDate, actual.endDate)
        assertEquals(expect.repeat, actual.repeat)
        assertEquals(expect.repeatEnd, actual.repeatEnd)
        if (expect.repeatEndAfter && actual.repeatEndAfter) {
            assertEquals(expect.repeatEndAfter, actual.repeatEndAfter, 0)
        } else if (expect.repeatEndAfter != actual.repeatEndAfter) {
            assert false
        }
        
        assertEquals(expect.repeatOn, actual.repeatOn)
    }
    
}
