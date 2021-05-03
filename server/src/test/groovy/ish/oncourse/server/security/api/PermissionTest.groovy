package ish.oncourse.server.security.api

import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import io.bootique.test.junit.BQTestFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.ws.rs.HttpMethod

@CompileStatic
class PermissionTest {

    private static final String CONFIG_PATH = "--config=classpath:permissionTest.yml"

    private IPermissionService permissionService

    public static BQTestFactory testFactory = new BQTestFactory()


    @BeforeEach
    void before() {
        BQRuntime runtime = testFactory.app(CONFIG_PATH)
                .module(PermissionModule.class)
                .module(MockModule.class)
                .createRuntime()

        permissionService = runtime.getInstance(IPermissionService.class)
    }

    
    @Test
    void testHasAccess() {
        Assertions.assertTrue(permissionService.hasAccess("/a/v1/test", HttpMethod.GET).isSuccessful())
        Assertions.assertTrue(permissionService.hasAccess("/a/v1/test", HttpMethod.POST).isSuccessful())
        Assertions.assertFalse(permissionService.hasAccess("/a/v1/test", HttpMethod.PUT).isSuccessful())
    }

    
    @Test
    void testHasAccessToList() {
        Assertions.assertTrue(permissionService.hasAccess("/a/v1/test/list?entity=qualification", HttpMethod.GET).isSuccessful())
    }

    
    @Test
    void testHasNoAccessToList() {
        Assertions.assertFalse(permissionService.hasAccess("/a/v1/test/list?otherParam=aaa", HttpMethod.GET).isSuccessful())
    }
}
