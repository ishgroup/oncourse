package ish.oncourse.willow.service

import com.google.inject.*
import org.junit.Assert
import org.junit.Test

class GuiceScopeTest {

    @Test
    void test() {

        Injector injector = Guice.createInjector(new TestModule())

        Service1Impl i1 = injector.getInstance(Service1Impl)
        Service1Impl i2 = injector.getInstance(Service1Impl)

        Assert.assertTrue(i1 == i2)
    }


    static class TestModule implements Module {
        @Override
        void configure(Binder binder) {
            binder.bind(Service1Impl).in(Singleton.class)
        }
    }


    static interface Service1 {
        void method()
    }

    static class Service1Impl implements Service1 {
        @Override
        void method() {
            println("Service1Impl.method")
        }
    }
}
