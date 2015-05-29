package ish.oncourse.services.cache;

import org.apache.tapestry5.ioc.Invocation;
import org.apache.tapestry5.ioc.MethodAdvice;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.lang.reflect.Method;

public class RequestCacheService implements IRequestCacheService {

    @Inject
    private Request request;

    public <V> Value<V> getFromRequest(Class<V> vClass, String key) {
        return  (Value<V>) request.getAttribute(key);
    }

    public <V> void putToRequest(String key, V value) {
        request.setAttribute(key, Value.valueOf(value));
    }


    public void applyRequestCachedAdvice(final MethodAdviceReceiver receiver) {
        MethodAdvice advice = new MethodAdvice()
        {
            public void advise(Invocation invocation)
            {
                String key = receiver.getInterface().getName() + '.' + invocation.getMethodName();

                try {
                    Value result = RequestCacheService.this.getFromRequest(invocation.getResultType(), key);
                    if (result == null) {
                        invocation.proceed();
                        RequestCacheService.this.putToRequest(key, invocation.getResult());
                    }
                    else {
                        invocation.overrideResult(result.getValue());
                    }
                } catch (Exception e) {
                    invocation.proceed();
                    RequestCacheService.this.putToRequest(key, invocation.getResult());
                }
            }
        };

        for (Method m : receiver.getInterface().getMethods())
        {
            if (m.getAnnotation(RequestCached.class) != null)
                receiver.adviseMethod(m, advice);
        }
    }

}
