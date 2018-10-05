/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.ConfigModule;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Properties;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;

public class SolrClientTestModule extends ConfigModule {

    @Provides
    @Singleton()
    public SolrClient createSolrClient() {
        return org.mockito.Mockito.mock(SolrClient.class);
    }

    @Provides
    @Singleton()
    public ZooKeeper createZooKeeper() throws IOException {
        return  org.mockito.Mockito.mock(ZooKeeper.class);
    }

}
