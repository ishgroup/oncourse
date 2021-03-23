package ish.oncourse.test.functions

import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.JCacheModule
import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.configuration.server.ServerRuntime

class ServerRuntimeBuilder {
    private String cayenneXml = "cayenne-oncourse.xml"
    private QueryCache queryCache = null

    ServerRuntimeBuilder cayenneXml(String cayenneXml) {
        this.cayenneXml = cayenneXml
        return this
    }

    ServerRuntimeBuilder queryCache(QueryCache queryCache) {
        this.queryCache = queryCache
        return this
    }

    ServerRuntime build() {
        def runtimeBuilder = ServerRuntime.builder().addConfig(cayenneXml)
        if (queryCache == null) {
            runtimeBuilder.addModule(new WillowCayenneModuleBuilder().queryCache().build()).addModule(new JCacheModule())
        } else {
            runtimeBuilder.addModule(new WillowCayenneModuleBuilder().queryCache(queryCache).build())
        }
        runtimeBuilder.build()
    }
}
