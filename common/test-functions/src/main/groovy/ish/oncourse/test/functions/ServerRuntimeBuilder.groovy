package ish.oncourse.test.functions

import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.JCacheModule
import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.configuration.server.ServerRuntime

/**
 * User: akoiro
 * Date: 9/12/17
 */
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
        if (queryCache == null) {
            return new ServerRuntime(cayenneXml, new WillowCayenneModuleBuilder().queryCache().build(), new JCacheModule())
        } else {
            return new ServerRuntime(cayenneXml, new WillowCayenneModuleBuilder().queryCache(queryCache).build())
        }
    }
}
