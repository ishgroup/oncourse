package ish.oncourse.services.cache;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class RequestCacheService implements IRequestCacheService {

    @Inject
    private Request request;

    public <V> V getFromRequest(Class<V> vClass, String key)
    {
        Object value = request.getAttribute(key);
        if (value != null && vClass.isAssignableFrom(value.getClass()))
            return (V) value;
        else
            return null;
    }

    public <V> void putToRequest(String key, V value)
    {
        request.setAttribute(key, value);
    }

}
