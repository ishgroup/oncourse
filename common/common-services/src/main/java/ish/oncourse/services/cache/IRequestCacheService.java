package ish.oncourse.services.cache;

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
    public <V> V getFromRequest(Class<V> vClass, String key);
    public <V> void putToRequest(String key, V value);
}
