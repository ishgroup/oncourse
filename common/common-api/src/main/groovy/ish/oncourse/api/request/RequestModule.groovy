package ish.oncourse.api.request

import com.google.inject.Binder
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import io.bootique.ConfigModule
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter 

class RequestModule extends ConfigModule {

    private static final String ROOT_URL_PATTERN = '/*'

    private static final TypeLiteral<MappedFilter<RequestFilter>> REQUEST_FILTER =
            new TypeLiteral<MappedFilter<RequestFilter>>() {
            }
    @Override
    void configure(Binder binder) {
        JettyModule.extend(binder).addMappedFilter(REQUEST_FILTER)
        binder.bind(RequestService)
    }


    @Singleton
    @Provides
    MappedFilter<RequestFilter> createRequestFilter() {
        new MappedFilter<RequestFilter>(new RequestFilter(),
                Collections.singleton(ROOT_URL_PATTERN), RequestFilter.simpleName, 0)
    }

}
