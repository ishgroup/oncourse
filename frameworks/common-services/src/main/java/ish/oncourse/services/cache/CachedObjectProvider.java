package ish.oncourse.services.cache;

/**
 * A factory object that is asked to create an object if it is missing or
 * expired from the cache.
 */
public interface CachedObjectProvider<T> {

	T create();
}
