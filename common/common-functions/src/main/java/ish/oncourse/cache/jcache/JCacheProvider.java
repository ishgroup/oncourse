/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.jcache;

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.ICacheProvider;
import ish.oncourse.cayenne.cache.JCacheConstants;
import org.apache.cayenne.configuration.RuntimeProperties;
import org.apache.cayenne.di.DIRuntimeException;
import org.apache.cayenne.di.Inject;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * User: akoiro
 * Date: 3/4/18
 */
public class JCacheProvider implements ICacheProvider {
	protected final CacheManager cacheManager;

	public JCacheProvider(@Inject RuntimeProperties properties) {
		cacheManager = initCacheManager(properties);
	}

	private static CacheManager initCacheManager(RuntimeProperties properties) {
		CachingProvider provider;
		try {
			provider = Caching.getCachingProvider(CaffeineCachingProvider.class.getName());
		} catch (CacheException e) {
			throw new DIRuntimeException("'cayenne-jcache' doesn't bundle any JCache providers. " +
					"You must place a JCache 1.0 provider on classpath explicitly.", e);
		}

		URI jcacheConfig = getConfig(properties);
		return jcacheConfig != null ? provider.getCacheManager(jcacheConfig, null) : provider.getCacheManager();
	}

	private static URI getConfig(RuntimeProperties properties) {
		String config = properties.get(JCacheConstants.JCACHE_PROVIDER_CONFIG);
		try {
			if (config != null) {
				return new URI(config);
			} else {
				URL url = JCacheProvider.class.getClassLoader().getResource("ehcache.xml");
				return url != null ? url.toURI() : null;
			}

		} catch (URISyntaxException ex) {
			throw new DIRuntimeException("Wrong value for JCache provider config property", ex);
		}
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	public <K, V> ICacheFactory<K, V> createFactory(Class<K> keyType, Class<V> valueType) {
		return new JCacheFactory<>(cacheManager, keyType, valueType);
	}
}
