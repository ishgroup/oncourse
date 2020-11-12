package ish.oncourse.server.security.api

import io.bootique.BQRuntime
import io.bootique.test.junit.BQTestFactory
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

import javax.ws.rs.HttpMethod

class PermissionTest {

    private static final String CONFIG_PATH = "--config=classpath:permissionTest.yml"

    private IPermissionService permissionService

    @ClassRule
    public static BQTestFactory testFactory = new BQTestFactory()

    @Before
    void before() {
        BQRuntime runtime = testFactory.app(CONFIG_PATH)
                .module(PermissionModule.class)
                .module(MockModule.class)
                .createRuntime()

        permissionService = runtime.getInstance(IPermissionService.class)
    }

    @Test
    void testHasAccess() {
        assertTrue(permissionService.hasAccess("/a/v1/test", HttpMethod.GET).isSuccessful())
        assertTrue(permissionService.hasAccess("/a/v1/test", HttpMethod.POST).isSuccessful())
        assertFalse(permissionService.hasAccess("/a/v1/test", HttpMethod.PUT).isSuccessful())
    }

    @Test
    void testHasAccessToList() {
        assertTrue(permissionService.hasAccess("/a/v1/test/list?entity=qualification", HttpMethod.GET).isSuccessful())
    }

    @Test
    void testHasNoAccessToList() {
        assertFalse(permissionService.hasAccess("/a/v1/test/list?otherParam=aaa", HttpMethod.GET).isSuccessful())
    }
}
