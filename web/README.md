#Cache
We have two types of cache

1. website template cache - that is query cache that has cache groups per web site. It has no expiry settings and we invalidate it only on website publish using ZK watcher

see ish.oncourse.website.cache.CacheInvalidationService


2. other cayenne object cache, which is regular object cache. Works for courses, classes and other site content entities

We have followed config

TTL == 10 min

cache size = 10000 entries

see  ish.oncourse.cache.caffeine.CaffeineFactory