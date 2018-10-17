package ish.oncourse.log4j;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import ish.oncourse.log4j.lookup.XSiteKeySaverFilter;

import java.util.Collections;

/**
 * Bootique module which extends default Log4j2 logging possibilities
 */
public class IshLog4jModule implements Module {

    private static final String URL_PATTERN_ALL = "/*";
    private static final int REQUEST_SAVER_FILTER_ORDER = 0;

    @Override
    public void configure(Binder binder) {
        MappedFilter<XSiteKeySaverFilter> filter = new MappedFilter<>(
                new XSiteKeySaverFilter(),
                Collections.singleton(URL_PATTERN_ALL),
                REQUEST_SAVER_FILTER_ORDER
        );
        JettyModule.extend(binder)
                .addMappedFilter(filter);
    }
}
