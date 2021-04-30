package ish.oncourse.server

import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISystemUserService
import org.apache.cayenne.access.DataContext
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.junit.Assert.assertEquals
import static org.mockito.Matchers.any
import static org.mockito.Mockito.when

class PreferenceControllerTest {

    @Test
    void testSetValue() throws Exception {
        Preference preference = new Preference()
        PreferenceController preferenceController = initPreferenceController(preference)

        preferenceController.setValue("testKey", false, "testValue")
        assertEquals("testValue", preference.getValueString())
    }

    @Test
    void testSetNotTrimmedValue() throws Exception {
        Preference preference = new Preference()
        PreferenceController preferenceController = initPreferenceController(preference)

        preferenceController.setValue("testKey", false, "  testValue  ")
        assertEquals("testValue", preference.getValueString())
    }

    private PreferenceController initPreferenceController(Preference preference) {
        ICayenneService iCayenneService = Mockito.mock(ICayenneService.class)

        DataContext dataContext = Mockito.mock(DataContext.class)
        when(iCayenneService.getNewContext()).thenReturn(dataContext)

        when(dataContext.selectFirst(any())).thenReturn(preference)
        when(dataContext.localObject(any())).thenReturn(preference)

        ISystemUserService systemUserService = Mockito.mock(ISystemUserService.class)
        LicenseService licenseService = Mockito.mock(LicenseService.class)

        return new PreferenceController(iCayenneService, systemUserService, licenseService)
    }
}