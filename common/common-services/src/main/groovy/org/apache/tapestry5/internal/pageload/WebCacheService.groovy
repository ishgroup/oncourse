package org.apache.tapestry5.internal.pageload

import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.tapestry5.internal.parser.ComponentTemplate
import org.apache.tapestry5.internal.structure.Page
import org.apache.tapestry5.internal.util.MultiKey
import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.internal.util.CollectionFactory

import java.util.concurrent.Callable

class WebCacheService {
    private static final String TEMPLATE_RESOURCES_CACHE_KEY = 'templateResources'
    private static final String TEMPLATES_CACHE_KEY ='templates'
    private static final String COMPONENT_ASSEMBLERS_CACHE_KEY = 'componentAssemblers'
    private static final String PAGES_CACHE_KEY = 'pages'

    private static final Logger logger = LogManager.getLogger();

    @Inject
    private CacheManager cacheManager

    /**
     * Caches from a key (combining component name and locale) to a resource.
     * Often, many different keys will point to the same resource (i.e.,
     * "foo:en_US", "foo:en_UK", and "foo:en" may all be parsed from the same
     * "foo.tml" resource). The resource may end up being null, meaning the
     * template does not exist in any locale.
     */
    Resource getTemplateResource(String cacheKey, MultiKey key, Callable<Resource> get) {
        getCachedValue(TEMPLATE_RESOURCES_CACHE_KEY, cacheKey, key, get)
    }

    /**
     * Cache of parsed templates, keyed on resource.
     */
    ComponentTemplate getTemplate(String cacheKey, Resource resource, Callable<ComponentTemplate> get) {
        getCachedValue(TEMPLATES_CACHE_KEY, cacheKey, resource, get)
    }

    ComponentAssembler getComponentAssembler(String cacheKey, MultiKey key, Callable<ComponentAssembler> get) {
        getCachedValue(COMPONENT_ASSEMBLERS_CACHE_KEY, cacheKey, key, get)
    }

    Page getPage(String cacheKey, MultiKey key, Callable<Page> get) {
        getCachedValue(PAGES_CACHE_KEY, cacheKey, key, get)
    }
    
    
    def <K, V> V getCachedValue(String cacheGroup, String webSiteCacheKey, K key, Callable<V> get) {
        if (!webSiteCacheKey) {
           return get.call()
        }
        try {
            Cache cache = cacheManager.getCache(cacheGroup)
            Element element = cache.get(webSiteCacheKey)
            Map<K,V> siteMap = element?.objectValue as Map<K,V>?:CollectionFactory.newConcurrentMap()
            V value = siteMap.get(key)
    
            if (value == null) {
                value = get.call()
                if (value != null) {
                    siteMap.put(key, value)
                }
                cache.put(new Element(webSiteCacheKey, siteMap))
            }
            return value
        } catch (Exception e) {
            logger.error("Exception appeared during reading cached value. cacheGroup $cacheGroup, webSiteCacheKey: $webSiteCacheKey, key: $key")
            logger.catching(e)
            return get.call()
        }
    }
    
    void cleanTemplatesCache(String cacheKey) {
        cacheManager.getCache(TEMPLATES_CACHE_KEY).put(new Element(cacheKey,CollectionFactory.newConcurrentMap()))
        cacheManager.getCache(TEMPLATE_RESOURCES_CACHE_KEY).put(new Element(cacheKey,CollectionFactory.newConcurrentMap()))
        cacheManager.getCache(COMPONENT_ASSEMBLERS_CACHE_KEY).put(new Element(cacheKey,CollectionFactory.newConcurrentMap()))
        cacheManager.getCache(PAGES_CACHE_KEY).put(new Element(cacheKey,CollectionFactory.newConcurrentMap()))
    }
    
}
