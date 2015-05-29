package ish.oncourse.services.cache;

import org.apache.tapestry5.ioc.MethodAdviceReceiver;

/**
 * The service allows to add some attributes to current request.
 * We use the service to cache results of some service's getters in current request.
 * The solution allows performing java code of these methods once per request.
 *
 * To activate the feautre for a get method of some services we should use RequestCached annotation for this method.
 *
 * @see ish.oncourse.services.cache.RequestCached
 */
public interface IRequestCacheService {
    <V> Value<V> getFromRequest(Class<V> vClass, String key);
    <V> void putToRequest(String key, V value);
    void applyRequestCachedAdvice(final MethodAdviceReceiver receiver);
}
