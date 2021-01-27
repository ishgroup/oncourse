package ish.oncourse.api.jetty

import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import io.bootique.ConfigModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedServlet
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.util.log.LogAppInfo

class HealthCheckModule extends ConfigModule {

    private static final TypeLiteral<MappedServlet<ISHHealthCheckServlet>> ISH_HEALTH_CHECK_SERVLET =
            new TypeLiteral<MappedServlet<ISHHealthCheckServlet>>() {}


    @Override
    void configure(Binder binder) {
        JettyModule.extend(binder).addMappedServlet(ISH_HEALTH_CHECK_SERVLET)
    }
    
    @Singleton
    @Provides
    MappedServlet<ISHHealthCheckServlet> createHealthCheckServlet(Injector injector) {
        LogAppInfo info = new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(LogAppInfo.DATA_SOURSE_NAME))
        info.log()
        new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME)
    }
}
